package pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.managers;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.ReservationEditDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.*;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.*;
import pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades.*;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.comparators.ReservationEditComparator;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.converters.ReservationConverter;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.converters.ReservationEditConverter;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.EjbLoggerInterceptor;

import javax.annotation.Resource;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Klasa do obsługi rezerwacji
 */
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@DenyAll
@Interceptors(EjbLoggerInterceptor.class)
public class ReservationManager extends AbstractMooManager implements ReservationManagerLocal {

    @Inject
    private ClientEntityMooFacade clientEntityMooFacade;
    @Inject
    private UserEntityMooFacade userEntityMooFacade;

    @Inject
    private ReservationEntityMooFacade reservationFacade;

    @Resource
    private SessionContext ctx;

    @Inject
    private ClientEntityMooFacade clientFacade;

    @Inject
    private EntertainerEntityMooFacade entertainerFacade;

    @Inject
    private OfferEntityMooFacade offerFacade;

    /** Pobiera wszystkie rezerwacja, dostępne role "Management"
     * @return listę wszystkich rezerwacji
     * @throws AbstractAppException
     */
    @Override
    @RolesAllowed({"Management"})
    public List<ReservationEntity> getAllReservations() throws AbstractAppException {
        return reservationFacade.findAllAndRefresh();
    }

    /** Zwraca rezerwację zalogowanego klienta, dostępne role "Client"
     * @return listę rezerwacji klienta
     * @throws AbstractAppException
     */
    @Override
    @RolesAllowed({"Client"})
    public List<ReservationEntity> getSelfReservations() throws AbstractAppException {
        var userEntity = userEntityMooFacade.findByLogin(ctx.getCallerPrincipal().getName());
        var clientEntity = clientEntityMooFacade.findByUser(userEntity);
        return reservationFacade.findReservationByClient(clientEntity);
    }

    /** Zwraca pojedyńczą rezerwację po id, dostępne role: wszystkie
     * @param id id rezerwacji do wyświetlenia
     * @return rezerwację o podanym id
     * @throws AbstractAppException
     */
    @Override
    @PermitAll
    public ReservationEntity getReservation(Long id) throws AbstractAppException {
        var reservationEntity = reservationFacade.findAndRefresh(id);
        if (reservationEntity == null) {
            throw ReservationAppException.createReservationNotFoundAppException(id);
        }
        return reservationEntity;
    }
    @Override
    @RolesAllowed({"Entertainer"})
    public ReservationEntity endReservation(Long id) throws AbstractAppException {
        EntertainerEntity myself = entertainerFacade.findByLogin(ctx.getCallerPrincipal().getName());
        ReservationEntity reservation = reservationFacade.findForEntertainer(id, myself.getId());
        if (!reservation.getStatus().equals(ReservationEntity.Status.ACCEPTED))
            throw ReservationAppException.createReservationNotAcceptedAppException(id);
        reservation.setStatus(ReservationEntity.Status.ENDED);
        reservationFacade.edit(reservation);
        reservationFacade.flush();
        return reservation;
    }


    /** Tworzy nową rezerwację, dostępne role: "Client"
     * @param reservation szczegóły rezerwacji
     * @param offerId id oferty której tyczy się rezerwacja
     * @param offerVersion wersja oferty
     * @return
     * @throws AbstractAppException
     */
    @Override
    @RolesAllowed({"Client"})
    public ReservationEntity createReservation(ReservationEntity reservation, Long offerId, Long offerVersion) throws AbstractAppException {
        reservationAssignment(reservation, offerId, offerVersion, ctx.getCallerPrincipal().getName());
        entertainerFacade.lockPessimisticRead(reservation.getOffer().getEntertainer());
        if (!validateOfferAvailability(reservation.getOffer())) {
            throw OfferException.createOfferNotAvailableException(offerId);
        }
        if (!validateReservationTerm(reservation))
            throw UniqueConstraintAppException.createWrongTermsException();
        reservationFacade.create(reservation);
        reservationFacade.flush();
        return reservation;
    }

    private boolean validateOfferAvailability(OfferEntity offer) {
        boolean validationResult = offer.getEntertainer().isActive();
        validationResult &= offer.getEntertainer().getUser().isActive();
        validationResult &= offer.isActive();
        return validationResult;
    }

