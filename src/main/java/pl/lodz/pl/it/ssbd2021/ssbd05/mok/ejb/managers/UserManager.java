package pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.managers;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.AccessLevelDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.AccessLevelEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.PersonalDataEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.SessionLogEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.UserEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.*;
import pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades.AccessLevelEntityMokFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades.PersonalDataEntityMokFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades.SessionLogEntityMokFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades.UserEntityMokFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.HashGenerator;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.MailManager;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.AccessLevelLogger;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.EjbLoggerInterceptor;

import javax.annotation.Resource;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.*;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Komponent EJB odpowiadający za operacje na użytkownikach istniejących już w systemie
 */
@Stateful // w celu mechanizmu ponawiania transakcji
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@DenyAll
@Interceptors(EjbLoggerInterceptor.class)
public class UserManager extends AbstractMokManager implements UserManagerLocal {

    @Resource
    private SessionContext ctx;

    @Inject
    PersonalDataEntityMokFacade personalDataEntityMokFacade;

    @Inject
    UserEntityMokFacade userEntityFacade;

    @Inject
    SessionLogEntityMokFacade sessionLogEntityMokFacade;

    @Inject
    private MailManager mailManager;

    @Inject
    private AccessLevelEntityMokFacade accessLevelEntityMokFacade;

    @Inject
    private HttpServletRequest request;

    @Inject
    private AccessLevelLogger accessLevelLogger;

    private String buttonText;
    private String footerText;


    @Override
    @PermitAll
    public void logAccessLevelChange(String login, String accessLevel) {
        accessLevelLogger.logAccessLevelChange(login, accessLevel);
    }

    /**
     * Metoda umożliwiająca zmianę danych personalnych użytkownika
     *
     * @param newData nowe dane użytkownika
     * @return encja użytkownika po zmianie
     * @throws UserNotFoundAppException wyjątek rzucany w przypadku braku podanego id użytkownika w bazie
     */
    @Override
    @RolesAllowed({"CLIENT", "ENTERTAINER", "MANAGEMENT"})
    public UserEntity editUserData(PersonalDataEntity newData) throws AbstractAppException {
//        long id = newData.getUserId();
        UserEntity userEntity = userEntityFacade.findByLogin(ctx.getCallerPrincipal().getName());
        if (null == userEntity)
            throw UserNotFoundAppException.createUserWithProvidedIdNotFoundException(userEntity.getId());
        if (!userEntity.getLogin().equals(ctx.getCallerPrincipal().getName()))
            throw NotAllowedAppException.createNotAllowedException();
        PersonalDataEntity personalDataEntity = personalDataEntityMokFacade.find(userEntity.getId());
        if (null == personalDataEntity) {
            personalDataEntityMokFacade.create(newData);
            userEntity.setPersonalData(newData);
        } else
            copyProperties(personalDataEntity, newData);
        userEntityFacade.flush();
        personalDataEntityMokFacade.flush();
        return userEntity;
    }

    /**
     * Metoda przepisująca dane z jednego obiektu encji do drugiego.
     *
     * @param oldData - stary obiekt encji danych personalnych
     * @param newData - nowy obiekt encji danych personalnych
     */
    private void copyProperties(PersonalDataEntity oldData, PersonalDataEntity newData) {
        oldData.setName(newData.getName());
        oldData.setSurname(newData.getSurname());
        oldData.setPhoneNumber(newData.getPhoneNumber());
        oldData.setLanguage(newData.getLanguage());
        oldData.setIsMan(newData.getIsMan());
        oldData.setVersion(newData.getVersion());
    }


