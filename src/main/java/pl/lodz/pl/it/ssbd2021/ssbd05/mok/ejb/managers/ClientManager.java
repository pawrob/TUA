package pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.managers;

import pl.lodz.pl.it.ssbd2021.ssbd05.entities.*;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.*;
import pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades.ClientEntityMokFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades.PersonalDataEntityMokFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades.UserEntityMokFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.HashGenerator;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.MailManager;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Komponent EJB zarządający metodami, które pozwalają na utworzenie i usunięcie konta użytkownika.
 */
@DenyAll
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors(EjbLoggerInterceptor.class)
public class ClientManager extends AbstractMokManager implements ClientManagerLocal {

    @Inject
    private UserEntityMokFacade userEntityMokFacade;

    @Inject
    private ClientEntityMokFacade clientEntityMokFacade;

    @Inject
    private MailManager mailManager;

    @Inject
    PersonalDataEntityMokFacade personalDataEntityMokFacade;

    @Resource
    private SessionContext ctx;

    private String buttonText;


    /**
     * Metoda managera pozwalająca na stworzenie uzytkownika w bazie danych.
     *
     * @param userEntity  - obiekt encji uzytkownika
     * @param accessLevel - obiekt encji poziomu dostepu
     * @return Response
     * @throws AbstractAppException         abstrakcyjny wyjątek aplikacyjny
     * @throws UniqueConstraintAppException rzucany w przypadku gdy podane dane są juz zajęte
     */
    @Override
    @PermitAll
    public UserEntity createUserAccountWithAccessLevel(UserEntity userEntity, AccessLevelEntity accessLevel) throws AbstractAppException {
        if (null != userEntityMokFacade.findByEmail(userEntity.getEmail()))
            throw UniqueConstraintAppException.createEmailTakenException();
        if (null != userEntityMokFacade.findByLogin(userEntity.getLogin()))
            throw UniqueConstraintAppException.createLoginTakenException();
        userEntity.setPassword(HashGenerator.generateHash(userEntity.getPassword()));
        String token = HashGenerator.generateSecureRandomToken();
        userEntity.setPasswordResetToken(token);
        List<AccessLevelEntity> AccessLevelList = new ArrayList<>();
        AccessLevelList.add(new ManagementEntity());
        AccessLevelList.add(new EntertainerEntity());
        AccessLevelList.add(new ClientEntity());
        List<AccessLevelEntity> AccessLevelFilteredList = AccessLevelList.stream().filter(a -> !a.getClass().equals(accessLevel.getClass())).collect(Collectors.toList());
        AccessLevelFilteredList.forEach(a -> {
            a.setActive(false);
            a.setUser(userEntity);
            userEntity.getAccessLevels().add(a);
        });
        accessLevel.setUser(userEntity);
        userEntity.getAccessLevels().add(accessLevel);


        userEntityMokFacade.create(userEntity);

        userEntityMokFacade.flush();
        PersonalDataEntity personalDataEntity = new PersonalDataEntity();
        personalDataEntity.setUser(userEntity);
        personalDataEntity.setUserId(userEntity.getId());
        userEntity.setPersonalData(personalDataEntity);
        userEntity.setTokenTimestamp(OffsetDateTime.now());

        userEntityMokFacade.flush();
        userEntityMokFacade.refresh(userEntity);
        buttonText = "https://studapp.it.p.lodz.pl:8405/ssbd05/#/accountConfirmation/?id=" + userEntity.getId() + "&token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        mailManager.createAndSendEmailFromTemplate(userEntity, "titleCreateAccount", "headerCreateAccount", buttonText, "footerCreateAccount");

        return userEntity;
    }

    /**
     * Metoda managera pozwalająca na usunięcie użytkownika.
     *
     * @param login    - identyfikator konta
     * @param password - hasło użytkownika o danym id
     * @throws AbstractAppException          abstrakcyjny wyjątek aplikacyjny
     * @throws OptimisticLockAppException    rzucany w przypadku naruszenia zasad struktury danych
     * @throws IncorrectPasswordAppException rzucany w przypadku gdy uzytkownik poda niepoprawne hasło
     * @throws UserNotFoundAppException      rzucany w przypadku gdy uzytkownik nie został znaleziony
     * @throws NotAllowedAppException        rzucany w przypadku gdy login uzytkownika przypisanego do konta jest inny niz uzytkownika usuwajacego konto
     */
    @Override
    @RolesAllowed({"CLIENT", "MANAGEMENT"})
    public void deleteUser(String login, String password) throws AbstractAppException {
        var userEntity = userEntityMokFacade.findByLogin(login);
        if (userEntity == null) {
            throw UserNotFoundAppException.createUserWithProvidedLoginNotFoundException(login);
        }
        if (userEntity.getLogin().startsWith("#")) {
            return;
        }

        if (!userEntity.getLogin().equals(ctx.getCallerPrincipal().getName()))
            throw NotAllowedAppException.createNotAllowedException();

        if (HashGenerator.checkPassword(password, userEntity.getPassword())) {
            userEntity.setActive(false);
            userEntity.setLogin("#" + login);
            userEntity.setEmail("#" + userEntity.getEmail());

            PersonalDataEntity personalDataEntity = userEntity.getPersonalData();

            personalDataEntity.setName(null);
            personalDataEntity.setSurname(null);
            personalDataEntity.setPhoneNumber(null);
            personalDataEntity.setLanguage("POL");
            userEntityMokFacade.flush();
            personalDataEntityMokFacade.flush();
        } else {
            throw IncorrectPasswordAppException.createIncorrectPasswordAppException();
        }
    }

    @Override
    @RolesAllowed({"ENTERTAINER"})
    public UserEntity getClientInfo(Long id) throws AbstractAppException {
        return clientEntityMokFacade.findByUserId(id).getUser();
    }
}