    private void reservationAssignment(ReservationEntity reservation, Long offerId, Long offerVersion, String clientLogin) throws AbstractAppException {
        OfferEntity offer = offerFacade.find(offerId);
        offer.setVersion(offerVersion);
        reservation.setOffer(offer);
        ClientEntity byLogin = clientFacade.findByLogin(clientLogin);
        reservation.setClient(byLogin);
    }

    private boolean validateReservationTerm(ReservationEntity reservation) {

        OfferEntity offer = reservation.getOffer();
        EntertainerEntity entertainer = offer.getEntertainer();

        boolean validationResult = reservation.getReservationTo().getDayOfYear() == reservation.getReservationFrom().getDayOfYear();
        validationResult &= reservation.getReservationTo().getYear() == reservation.getReservationFrom().getYear();

        Collection<OfferAvailabilityEntity> offerAvailabilities = offer.getOfferAvailabilities();
        validationResult &= offerAvailabilities.stream().anyMatch((x) -> offerAvailabilityValidation(x, reservation));

        List<EntertainerUnavailabilityEntity> unavailabilityList = entertainer.getEntertainerUnavailability()
                .stream()
                .filter(EntertainerUnavailabilityEntity::isValid)
                .collect(Collectors.toList());
        validationResult &= unavailabilityList
                .stream()
                .noneMatch(unavailability ->
                        termsOverlap(unavailability.getDateTimeFrom(), unavailability.getDateTimeTo(),
                                reservation.getReservationFrom(), reservation.getReservationTo())
                );

        validationResult &= reservation.getReservationFrom().isAfter(offer.getValidFrom());
        validationResult &= reservation.getReservationTo().isBefore(offer.getValidTo());

        List<ReservationEntity> allForEntertainer = reservationFacade.findAllForEntertainer(entertainer.getId());
        return validationResult && allForEntertainer
                .stream()
                .filter(rsv -> rsv.getStatus().equals(ReservationEntity.Status.PENDING) || rsv.getStatus().equals(ReservationEntity.Status.ACCEPTED))
                .noneMatch(
                        rsv -> termsOverlap(
                                rsv.getReservationFrom(),
                                rsv.getReservationTo(),
                                reservation.getReservationFrom(),
                                reservation.getReservationTo()));
    }

    private boolean termsOverlap(OffsetDateTime firstBegin, OffsetDateTime firstEnds, OffsetDateTime secondBegin, OffsetDateTime secondEnds) {
        return firstBegin.isBefore(secondEnds) && secondBegin.isBefore(firstEnds);
    }

    private static boolean offerAvailabilityValidation(OfferAvailabilityEntity offerAvailability, ReservationEntity reservation) {
        boolean validation = offerAvailability.getHoursFrom().withOffsetSameInstant(ZoneOffset.UTC).getHour() <= reservation.getReservationFrom().withOffsetSameInstant(ZoneOffset.UTC).getHour();
        validation &= offerAvailability.getHoursTo().withOffsetSameInstant(ZoneOffset.UTC).getHour() >= reservation.getReservationTo().withOffsetSameInstant(ZoneOffset.UTC).getHour();
        validation &= offerAvailability.getWeekDay() == reservation.getReservationTo().getDayOfWeek().getValue() % 7;
        validation &= offerAvailability.getWeekDay() == reservation.getReservationFrom().getDayOfWeek().getValue() % 7;
        return validation;
    }

    private static boolean offerAvailabilityValidation(OfferAvailabilityEntity offerAvailability, ReservationEditDTO reservation) {
        boolean validation = offerAvailability.getHoursFrom().withOffsetSameInstant(ZoneOffset.UTC).getHour() <= reservation.getReservationFrom().withOffsetSameInstant(ZoneOffset.UTC).getHour();
        validation &= offerAvailability.getHoursTo().withOffsetSameInstant(ZoneOffset.UTC).getHour() >= reservation.getReservationTo().withOffsetSameInstant(ZoneOffset.UTC).getHour();
        validation &= offerAvailability.getWeekDay() == reservation.getReservationTo().getDayOfWeek().getValue() % 7;
        validation &= offerAvailability.getWeekDay() == reservation.getReservationFrom().getDayOfWeek().getValue() % 7;
        return validation;
    }

//    TODO: javadoc
    @Override
    public ReservationEntity updateReservation(Long id, ReservationEntity newReservation) {
        throw new UnsupportedOperationException();
    }