    /**
     * Metoda pozwalająca na zweryfikowanie uzytkownka w systemie za pomocą linku wysylanego w wiadomosći email.
     *
     * @param id    - identyfikator uzytkownika
     * @param token - jednorazowy token uzytkownika, ważny 24h
     * @return obiekt encji uzytkownika
     * @throws OptimisticLockAppException rzucany w przypadku naruszenia zasad struktury danych
     * @throws InvalidTokenException      rzucany w przypadku niepoprawnego tokenu
     * @throws UserNotFoundAppException   rzucany w przypadku gdyuzytkownik nie został znaleziony
     */
    @Override
    @PermitAll
    public UserEntity verifyUser(long id, String token) throws AbstractAppException {
        UserEntity userEntity = userEntityFacade.find(id);
        if (null == userEntity)
            throw UserNotFoundAppException.createUserWithProvidedIdNotFoundException(id);
        if (null == token)
            throw InvalidTokenException.createInvalidTokenException(id);
        if (userEntity.getTokenTimestamp() == null)
            throw InvalidTokenException.createTokenExpiredException();

        if (!userEntity.getPasswordResetToken().equals(token)) {
            throw InvalidTokenException.createInvalidTokenException(id);
        }

        if (userEntity.getTokenTimestamp().until(OffsetDateTime.now(), ChronoUnit.HOURS) > 24) {
            throw InvalidTokenException.createTokenExpiredException();
        }


        userEntity.setVerified(true);
        userEntity.setTokenTimestamp(null);
        userEntity.setPasswordResetToken(null);
        userEntityFacade.edit(userEntity);
        userEntityFacade.flush();
        mailManager.createAndSendEmailFromTemplate(userEntity, "titleSuccessfulVerification", "titleSuccessfulVerification", "noButton", "footerTextDeleteAccount");

        return userEntity;
    }


    /**
     * Metoda zmieniająca hasło konta użytkownika.
     *
     * @param oldPassword - aktualne hasło konta użytkownika
     * @param newPassword - nowe hasło
     * @return obiekt encji uzytkownika
     * @throws AbstractAppException             abstrakcyjny wyjątek aplikacyjny
     * @throws OptimisticLockAppException       rzucany w przypadku naruszenia zasad struktury danych
     * @throws UserNotFoundAppException         rzucany w przypadku gdy uzytkownik nie został znaleziony
     * @throws IncorrectPasswordAppException    rzucany w przypadku gdy uzytkownik poda niepoprawne hasło
     * @throws NewPasswordSameAsOldAppException rzucany w przypadku gdy podane haslo jest takie samo jak poprzednie
     */
    @Override
    @RolesAllowed({"CLIENT", "ENTERTAINER", "MANAGEMENT"})
    public UserEntity changePassword(String oldPassword, String newPassword) throws
            AbstractAppException {
        var userEntity = userEntityFacade.findByLogin(ctx.getCallerPrincipal().getName());
        if (userEntity == null) {
            throw UserNotFoundAppException.createUserWithProvidedLoginNotFoundException(ctx.getCallerPrincipal().getName());
        }

        if (!HashGenerator.checkPassword(oldPassword, userEntity.getPassword())) {
            throw IncorrectPasswordAppException.createIncorrectPasswordAppException();
        }
        if (HashGenerator.checkPassword(newPassword, userEntity.getPassword())) {
            throw NewPasswordSameAsOldAppException.createNewPasswordSameAsOldAppException();
        }

        userEntity.setPassword(HashGenerator.generateHash(newPassword));
        userEntity.setTokenTimestamp(null);
        userEntity.setPasswordResetToken(null);
        userEntityFacade.edit(userEntity);
        userEntityFacade.flush();

        return userEntity;
    }

