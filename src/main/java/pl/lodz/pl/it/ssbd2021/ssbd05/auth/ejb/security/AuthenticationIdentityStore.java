package pl.lodz.pl.it.ssbd2021.ssbd05.auth.ejb.security;

import lombok.Data;
import pl.lodz.pl.it.ssbd2021.ssbd05.auth.ejb.facades.AuthenticationViewEntityFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.AuthenticationViewEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.DatabaseErrorAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.IncorrectCredentialsAppException;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Klasa zarządzająca walidacją poświadczeń użytkownika i przechowywaniem powiązanych loginów z poziomami dostępów.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AuthenticationIdentityStore implements IdentityStore {

    @Inject
    AuthenticationViewEntityFacade facade;

    /**
     * Metoda walidująca przekazane poświadczenia użytkowika.
     *
     * @param credential - poświadczenia użytkownika
     * @return NOT_VALIDATED,
     * INVALID,
     * VALID
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CredentialValidationResult validate(Credential credential) {
        if (credential instanceof UsernamePasswordCredential) {
            UsernamePasswordCredential usernamePasswordCredential = (UsernamePasswordCredential) credential;
            List<AuthenticationViewEntity> userRoles = null;

            try {
                userRoles = facade
                        .findByLoginAndPassword(
                                usernamePasswordCredential.getCaller(), usernamePasswordCredential.getPasswordAsString()
                        );
            } catch (IncorrectCredentialsAppException e) {
                return CredentialValidationResult.INVALID_RESULT;
            } catch (DatabaseErrorAppException e) {
                return CredentialValidationResult.NOT_VALIDATED_RESULT;
            }

            if (null != userRoles) {
                LoginAccessLevels loginAccessLevels = new LoginAccessLevels(userRoles);
                return new CredentialValidationResult(loginAccessLevels.getLogin(), new HashSet<>(loginAccessLevels.getAccessLevels()));
            } else {
                return CredentialValidationResult.INVALID_RESULT;
            }
        }
        return CredentialValidationResult.NOT_VALIDATED_RESULT;

    }

    /**
     * Metoda pobierająca role użytkownika
     *
     * @param validationResult wynik walidacji danych uwierzytelniających
     * @return grupy wywołującego metodę
     */
    @Override
    public Set<String> getCallerGroups(CredentialValidationResult validationResult) {
        return IdentityStore.super.getCallerGroups(validationResult);
    }

    /**
     * Klasa wewnętrzna przechowująca poziomy dostępu przypisane do wskazanego loginu.
     */
    @Data
    static class LoginAccessLevels {
        private String login;
        private List<String> accessLevels;

        public LoginAccessLevels(List<AuthenticationViewEntity> userRoles) {
            this.login = userRoles.get(0).getLogin();
            accessLevels = new ArrayList<>();
            userRoles.forEach(x -> accessLevels.add(x.getAccessLevel()));
        }
    }


}
