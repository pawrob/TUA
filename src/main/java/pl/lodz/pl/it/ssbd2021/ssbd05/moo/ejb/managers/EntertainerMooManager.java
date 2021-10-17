package pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.managers;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.EntertainerUnavailabilityDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.EntertainerUnavailabilityEntireDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.AbstractManager;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.AccessLevelEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.EntertainerEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.EntertainerUnavailabilityEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.UserEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.EntertainerNotFoundException;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.NotAllowedAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.UserNotFoundAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades.EntertainerEntityMooFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades.EntertainerUnavailabilityEntityMooFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades.QueryLogEntityMooFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades.UserEntityMooFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.converters.EntertainerUnavailabilityConverter;
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
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Komponent EJB z logiką biznesową dotyczącą Pracownika Działu Rozrywki w Module Obsługi Ofert (MOO).
 */
@DenyAll
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors(EjbLoggerInterceptor.class)
public class EntertainerMooManager extends AbstractMooManager implements EntertainerMooManagerLocal {

    @Resource
    private SessionContext ctx;

    @Inject
    private EntertainerEntityMooFacade entertainerFacade;

    @Inject
    EntertainerUnavailabilityEntityMooFacade entertainerUnavailabilityEntityMooFacade;

    @Inject
    UserEntityMooFacade userEntityMooFacade;

    @Inject
    private QueryLogEntityMooFacade queryLogEntityMooFacade;