    /**
     * Metoda zmieniająca adres email konta użytkownika.
     *
     * @param id    - identyfikator uzytkownika
     * @param token - jednorazowy token uzytkownika, ważny 20 minut
     * @param email - nowy adres e-mail
     * @return obiekt encji uzytkownika
     * @throws AbstractAppException         abstrakcyjny wyjątek aplikacyjny
     * @throws OptimisticLockAppException   rzucany w przypadku naruszenia zasad struktury danych
     * @throws InvalidTokenException        rzucany w przypadku niepoprawnego tokenu
     * @throws UserNotFoundAppException     rzucany w przypadku gdy uzytkownik nie został znaleziony
     * @throws NotAllowedAppException       rzucany w przypadku gdy login uzytkownika przypisanego do maila jest inny niz uzytkownika zmieniajacego adres email
     * @throws UniqueConstraintAppException rzucany w przypadku gdy podane haslo jest takie samo jak poprzednie
     */
    @Override
    @RolesAllowed({"CLIENT", "ENTERTAINER", "MANAGEMENT"})
    public UserEntity changeEmail(long id, String token, String email) throws
            AbstractAppException {
        UserEntity userEntity = userEntityFacade.find(id);
        if (null == userEntity)
            throw UserNotFoundAppException.createUserWithProvidedIdNotFoundException(id);
        if (null == token)
            throw InvalidTokenException.createInvalidTokenException(id);
        if (userEntity.getTokenTimestamp() == null)
            throw InvalidTokenException.createTokenExpiredException();
        if (!userEntity.getLogin().equals(ctx.getCallerPrincipal().getName()))
            throw NotAllowedAppException.createNotAllowedException();
        if (!userEntity.getPasswordResetToken().equals(token)) {
            throw InvalidTokenException.createInvalidTokenException(id);
        }
        if (userEntity.getTokenTimestamp().until(OffsetDateTime.now(), ChronoUnit.MINUTES) > 20) {
            throw InvalidTokenException.createTokenExpiredException();
        }

        buttonText = "noButton";


        try {
            userEntity.setEmail(email);
            userEntity.setTokenTimestamp(null);
            userEntity.setPasswordResetToken(null);
            userEntityFacade.edit(userEntity);
            userEntityFacade.flush();
            userEntityFacade.refresh(userEntity);
            mailManager.createAndSendEmailFromTemplate(userEntity, "titleChangeEmail", "headerChangeEmail", buttonText, "footerTextDeleteAccount");
        } catch (NullPointerException | EJBException npe) {
            throw InvalidTokenException.createTokenExpiredException();
        } catch (PersistenceException pe) {
            throw UniqueConstraintAppException.createEmailTakenException();
        }
        return userEntity;
    }

    /**
     * Metoda wysyłająca wiadomość email z tokenem pozwalającym na zmiane adresu email.
     *
     * @param id    - identyfikator uzytkownika
     * @param email - adres email przypisany do konta uzytkownika
     * @return obiekt encji uzytkownika
     * @throws AbstractAppException         abstrakcyjny wyjątek aplikacyjny
     * @throws OptimisticLockAppException   rzucany w przypadku naruszenia zasad struktury danych
     * @throws UserNotFoundAppException     rzucany w przypadku gdy uzytkownik nie został znaleziony
     * @throws EmailNotUniqueAppException   rzucany w przypadku gdy email jest juz zajety
     * @throws NotAllowedAppException       rzucany w przypadku gdy login uzytkownika przypisanego do maila jest inny niz uzytkownika zmieniajacego adres email
     * @throws UniqueConstraintAppException rzucany w przypadku gdy podane haslo jest takie samo jak poprzednie
     */
    @Override
    @RolesAllowed({"CLIENT", "ENTERTAINER", "MANAGEMENT"})
    public UserEntity requestChangeEmail(String login, String email) throws
            AbstractAppException {

        UserEntity userEntity = userEntityFacade.findByLogin(login);
        if (null == userEntity)
            throw UserNotFoundAppException.createUserWithProvidedLoginNotFoundException(login);
        if (!userEntity.getLogin().equals(ctx.getCallerPrincipal().getName()))
            throw NotAllowedAppException.createNotAllowedException();
        if (email.equals(userEntity.getEmail())) {
            throw EmailNotUniqueAppException.createEmailNotUniqueException();
        }
        String token = HashGenerator.generateSecureRandomToken();
        userEntity.setPasswordResetToken(token);
        userEntity.setTokenTimestamp(OffsetDateTime.now());

        buttonText = "https://studapp.it.p.lodz.pl:8405/ssbd05/#/account/change-email/confirm/" + userEntity.getId() + "/" + URLEncoder.encode(token, StandardCharsets.UTF_8) + "/" + URLEncoder.encode(email, StandardCharsets.UTF_8);
        try {
            userEntityFacade.edit(userEntity);
            userEntityFacade.flush();
            mailManager.createAndSendEmailFromTemplate(userEntity, "titleRequestChangeEmail", "headerRequestChangeEmail", buttonText, "footerRequestChangeEmail");
        } catch (PersistenceException pe) {
            throw UniqueConstraintAppException.createEmailTakenException();
        }
        return userEntity;
    }