    /** Akceptacja rezerwacji przez PDR, dostępne role: "Entertainer"
     * @param id id rezerwacji
     * @return Zaakceptowaną rezerwację
     * @throws AbstractAppException
     */
    @Override
    @RolesAllowed({"Entertainer"})
    public ReservationEntity acceptReservation(Long id) throws AbstractAppException {
        EntertainerEntity myself = entertainerFacade.findByLogin(ctx.getCallerPrincipal().getName());
        ReservationEntity reservation = reservationFacade.findForEntertainer(id, myself.getId());
        if (!reservation.getStatus().equals(ReservationEntity.Status.PENDING))
            throw ReservationAppException.createReservationNotActualAppException(id);
        reservation.setStatus(ReservationEntity.Status.ACCEPTED);
        reservationFacade.edit(reservation);
        reservationFacade.flush();
        return reservation;
    }


    /**
     * Metoda managera pozwalająca na modyfikację oceny i komentarza do danej rezerwacji
     *
     * @param id - identyfikator rezerwacji
     * @param rating - ocena
     * @param comment - komentarz
     * @return encja rezerwacji po zmianie
     * @throws ReservationNotFoundAppException - wyjątek rzucany w przypadku nie znalezienia rezerwacji po danym identyfikatorze
     * @throws NotAllowedAppException - wyjątek rzucany w przypadku braku uprawnień
     */
    @Override
    @RolesAllowed({"Client"})
    public ReservationEntity updateRating(Long id, int rating, String comment) throws AbstractAppException {
        ReservationEntity reservationEntity = reservationFacade.findAndRefresh(id);
        if (reservationEntity == null) {
            throw ReservationNotFoundAppException.updateRatingOfReservationWithProvidedIdNotFoundException(id);
        }

        if (!reservationEntity.getClient().getUser().getLogin().equals(ctx.getCallerPrincipal().getName())) {
            throw NotAllowedAppException.createNotAllowedException();
        }

        reservationEntity.setRating(rating);
        reservationEntity.setComment(comment);
        calculateRatings(reservationEntity);

        reservationFacade.flush();

        return reservationFacade.findAndRefresh(id);
    }

    /**
     * Metoda managera pozwalająca na usunięcie oceny i komentarza przez użytkownika z poziomem dostępu Client, do danej rezerwacji
     *
     * @param id - identyfikator rezerwacji
     * @return encja rezerwacji po zmianie
     * @throws ReservationNotFoundAppException - wyjątek rzucany w przypadku nie znalezienia rezerwacji po danym identyfikatorze
     * @throws NotAllowedAppException - wyjątek rzucany w przypadku braku uprawnień
     */
    @Override
    @RolesAllowed({"Client"})
    public ReservationEntity deleteRatingByClient(Long id) throws AbstractAppException {
        ReservationEntity reservationEntity = reservationFacade.findAndRefresh(id);
        if (reservationEntity == null) {
            throw ReservationNotFoundAppException.updateRatingOfReservationWithProvidedIdNotFoundException(id);
        }

        if (!reservationEntity.getClient().getUser().getLogin().equals(ctx.getCallerPrincipal().getName())) {
            throw NotAllowedAppException.createNotAllowedException();
        }

        reservationEntity.setRating(null);
        reservationEntity.setComment(null);
        calculateRatings(reservationEntity);

        reservationFacade.flush();

        return reservationEntity;
    }

    /**
     * Metoda managera pozwalająca na usunięcie oceny i komentarza przez użytkownika z poziomem dostępu Management, do danej rezerwacji
     *
     * @param id - identyfikator rezerwacji
     * @return encja rezerwacji po zmianie
     * @throws ReservationNotFoundAppException - wyjątek rzucany w przypadku nie znalezienia rezerwacji po danym identyfikatorze
     * @throws NotAllowedAppException - wyjątek rzucany w przypadku braku uprawnień
     */
    @Override
    @RolesAllowed({"Management"})
    public ReservationEntity deleteRatingByManagement(Long id) throws AbstractAppException {
        ReservationEntity reservationEntity = reservationFacade.findAndRefresh(id);
        if (reservationEntity == null) {
            throw ReservationNotFoundAppException.updateRatingOfReservationWithProvidedIdNotFoundException(id);
        }

        reservationEntity.setRating(null);
        reservationEntity.setComment(null);
        calculateRatings(reservationEntity);

        reservationFacade.flush();

        return reservationEntity;
    }

