package pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.managers;


import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.AbstractManager;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.*;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.*;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.OptimisticLockExceptionInterceptor;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.PersistenceExceptionInterceptor;
import pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades.AccessLevelEntityMokFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades.UserEntityMokFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.HashGenerator;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.MailManager;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.EjbLoggerInterceptor;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Komponent EJB odpowiadający za operacje na obiektach encji UserEntity z poziomem dostępu "ENTERTAINER".
 */
@Stateful // w celu mechanizmu ponawiania transakcji
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@DenyAll
@Interceptors({PersistenceExceptionInterceptor.class, OptimisticLockExceptionInterceptor.class,EjbLoggerInterceptor.class})
public class EntertainerManager extends AbstractMokManager implements EntertainerManagerLocal {


    @Inject
    private UserEntityMokFacade userEntityMokFacade;

    @Inject
    private MailManager mailManager;

    @Inject
    private AccessLevelEntityMokFacade accessLevelEntityMokFacade;


    private String buttonText;


    /**
     * Metoda managera umożliwiająca dezaktywowanie konta użytkownika z poziomem dostępu "ENTERTAINER".
     *
     * @param id identyfikator konta jakie ma zostac dezaktywowane
     * @throws AbstractAppException       abstrakcyjny wyjątek aplikacyjny
     * @throws AccessLevelNotFound        wyjątek rzucany gdy user o zadanym id nie posiada przypisanego poziomu dostępu "ENTERTAINER"
     * @throws OptimisticLockAppException rzucany w przypadku naruszenia zasad struktury danych
     */
    @Override
    @RolesAllowed("MANAGEMENT")
    public UserEntity deactivateEntertainerAccount(Long id) throws AbstractAppException {

        if (!isEntertainer(id)) {
            throw AccessLevelNotFound.createAccessLevelForUserNotFound(id, "ENTERTAINER");
        }


        buttonText = "noButton";


        userEntityMokFacade.findAndRefresh(id)
                .getAccessLevels()
                .stream()
                .filter(level -> level.getAccessLevel().equalsIgnoreCase("ENTERTAINER"))
                .forEach(level -> level.setActive(false));
        userEntityMokFacade.flush();

        mailManager.createAndSendEmailFromTemplate(userEntityMokFacade.find(id), "headerBlockEntertainer", "headerBlockEntertainer", buttonText, "footerTextDeleteAccount");


        return userEntityMokFacade.findAndRefresh(id);
    }


    /**
     * Metoda służąca do sprawdzenia czy dany user posiada poziom dostępu "ENTERTAINER".
     *
     * @param id id konta dla którego szukany jest poziom dostępu
     * @throws AbstractAppException     abstrakcyjny wyjątek aplikacyjny
     * @throws UserNotFoundAppException rzucany w przypadku gdy uzytkownik nie został znaleziony
     */
    private boolean isEntertainer(Long id) throws AbstractAppException {
        UserEntity user = userEntityMokFacade.findAndRefresh(id);
        if (user == null) {
            throw UserNotFoundAppException.createUserWithProvidedIdNotFoundException(id);
        }
        long counter = user.getAccessLevels()
                .stream()
                .filter(level -> level.getAccessLevel().equalsIgnoreCase("ENTERTAINER"))
                .count();
        return (counter != 0);
    }

    /**
     * Metoda managera umożliwiająca reaktywowanie konta użytkownika z poziomem dostępu "ENTERTAINER".
     *
     * @param id identyfikator konta jakie ma zostac reaktywowane
     * @throws AbstractAppException       abstrakcyjny wyjątek aplikacyjny
     * @throws AccessLevelNotFound        wyjątek rzucany gdy user o zadanym id nie posiada przypisanego poziomu dostępu "ENTERTAINER"
     * @throws OptimisticLockAppException rzucany w przypadku naruszenia zasad struktury danych
     */
    @Override
    @RolesAllowed("MANAGEMENT")
    public UserEntity reactivateEntertainerAccount(Long id) throws AbstractAppException {

        if (!isEntertainer(id)) {
            throw AccessLevelNotFound.createAccessLevelForUserNotFound(id, "ENTERTAINER");
        }

        buttonText = "noButton";


        userEntityMokFacade.findAndRefresh(id)
                .getAccessLevels()
                .stream()
                .filter(level -> level.getAccessLevel().equalsIgnoreCase("ENTERTAINER"))
                .forEach(level -> level.setActive(true));
        userEntityMokFacade.flush();

        mailManager.createAndSendEmailFromTemplate(userEntityMokFacade.find(id), "headerUnblockEntertainer", "headerUnblockEntertainer", buttonText, "footerTextDeleteAccount");

        return userEntityMokFacade.findAndRefresh(id);
    }