    /**
     * Metoda resetująca hasło konta użytkownika.
     *
     * @param id          - identyfikator uzytkownika
     * @param token       - jednorazowy token uzytkownika, ważny 20 minut
     * @param newPassword - nowe hasło
     * @return obiekt encji uzytkownika
     * @throws AbstractAppException       abstrakcyjny wyjątek aplikacyjny
     * @throws OptimisticLockAppException rzucany w przypadku naruszenia zasad struktury danych
     * @throws UserNotFoundAppException   rzucany w przypadku gdy uzytkownik nie został znaleziony
     * @throws InvalidTokenException      rzucany w przypadku niepoprawnego tokenu
     */
    @Override
    @PermitAll
    public UserEntity resetPassword(long id, String token, String newPassword) throws
            AbstractAppException {

        UserEntity userEntity = userEntityFacade.find(id);
        if (null == userEntity)
            throw UserNotFoundAppException.createUserWithProvidedIdNotFoundException(id);
        if (null == token)
            throw InvalidTokenException.createInvalidTokenException(id);
        if (userEntity.getTokenTimestamp() == null)
            throw InvalidTokenException.createTokenExpiredException();
        if (!userEntity.getPasswordResetToken().equals(token)) {
            throw InvalidTokenException.createInvalidTokenException(id);
        }
        if (HashGenerator.checkPassword(newPassword, userEntity.getPassword())) {
            throw NewPasswordSameAsOldAppException.createNewPasswordSameAsOldAppException();
        }
        if (userEntity.getTokenTimestamp().until(OffsetDateTime.now(), ChronoUnit.MINUTES) > 20) {
            throw InvalidTokenException.createTokenExpiredException();
        }

        userEntity.setPassword(HashGenerator.generateHash(newPassword));
        userEntity.setTokenTimestamp(null);
        userEntity.setPasswordResetToken(null);
        userEntityFacade.edit(userEntity);
        userEntityFacade.flush();
        return userEntity;

    }

    /**
     * Metoda wysyłająca wiadomość email z tokenem pozwalającym na zresetowanie hasła.
     *
     * @param email - adres email przypisany do konta uzytkownika
     * @return obiekt encji uzytkownika
     * @throws AbstractAppException       abstrakcyjny wyjątek aplikacyjny
     * @throws OptimisticLockAppException rzucany w przypadku naruszenia zasad struktury danych
     * @throws UserNotFoundAppException   rzucany w przypadku gdy uzytkownik nie został znaleziony
     */
    @Override
    @PermitAll
    public UserEntity requestResetPassword(String email) throws
            AbstractAppException {

        UserEntity userEntity = userEntityFacade.findByEmail(email);
        if (null == userEntity)
            throw UserNotFoundAppException.createUserWithProvidedEmailNotFoundException(email);

        String token = HashGenerator.generateSecureRandomToken();
        userEntity.setPasswordResetToken(token);
        userEntity.setTokenTimestamp(OffsetDateTime.now());

        buttonText = "https://studapp.it.p.lodz.pl:8405/ssbd05/#/confirmresetpassword/?id=" + userEntity.getId() + "&token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);

        userEntityFacade.edit(userEntity);
        userEntityFacade.flush();
        mailManager.createAndSendEmailFromTemplate(userEntity, "titleRequestResetPassword", "headerRequestResetPassword", buttonText, "footerRequestChangeEmail");
        return userEntity;

    }


