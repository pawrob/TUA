package pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.managers;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.OfferEditDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.OfferToCreateDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.EntertainerEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.OfferEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.*;
import pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades.ClientEntityMooFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades.EntertainerEntityMooFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades.OfferEntityMooFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades.UserEntityMooFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.comparators.OfferEditComparator;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.converters.OfferConverter;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.converters.OfferEditConverter;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa do obsługi ofert
 */
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@DenyAll
@Interceptors(EjbLoggerInterceptor.class)
public class OfferManager extends AbstractMooManager implements OfferManagerLocal {

    @Inject
    private OfferEntityMooFacade offerEntityFacade;

    @Inject
    private ClientEntityMooFacade clientEntityMooFacade;

    @Inject
    private UserEntityMooFacade userEntityMooFacade;

    @Inject
    private EntertainerEntityMooFacade entertainerEntityMooFacade;

    @Resource
    private SessionContext ctx;


    /**
     * Zwraca wszystkie oferty, dozwolone role: "Entertainer", "Management"
     * @return Liste ofert
     * @throws AbstractAppException
     */
    @Override
    @RolesAllowed({"Entertainer", "Management"})
    public List<OfferEntity> getAllOffers() throws AbstractAppException {
        return offerEntityFacade.findAllAndRefresh();
    }

    /**
     * Zwraca wszystkie aktywne oferty, dozwolone role: "Entertainer", "Management"
     * @return Liste ofert
     * @throws AbstractAppException
     */
    @PermitAll
    @Override
    public List<OfferEntity> getAllActiveOffers() throws AbstractAppException {
        List<OfferEntity> offerEntityList = offerEntityFacade.findAllAndRefresh();
        List<OfferEntity> activeOfferEntityList = new ArrayList<OfferEntity>();

        for (OfferEntity entity : offerEntityList) {
            if (entity.isActive() && entity.getEntertainer().isActive()) {
                activeOfferEntityList.add(entity);
            }
        }

        return activeOfferEntityList;
    }


    /**
     * Zwraca wszystkie oferty zalogowanego użytkownika, dozwolone role: "Entertainer"
     * @return Liste ofert
     * @throws AbstractAppException
     */
    @Override
    @RolesAllowed({"Entertainer"})
    public List<OfferEntity> getAllEntertainerOffers() throws AbstractAppException {
        List<OfferEntity> offerEntityList = offerEntityFacade.findAllAndRefresh();
        List<OfferEntity> entertainerOfferEntityList = new ArrayList<OfferEntity>();

        for (OfferEntity entity : offerEntityList) {
            if (entity.getEntertainer().getUser().getLogin().equals(ctx.getCallerPrincipal().getName())) {
                entertainerOfferEntityList.add(entity);
            }
        }
        return entertainerOfferEntityList;
    }

    /** Zwraca ofertę o podanym id
     * @param id id oferty
     * @return oferta o podanym id
     * @throws AbstractAppException
     */
    @Override
    @RolesAllowed({"Client", "Entertainer", "Management"})
    public OfferEntity getOffer(Long id) throws AbstractAppException {
        OfferEntity offerEntity = offerEntityFacade.findAndRefresh(id);
        if (offerEntity == null) {
            throw OfferNotFoundException.createOfferWithProvidedIdNotFoundException(id);
        }
        return offerEntity;
    }