    @Override
    public EntertainerEntity updateDescription(Long id, String description) {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed("ENTERTAINER")
    public EntertainerUnavailabilityEntity updateUnavailability(EntertainerUnavailabilityEntireDTO unavailabilityDTO) throws AbstractAppException {

        if (unavailabilityDTO.getEntertainerId() != entertainerFacade.findByLogin(ctx.getCallerPrincipal().getName()).getId()) {
            throw new NotAllowedAppException(NotAllowedAppException.NOT_ALLOWED);
        }
        EntertainerUnavailabilityEntity unavailabilityEntity = entertainerUnavailabilityEntityMooFacade
                .find(unavailabilityDTO.getId());
        unavailabilityEntity.setDateTimeFrom(Timestamp.from(unavailabilityDTO.getDateTimeFrom().toInstant()));
        unavailabilityEntity.setDateTimeTo(Timestamp.from(unavailabilityDTO.getDateTimeTo().toInstant()));
        unavailabilityEntity.setDescription(unavailabilityDTO.getDescription());
        unavailabilityEntity.setValid(unavailabilityDTO.isValid());
        entertainerUnavailabilityEntityMooFacade.flush();
        return entertainerUnavailabilityEntityMooFacade.findAndRefresh(unavailabilityDTO.getId());
    }


    @PermitAll
    @Override
    public List<EntertainerUnavailabilityEntity> getUnavailabilities() throws AbstractAppException {
        return entertainerUnavailabilityEntityMooFacade.findAll();
    }

    @Override
    @RolesAllowed("ENTERTAINER")
    public List<EntertainerUnavailabilityEntity> getMyUnavailabilities() throws AbstractAppException {
        long entertainerId = entertainerFacade.findByLogin(ctx.getCallerPrincipal().getName()).getId();
        List<EntertainerUnavailabilityEntity> unavailabilityEntities = entertainerUnavailabilityEntityMooFacade
                .findAll()
                .stream()
                .filter(x -> x.getEntertainerId() == entertainerId)
                .collect(Collectors.toList());
        return unavailabilityEntities;
    }


    @Override
    public EntertainerEntity getEntertainerByUsername(String username) throws AbstractAppException {
        return entertainerFacade.findAll()
                .stream()
                .filter(entertainer -> entertainer
                        .getUser()
                        .getLogin()
                        .equals(username))
                .findFirst()
                .orElseThrow(() -> UserNotFoundAppException.createUserWithProvidedLoginNotFoundException(username));
    }

    @Override
    @RolesAllowed("ENTERTAINER")
    public EntertainerUnavailabilityEntity createUnavailability(EntertainerUnavailabilityDTO unavailability) throws AbstractAppException {
        EntertainerEntity entertainer = entertainerFacade.findByLogin(ctx.getCallerPrincipal().getName());
        EntertainerUnavailabilityEntity unavailabilityEntity = EntertainerUnavailabilityConverter.createEntityFromDTO(
                unavailability, true, entertainer.getId());
        entertainerUnavailabilityEntityMooFacade.create(unavailabilityEntity);
        entertainerUnavailabilityEntityMooFacade.flush();
        return entertainerUnavailabilityEntityMooFacade.findAndRefresh(unavailabilityEntity.getId());
    }




    /**
     * Metoda pozwalająca wczytać opis w profilu ustawiony przez PDR.
     * <p>
     * Dostępna dla użytkowników z jedną z ról: "Client", "Entertainer", "Management"
     *
     * @param id identyfikator PDR
     * @return ciąg znaków reprezentujący opis w profilu PDR
     * @throws AbstractAppException
     */
    @Override
    @RolesAllowed({"CLIENT", "ENTERTAINER", "MANAGEMENT"})
    public String getDescription(Long id) throws AbstractAppException {

        EntertainerEntity entertainerEntity = entertainerFacade.findAndRefresh(id);
        if (entertainerEntity == null) {
            throw EntertainerNotFoundException.entertainerWithProvidedIdNotFoundException(id);
        }
        return entertainerEntity.getDescription();
    }

    /**
     * Metoda pozwalająca wczytać opis w profilu obecnie uwierzytelnionego PDR.
     * <p>
     * Dostępna dla użytkownika z rolą "Entertainer".
     *
     * @return ciąg znaków reprezentujący opis w profilu PDR
     * @throws AbstractAppException
     */
    @Override
    @RolesAllowed({"ENTERTAINER"})
    public String getSelfDescription() throws AbstractAppException {
        UserEntity user = userEntityMooFacade.findByLogin(ctx.getCallerPrincipal().getName());
        EntertainerEntity entertainerEntity = null;
        for (AccessLevelEntity access_level : user.getAccessLevels()) {
            if (access_level.getAccessLevel().equals("ENTERTAINER")) {
                entertainerEntity = entertainerFacade.findAndRefresh(access_level.getId());
            }
        }
        if (entertainerEntity == null) {
            throw EntertainerNotFoundException.entertainerNotFoundException();
        }
        return entertainerEntity.getDescription();
    }


    /**
     * Metoda pozwalająca zaktualizować opis w profilu obecnie uwierzytelnionego PDR.
     * <p>
     * Dostępna dla użytkownika z rolą "Entertainer".
     *
     * @return ciąg znaków reprezentujący opis w profilu PDR
     * @throws AbstractAppException
     */
    @Override
    @RolesAllowed({"ENTERTAINER"})
    public String updateDescription(String description) throws AbstractAppException {
        UserEntity user = userEntityMooFacade.findByLogin(ctx.getCallerPrincipal().getName());
        EntertainerEntity entertainerEntity = null;
        for (AccessLevelEntity access_level : user.getAccessLevels()) {
            if (access_level.getAccessLevel().equals("ENTERTAINER")) {
                entertainerEntity = entertainerFacade.findAndRefresh(access_level.getId());
            }
        }
        if (entertainerEntity == null) {
            throw EntertainerNotFoundException.entertainerNotFoundException();
        }

        entertainerEntity.setDescription(description);
        entertainerFacade.flush();

        return entertainerEntity.getDescription();
    }

    /**
     * Metoda pozwalająca pobrać podstawowe informacje o PDR.
     * <p>
     * Dostępna dla użytkowników z jedną z ról: "Client", "Entertainer", "Management"
     *
     * @param id identyfikator PDR
     * @return informacje o PDR
     * @throws AbstractAppException
     */
    @Override
    @RolesAllowed({"CLIENT", "ENTERTAINER", "MANAGEMENT"})
    public EntertainerEntity getBasicEntertainerInfo(Long id) throws AbstractAppException {
        EntertainerEntity entertainerEntity = entertainerFacade.findAndRefresh(id);
        if (entertainerEntity == null) {
            throw EntertainerNotFoundException.entertainerWithProvidedIdNotFoundException(id);
        }
        return entertainerEntity;
    }

    /**
     * Metoda pozwalająca pobrać okresy niedostępności PDR.
     * <p>
     * Dostępna dla użytkownika z rolą "Client".
     *
     * @param id identyfikator PDR
     * @return lista okresów niedostępności PDR
     * @throws AbstractAppException
     */
    @Override
    @RolesAllowed({"CLIENT"})
    public List<EntertainerUnavailabilityEntity> getUnavailability(Long id) throws AbstractAppException {
        EntertainerEntity entertainer = entertainerFacade.findAndRefresh(id);
        return entertainer.getEntertainerUnavailability()
                .stream()
                .filter(EntertainerUnavailabilityEntity::isValid)
                .collect(Collectors.toList());
    }

    //TODO: javadoc here
    @Override
    public EntertainerUnavailabilityEntity updateUnavailability(Long id, EntertainerUnavailabilityEntity unavailabilityEntity) {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed("ENTERTAINER")
    public EntertainerUnavailabilityEntity updateUnavailabilityStatus(long id, boolean status) throws AbstractAppException {
        EntertainerUnavailabilityEntity ent = entertainerUnavailabilityEntityMooFacade.find(id);
        ent.setValid(status);
        entertainerUnavailabilityEntityMooFacade.flush();
        return entertainerUnavailabilityEntityMooFacade.findAndRefresh(id);
    }
}