    /**
     * Metoda managera pozwalająca na aktywowanie konta użytkownika.
     *
     * @param id - identyfikator konta
     * @return obiekt encji uzytkownika
     * @throws AbstractAppException       abstrakcyjny wyjątek aplikacyjny
     * @throws OptimisticLockAppException rzucany w przypadku naruszenia zasad struktury danych
     * @throws UserNotFoundAppException   rzucany w przypadku gdy uzytkownik nie został znaleziony
     */
    @Override
    @RolesAllowed("MANAGEMENT")
    public UserEntity activateUserAccount(Long id) throws AbstractAppException {
        UserEntity userEntity;
        buttonText = "noButton";

        try {
            userEntity = userEntityFacade.find(id);
            userEntity.setActive(true);
            userEntityFacade.flush();
            mailManager.createAndSendEmailFromTemplate(userEntity, "headerUnlockAccount", "headerUnlockAccount", buttonText, "footerTextDeleteAccount");
        }
        //todo ???
        catch (NullPointerException npe) {
            throw UserNotFoundAppException.activateUserNotFoundException();
        }
        return userEntity;
    }

    /**
     * metoda managera pozwalająca na deaktywowanie konta użytkownika.
     *
     * @param id - identyfikator konta
     * @return obiekt encji uzytkownika
     * @throws AbstractAppException       abstrakcyjny wyjątek aplikacyjny
     * @throws OptimisticLockAppException rzucany w przypadku naruszenia zasad struktury danych
     * @throws UserNotFoundAppException   rzucany w przypadku gdy uzytkownik nie został znaleziony
     */
    @Override
    @RolesAllowed("MANAGEMENT")
    public UserEntity deactivateUserAccount(Long id) throws AbstractAppException {
        UserEntity userEntity;


        buttonText = "noButton";

        try {
            userEntity = userEntityFacade.find(id);
            userEntity.setActive(false);
            userEntityFacade.flush();
            mailManager.createAndSendEmailFromTemplate(userEntity, "headerBlockAccount", "headerBlockAccount", buttonText, "footerBlockAccount");
        } catch (NullPointerException npe) {
            throw UserNotFoundAppException.activateUserNotFoundException();
        }
        return userEntity;
    }

    /**
     * Metoda pobierająca użytkowników wraz z ich poziomami dostępów.
     *
     * @return lista encji użytkowników
     * @throws AbstractAppException - abstrakcyjny wyjątek aplikacyjny
     */
    @Override
    @RolesAllowed("MANAGEMENT")
    public List<UserEntity> getAllUsers() throws AbstractAppException {
        return userEntityFacade.findAllAndRefresh().stream().filter(userEntity -> !userEntity.getLogin().startsWith("#")).collect(Collectors.toList());
    }

    /**
     * Metoda pozwala na wyszukiwanie użytkowników systemu podając frazę zawierającą część ich imienia lub nazwiska.
     *
     * @param query - fraza zawierająca część imienia lub nazwiska
     * @return list - lista znalezionych kont uzytkowników
     * @throws AbstractAppException - abstrakcyjny wyjątek aplikacyjny
     */
    @Override
    @RolesAllowed("MANAGEMENT")
    public List<UserEntity> getUserByPieceOfData(String query) throws AbstractAppException {
        List<PersonalDataEntity> personalDataEntityList = personalDataEntityMokFacade.findByNameOrSurname(query);
        List<UserEntity> list = new ArrayList<UserEntity>();
        for (PersonalDataEntity personalDataEntity : personalDataEntityList) {
            list.add(userEntityFacade.findAndRefresh(personalDataEntity.getUserId()));
        }
        return list;
    }

    /**
     * Metoda managera pozwalajaca na pobranie informacje o danym koncie użytkownika.
     *
     * @param id - identyfikator konta
     * @return obiekt typu Optional zawierający dane użytkownika
     * @throws AbstractAppException     - abstrakcyjny wyjątek aplikacyjny
     * @throws UserNotFoundAppException rzucany w przypadku gdy uzytkownik nie został znaleziony
     */
    @Override
    @RolesAllowed("MANAGEMENT")
    public UserEntity getUser(Long id) throws AbstractAppException {
        UserEntity userEntity = userEntityFacade.findAndRefresh(id);
        if (userEntity == null) {
            throw UserNotFoundAppException.createUserWithProvidedIdNotFoundException(id);
        }
        return userEntity;
    }