    //    TODO:javadoc
    @RolesAllowed("Entertainer")
    @Override
    public OfferEntity createOffer(OfferToCreateDTO offer) throws AbstractAppException {
        EntertainerEntity entertainer = entertainerEntityMooFacade.findByLogin(ctx.getCallerPrincipal().getName());
        OfferEntity offerEntity = OfferConverter.createNewOfferEntityWithDetailsFromDTO(offer, entertainer);
        var t = offerEntity.getValidFrom().compareTo(offerEntity.getValidTo());
        if (offerEntity.getValidFrom().compareTo(offerEntity.getValidTo()) > 0) {
            throw OtherAppException.createInvalidDatasExeception();
        }
        offerEntityFacade.create(offerEntity);
        offerEntityFacade.flush();
        return offerEntityFacade.findAndRefresh(offerEntity.getId());
    }
    //    TODO:javadoc
    @Override
    public OfferEntity updateOffer(Long id, OfferEntity newOffer) {
        throw new UnsupportedOperationException();
    }
    //    TODO:javadoc
    @Override
    public OfferEntity deactivateOffer(Long id) throws AbstractAppException {
        OfferEntity offerEntity = offerEntityFacade.findAndRefresh(id);
        if (offerEntity == null) {
            throw OfferNotFoundException.createOfferWithProvidedIdNotFoundException(id);
        }
        offerEntity.setActive(false);
        offerEntityFacade.flush();
        return offerEntityFacade.findAndRefresh(id);
    }
    //    TODO:javadoc
    @Override
    public OfferEntity activateOffer(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed("Entertainer")
    public OfferEntity changeActiveOffer(Long id, boolean active) throws AbstractAppException {
        OfferEntity offer = offerEntityFacade.find(id);
        offer.setActive(active);
        offerEntityFacade.flush();
        return offerEntityFacade.findAndRefresh(id);
    }

    /** Dodanie danej oferty do ulubionych zalogowanego klienta, dozwolone role: "Client"
     * @param id id oferty do dodania do ulubionych
     * @return ofertę dodaną do ulubionych
     * @throws AbstractAppException
     */
    @RolesAllowed("Client")
    @Override
    public OfferEntity addOfferToFavourites(Long id) throws AbstractAppException {
        var offerEntity = offerEntityFacade.findAndRefresh(id);
        var userEntity = userEntityMooFacade.find(ctx.getCallerPrincipal().getName());
        var clientEntity = clientEntityMooFacade.findByUser(userEntity);
        if (offerEntity == null) {
            throw OfferNotFoundException.createOfferWithProvidedIdNotFoundException(id);
        }
        if (offerEntity.getLikedBy().contains(clientEntity)) {
            throw UniqueConstraintAppException.createFavouriteAlreadyException();
        }
        offerEntity.getLikedBy().add(clientEntity);
        offerEntityFacade.edit(offerEntity);
        offerEntityFacade.flush();
        return offerEntity;
    }

    /** Usunięcie danej oferty z ulubionych zalogowanego klienta, dozwolone role: "Client"
     * @param id id oferty do usunięcia z ulubionych
     * @return ofertę usuniętych z  ulubionych
     * @throws AbstractAppException
     */
    @RolesAllowed("Client")
    @Override
    public OfferEntity deleteOfferFromFavourites(Long id) throws AbstractAppException {
        var offerEntity = offerEntityFacade.findAndRefresh(id);
        var userEntity = userEntityMooFacade.findByLogin(ctx.getCallerPrincipal().getName());
        var clientEntity = clientEntityMooFacade.findByUser(userEntity);
        if (offerEntity == null) {
            throw OfferNotFoundException.createOfferWithProvidedIdNotFoundException(id);
        }
        if (!offerEntity.getLikedBy().contains(clientEntity)) {
            throw UniqueConstraintAppException.createNotFavouriteAlreadyException();
        }
        offerEntity.getLikedBy().remove(clientEntity);
        offerEntityFacade.edit(offerEntity);
        offerEntityFacade.flush();
        return offerEntity;
    }

    /** Pobiera ulubione oferty zalogowanego klienta, dozwolone role: "Client"
     * @return listę ulubionych ofert
     * @throws AbstractAppException
     */
    @RolesAllowed("Client")
    @Override
    public List<OfferEntity> getFavouriteOffers() throws AbstractAppException {
        var userEntity = userEntityMooFacade.findByLogin(ctx.getCallerPrincipal().getName());
        var clientEntity = clientEntityMooFacade.findByUser(userEntity);
        return offerEntityFacade.findAllFavouritesOffers(clientEntity);
    }


    /** Edycja oferty przez PDR, dozwolone role: "Entertainer"
     * @param id id oferty do edytowania
     * @param offerEditDTO Nowa treść oferty
     * @return zedytowaną ofertę
     * @throws AbstractAppException
     */
    @RolesAllowed("Entertainer")
    @Override
    public OfferEntity editOffer(Long id, OfferEditDTO offerEditDTO) throws AbstractAppException {
        OfferEntity offerEntity = offerEntityFacade.findAndRefresh(id);
        if (offerEntity == null) {
            throw OfferNotFoundException.createOfferWithProvidedIdNotFoundException(id);
        }

        var userEntity = offerEntity.getEntertainer().getUser();
        if (!userEntity.getLogin().equals(ctx.getCallerPrincipal().getName()))
            throw NotAllowedAppException.createNotAllowedException();


        OfferEditDTO orginalOfferEditDTO = OfferEditConverter.offerEntityToDTO(offerEntity);

        boolean notChanged = OfferEditComparator.compareData(orginalOfferEditDTO, offerEditDTO);
        if (notChanged) {
            return offerEntity;
        }

        var offerValidFrom = offerEditDTO.getValidFrom();
        var offerValidTo = offerEditDTO.getValidTo();
        var currentDateTime = OffsetDateTime.now();
        boolean validNewDateCurrent = offerValidFrom.isAfter(currentDateTime) || offerValidFrom.isEqual(currentDateTime);
        boolean validNewDateFromTo = offerValidTo.isAfter(offerValidFrom) || offerValidTo.isEqual(offerValidFrom);
        boolean validNewDate = validNewDateCurrent && validNewDateFromTo;
        if (!validNewDate) {
            throw OfferException.createOfferNotAcceptedAppException(offerEditDTO.getId());
        }

        //Tworzenie nowego na podstawie starego
        offerEntity.setActive(false);//Stary deaktywujemy
        OfferEntity newOffer = new OfferEntity();
        newOffer.setEntertainer(offerEntity.getEntertainer());
        newOffer.setTitle(offerEditDTO.getTitle());
        newOffer.setDescription(offerEditDTO.getDescription());
        newOffer.setActive(true);
        newOffer.setValidFrom(offerEditDTO.getValidFrom());
        newOffer.setValidTo(offerEditDTO.getValidTo());
        newOffer.setOfferAvailabilities(offerEntity.getOfferAvailabilities());
        newOffer.setEntertainer(offerEntity.getEntertainer());
        offerEntityFacade.create(newOffer);
        return newOffer;
    }
}