    private void calculateRatings(ReservationEntity reservationEntity) throws AbstractAppException {
        var offer = reservationEntity.getOffer();
        var entertainer = offer.getEntertainer();

        var averageOffer = offer.getReservations().stream()
                .map(ReservationEntity::getRating)
                .filter(Objects::nonNull)
                .mapToDouble(Double::valueOf)
                .average();
        offer.setAvgRating(averageOffer.isPresent() ? averageOffer.getAsDouble() : null);

        var averageEntertainer = reservationFacade.findAllForEntertainer(entertainer.getId()).stream()
                .map(ReservationEntity::getRating)
                .filter(Objects::nonNull)
                .mapToDouble(Double::valueOf)
                .average();
        entertainer.setAvgRating(averageEntertainer.isPresent() ? averageEntertainer.getAsDouble() : null);
    }

    /** Zwraca wszystkie rezerwacje zalogowanego PDR, dostępne role: "Entertainer"
     * @return liste rezerwacji pdr
     */
    @Override
    @RolesAllowed({"Entertainer"})
    public List<ReservationEntity> getAllReservationsForEntertainer() {
        EntertainerEntity myself = entertainerFacade.findByLogin(ctx.getCallerPrincipal().getName());
        return reservationFacade.findAllForEntertainer(myself.getId());
    }


    /** Edycja rezerwacji przez klienta, dostępne role: "Client"
     * @param id id rezerwacji
     * @param reservationEditDTO szczególy zmian rezerwacji
     * @return zmienioną rezerwację
     * @throws AbstractAppException
     */
    @Override
    @RolesAllowed({"Client"})
    public ReservationEntity editReservation(Long id, ReservationEditDTO reservationEditDTO) throws AbstractAppException {
        ReservationEntity reservationEntity = reservationFacade.findAndRefresh(id);

        if (reservationEntity == null) {
            throw ReservationNotFoundException.createReservationWithProvidedIdNotFoundException(id);
        }
        var userEntity = reservationEntity.getClient().getUser();
        if (!userEntity.getLogin().equals(ctx.getCallerPrincipal().getName()))
            throw NotAllowedAppException.createNotAllowedException();

        var now = OffsetDateTime.now();
        if (
                reservationEntity.getStatus().equals(ReservationEntity.Status.ENDED) ||
                        reservationEntity.getStatus().equals(ReservationEntity.Status.CANCELED)
        ) {
            throw ReservationAppException.createReservationCannotBeEditedAppException(reservationEntity.getId());
        }
        ReservationEditDTO orginalReservationEditDTO = ReservationEditConverter.reservationEntityToDTO(reservationEntity);
        boolean notChanged = ReservationEditComparator.compareData(orginalReservationEditDTO, reservationEditDTO);

        if (notChanged) {
            return reservationEntity;
        }
        try {
            ReservationEntity newReservationEntity = ReservationConverter.createEntityFromDto(ReservationConverter.reservationEntityToDTO(reservationEntity));
            newReservationEntity.setReservationTo(reservationEditDTO.getReservationTo());
            newReservationEntity.setReservationFrom(reservationEditDTO.getReservationFrom());
            var offerEntity = offerFacade.findAndRefresh(reservationEntity.getOffer().getId());
            var x = createReservation(newReservationEntity, offerEntity.getId(), offerEntity.getVersion());
            reservationEntity.setStatus(ReservationEntity.Status.CANCELED);
            return x;
        } catch (AbstractAppException abstractAppException) {
            throw abstractAppException;
        }
        /*System.out.println(reservationEditDTO == null ? "reservationEditDTO null" : reservationEditDTO.toString());
        System.out.println(reservationEditDTO.getReservationTo() == null ? " getReservationTonull" : reservationEditDTO.getReservationTo().toString());
        System.out.println(reservationEditDTO.getReservationFrom() == null ? " getReservationFromnull" : reservationEditDTO.getReservationFrom().toString());

        var offerEntity = reservationEntity.getOffer();
        var offerValidFrom = offerEntity.getValidFrom();
        var offerValidTo = offerEntity.getValidTo();
        var reservationFrom = reservationEditDTO.getReservationFrom();
        var reservationTo = reservationEditDTO.getReservationTo();
        var currentDateTime = OffsetDateTime.now();
        boolean validNewDateCurrent = reservationFrom.isAfter(currentDateTime);
        boolean validNewDateFrom =
                (reservationFrom.isAfter(offerValidFrom) || reservationFrom.isEqual(offerValidFrom));
        boolean validNewDateFromTo =
                (reservationFrom.isBefore(reservationTo) || reservationFrom.isEqual(reservationTo));
        boolean validNewDateTo =
                (reservationTo.isBefore(offerValidTo) || reservationTo.isEqual(offerValidTo));
        boolean validNewDate = validNewDateFrom && validNewDateFromTo && validNewDateTo && validNewDateCurrent;
        if (!validNewDate) {
            throw ReservationAppException.createReservationNotAcceptedAppException(reservationEntity.getId());
        }
        boolean validationResult = reservationEditDTO.getReservationTo().getDayOfYear() == reservationEditDTO.getReservationFrom().getDayOfYear();
        validationResult &= reservationEditDTO.getReservationTo().getYear() == reservationEditDTO.getReservationFrom().getYear();

        Collection<OfferAvailabilityEntity> offerAvailabilities = reservationEntity.getOffer().getOfferAvailabilities();
        validationResult &= offerAvailabilities.stream().anyMatch((x) -> offerAvailabilityValidation(x, reservationEditDTO));

        List<EntertainerUnavailabilityEntity> unavailabilityList = reservationEntity.getOffer().getEntertainer().getEntertainerUnavailability()
                .stream()
                .filter(EntertainerUnavailabilityEntity::isValid)
                .collect(Collectors.toList());
        validationResult &= unavailabilityList
                .stream()
                .noneMatch(unavailability ->
                        termsOverlap(unavailability.getDateTimeFrom(), unavailability.getDateTimeTo(),
                                reservationEditDTO.getReservationFrom(), reservationEditDTO.getReservationTo())
                );

        validationResult &= reservationEditDTO.getReservationFrom().isAfter(reservationEntity.getOffer().getValidFrom());
        validationResult &= reservationEditDTO.getReservationTo().isBefore(reservationEntity.getOffer().getValidTo());

        if (validationResult) {
            throw ReservationAppException.createReservationCannotBeEditedAppException(reservationEntity.getId());
        }

        reservationEntity.setStatus(ReservationEntity.Status.PENDING);//Oczekujemy ponownego potwierdzenia
        reservationEntity.setReservationFrom(reservationEditDTO.getReservationFrom());
        reservationEntity.setReservationTo(reservationEditDTO.getReservationTo());
        reservationFacade.flush();
        return reservationFacade.findAndRefresh(id);*/
    }