    /**
     * metoda managera pozwalająca pobrać listę użytkownikow
     *
     * @param pageNumber - numer strony
     * @return lista użytkowników na danej stronie
     */
    @Override
    @RolesAllowed("MANAGEMENT")
    public List<UserEntity> getUsersByPage(Long pageNumber) throws AbstractAppException {
        int pageSize = 2;
        return userEntityFacade.findAll().stream()
                .filter(userEntity -> !userEntity.getLogin().startsWith("#"))
                .skip(pageNumber * pageSize).limit(pageSize)
                .collect(Collectors.toList());
    }

    @Override
    @RolesAllowed("MANAGEMENT")
    public UserEntity changePrivileges(Long id, List<AccessLevelDTO> accessLevelDTOList) throws AbstractAppException {
        UserEntity userEntity = userEntityFacade.findAndRefresh(id);
        if (userEntity == null) {
            throw UserNotFoundAppException.createUserWithProvidedIdNotFoundException(id);
        }
        accessLevelDTOList.forEach(accessLevelDTO -> userEntity.getAccessLevels()
                .stream()
                .filter(accessLevel -> accessLevelDTO.getAccessLevel().equals(accessLevel.getAccessLevel()))
                .findFirst().ifPresent(accessLevelEntity -> {
                    accessLevelEntity.setActive(accessLevelDTO.isActive());
                    accessLevelEntity.setVersion(accessLevelDTO.getVersion());
                }));


        buttonText = "content";
        StringBuilder accessLevelsFromList = new StringBuilder();
        for (AccessLevelEntity accesslevel : userEntity.getAccessLevels()) {
            if (accesslevel.isActive()) {
                accessLevelsFromList.append(accesslevel.getAccessLevel().toUpperCase()).append("<br>");
            }
        }
        footerText = accessLevelsFromList.toString();

        userEntityFacade.flush();
        userEntityFacade.refresh(userEntity);
        mailManager.createAndSendEmailFromTemplate(userEntity, "titleChangedAccessLevels", "headerChangeAccessLevel", buttonText, footerText);

        return userEntity;
    }

    /**
     * metoda managera pozwalająca na usunięcie użytkownika
     *
     * @param id - identyfikator konta
     */
    @Override
    @RolesAllowed("MANAGEMENT")
    public void deleteUser(Long id) throws AbstractAppException {

        UserEntity userEntity = userEntityFacade.findAndRefresh(id);
        if (userEntity == null) {
            throw UserNotFoundAppException.createUserWithProvidedIdNotFoundException(id);
        }
        if (userEntity.getLogin().startsWith("#")) {
            return;
        }
        userEntity.setActive(false);
        userEntity.setLogin("#" + userEntity.getLogin());
        userEntity.setEmail("#" + userEntity.getEmail());

        PersonalDataEntity personalDataEntity = userEntity.getPersonalData();

        personalDataEntity.setName(null);
        personalDataEntity.setSurname(null);
        personalDataEntity.setPhoneNumber(null);
        personalDataEntity.setLanguage("POL");
        userEntityFacade.flush();
        personalDataEntityMokFacade.flush();
    }

    @Override
    @PermitAll
    public UserEntity getSelfUser() throws AbstractAppException {
        return userEntityFacade.findByLogin(ctx.getCallerPrincipal().getName());
    }

    @Override
    @PermitAll
    public void deleteUnverifiedUsers(Integer systemSchedulerHour) throws AbstractAppException {
        List<UserEntity> userEntityList = userEntityFacade.findAll();
        for (UserEntity userEntity : userEntityList) {
            if (!userEntity.isVerified() && userEntity.getTokenTimestamp().until(OffsetDateTime.now(), ChronoUnit.HOURS) > systemSchedulerHour / 2) {
                buttonText = "https://studapp.it.p.lodz.pl:8405/ssbd05/resources/user/verify/?id=" + userEntity.getId() + "&token=" + userEntity.getPasswordResetToken();
                mailManager.createAndSendEmailFromTemplate(userEntity, "titleNearDeleteAccount", "headerTextNearDeleteAccount", buttonText, "footerTextDeleteAccount");
            }
            if (!userEntity.isVerified() && userEntity.getTokenTimestamp().until(OffsetDateTime.now(), ChronoUnit.HOURS) >= systemSchedulerHour) {
                buttonText = "https://studapp.it.p.lodz.pl:8405/ssbd05/#/register";
//                personalDataEntityMokFacade.remove(userEntity.getPersonalData());
                for (AccessLevelEntity ac : userEntity.getAccessLevels())
                    accessLevelEntityMokFacade.remove(ac);
//                userEntityFacade.flush();
                userEntityFacade.refresh(userEntity);
                userEntityFacade.remove(userEntity);
                userEntityFacade.flush();
                mailManager.createAndSendEmailFromTemplate(userEntity, "titleDeleteAccount", "headerTextDeleteAccount", buttonText, "footerTextDeleteAccount");
            }

        }
    }