    /**
     * Metoda logiki biznesowej tworząca konto pracownika.
     *
     * @param entertainerEntity obiekt encji reprezentujący konto pracownika
     * @return obiekt encji pracownika, który został zapisany w bazie
     * @throws AbstractAppException rzucany w przypadku naruszenia zasad struktury danych
     */
    @Override
    @RolesAllowed("MANAGEMENT")
    @Interceptors(PersistenceExceptionInterceptor.class)
    public UserEntity createEnterainer(EntertainerEntity entertainerEntity) throws AbstractAppException {
        UserEntity user = entertainerEntity.getUser();
        if (null != userEntityMokFacade.findByEmail(user.getEmail()))
            throw UniqueConstraintAppException.createEmailTakenException();
        if (null != userEntityMokFacade.findByLogin(user.getLogin()))
            throw UniqueConstraintAppException.createLoginTakenException();
        user.setPassword(
                HashGenerator.generateHash(
                        entertainerEntity.getUser().getPassword()
                )
        );
        List<AccessLevelEntity> AccessLevelList = new ArrayList<>();
        AccessLevelList.add(new ManagementEntity());
        AccessLevelList.add(new ClientEntity());
        AccessLevelList.forEach(a -> {
            a.setActive(false);
            a.setUser(user);
            user.getAccessLevels().add(a);
        });
        user.getAccessLevels().add(entertainerEntity);
        String token = HashGenerator.generateSecureRandomToken();
        user.setPasswordResetToken(token);
        userEntityMokFacade.create(user);
        //W celu nadania id
        userEntityMokFacade.flush();
        PersonalDataEntity personalDataEntity = new PersonalDataEntity();
        personalDataEntity.setUser(user);
        personalDataEntity.setUserId(user.getId());
        user.setPersonalData(personalDataEntity);
        user.setTokenTimestamp(OffsetDateTime.now());

        userEntityMokFacade.flush();
        buttonText = "https://studapp.it.p.lodz.pl:8405/ssbd05/#/accountConfirmation/?id=" + user.getId() + "&token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        mailManager.createAndSendEmailFromTemplate(user, "titleCreateAccount", "headerCreateAccount", buttonText, "footerCreateAccount");
        return user;
    }

    /**
     * Metoda pozwalająca na aktualizacje statusu poziomu dostepu o zadanym id.
     * @param id Identyfikator poziomu dostepu poddawanego aktualizacji.
     * @param status Wartość jaką ma przyjąć status poziomu dostępu o zadanym id.
     * @return Zmieniony poziom dostępu
     * @throws AccessLevelNotFound        wyjątek rzucany nie znaleziono poziomu dostepu z zadanym id
     * @throws OptimisticLockAppException rzucany w przypadku naruszenia zasad struktury danych
     * @throws UserNotFoundAppException   wyjątek rzucany gdy nie znaleziono usera w bazie danych który posiada poziom dostepu o zadanym id
     */
    @Override
    @RolesAllowed("MANAGEMENT")
    public AccessLevelEntity changeEntertainerAccessLevelStatus(Long id, boolean status) throws AbstractAppException {
        AccessLevelEntity accessLevelEntity = accessLevelEntityMokFacade.find(id);
        buttonText = "noButton";

        UserEntity userEnt = userEntityMokFacade.findAllAndRefresh()
                .stream()
                .filter(user -> user.getAccessLevels()
                        .stream()
                        .filter(level -> level.getId() == id)
                        .count() > 0)
                .findFirst()
                .orElseThrow(() -> UserNotFoundAppException.userWithAccessLevelNotFoundException());
        accessLevelEntity.setActive(status);
        accessLevelEntityMokFacade.flush();

        mailManager.createAndSendEmailFromTemplate(userEnt, "headerBlockEntertainer", "headerBlockEntertainer", buttonText, "footerTextDeleteAccount");

        return accessLevelEntityMokFacade.find(id);
    }
}