    /** Wycofywanie rezerwacji przez klienta, dostępne role: "Client"
     * @param id id rezerwacji
     * @return zwraca wycofaną rezerwację
     * @throws AbstractAppException
     */
    @Override
    @RolesAllowed("Client")
    public ReservationEntity clientCancelReservation(Long id) throws AbstractAppException {
        var clientEntity = clientEntityMooFacade.findByLogin(ctx.getCallerPrincipal().getName());

        var reservationEntity = reservationFacade.findAndRefresh(id);
        if (reservationEntity.getClient().getId() != clientEntity.getId()) {
            throw new NotAllowedAppException("Not a client specified in reservation");
        }
        if (reservationEntity.getStatus().equals(ReservationEntity.Status.ENDED)) {
            throw new ReservationUncancellableAppException();
        }
        reservationEntity.setStatus(ReservationEntity.Status.CANCELED);
        reservationFacade.flush();
        return reservationEntity;
    }


    /** Wycofywanie rezerwacji przez PDR, dostępne role: "Entertainer"
     * @param id id rezerwacji
     * @return Wycofaną rezerwację
     * @throws AbstractAppException
     */
    @Override
    @RolesAllowed("Entertainer")
    public ReservationEntity entertainerCancelReservation(Long id) throws AbstractAppException {
        var entertainerEntity = entertainerFacade.findByLogin(ctx.getCallerPrincipal().getName());

        var reservationEntity = reservationFacade.findAndRefresh(id);
        if (reservationEntity.getOffer().getEntertainer().getId() != entertainerEntity.getId()) {
            throw new NotAllowedAppException("Not a entertainer specified in reservation");
        }
        if (reservationEntity.getStatus().equals(ReservationEntity.Status.ENDED)) {
            throw new ReservationUncancellableAppException();
        }
        reservationEntity.setStatus(ReservationEntity.Status.CANCELED);
        reservationFacade.flush();
        return reservationEntity;
    }
}