    @Override
    @RolesAllowed("MANAGEMENT")
    public AccessLevelEntity changeAccessLevelStatus(Long id, boolean status) throws AbstractAppException {
        AccessLevelEntity accessLevelEntity = accessLevelEntityMokFacade.find(id);
        UserEntity userEnt = userEntityFacade.findAllAndRefresh()
                .stream()
                .filter(user -> user.getAccessLevels()
                        .stream()
                        .filter(level -> level.getId() == id)
                        .count() > 0)
                .findFirst()
                .orElseThrow(() -> UserNotFoundAppException.userWithAccessLevelNotFoundException());
        if (!userEnt.getLogin().equals(request.getRemoteUser())) {
            accessLevelEntity.setActive(status);
        } else {
            throw new EJBAccessException();
        }
        accessLevelEntityMokFacade.flush();


        return accessLevelEntityMokFacade.find(id);
    }

    @Override
    @PermitAll
    public SessionLogEntity getLastFailedLogin(Long id) throws AbstractAppException {

        UserEntity userEntity = userEntityFacade.find(id);
        if (userEntity == null) {
            throw UserNotFoundAppException.createUserWithProvidedLoginNotFoundException(ctx.getCallerPrincipal().getName());
        }
        SessionLogEntity log = sessionLogEntityMokFacade.findLastFailedLogin(userEntity.getId());
        if (null == log) {
            throw SessionLogNotFoundException.createSessionLogNotFoundException();
        }
        return log;
    }

    @Override
    @PermitAll
    public SessionLogEntity getLastSuccessfulLogin(Long id) throws AbstractAppException {

        UserEntity userEntity = userEntityFacade.find(id);
        if (userEntity == null) {
            throw UserNotFoundAppException.createUserWithProvidedLoginNotFoundException(ctx.getCallerPrincipal().getName());
        }
        SessionLogEntity log = sessionLogEntityMokFacade.findLastSuccessfulLogin(userEntity.getId());
        if (null == log) {
            throw SessionLogNotFoundException.createSessionSuccessfulLogNotFoundException();
        }
        return log;
    }

    @Override
    @PermitAll
    public SessionLogEntity getOwnLastFailedLogin() throws AbstractAppException {

        UserEntity userEntity = userEntityFacade.findByLogin(ctx.getCallerPrincipal().getName());
        if (userEntity == null) {
            throw UserNotFoundAppException.createUserWithProvidedLoginNotFoundException(ctx.getCallerPrincipal().getName());
        }
        SessionLogEntity log = sessionLogEntityMokFacade.findLastFailedLogin(userEntity.getId());
        if (null == log) {
            throw SessionLogNotFoundException.createSessionLogNotFoundException();
        }
        return log;
    }

    @Override
    @PermitAll
    public SessionLogEntity getOwnLastSuccessfulLogin() throws AbstractAppException {

        UserEntity userEntity = userEntityFacade.findByLogin(ctx.getCallerPrincipal().getName());
        if (userEntity == null) {
            throw UserNotFoundAppException.createUserWithProvidedLoginNotFoundException(ctx.getCallerPrincipal().getName());
        }
        SessionLogEntity log = sessionLogEntityMokFacade.findLastSuccessfulLogin(userEntity.getId());
        if (null == log) {
            throw SessionLogNotFoundException.createSessionSuccessfulLogNotFoundException();
        }
        return log;
    }
}
