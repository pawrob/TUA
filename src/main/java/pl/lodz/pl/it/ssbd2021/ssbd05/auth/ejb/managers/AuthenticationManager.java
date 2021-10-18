package pl.lodz.pl.it.ssbd2021.ssbd05.auth.ejb.managers;

import pl.lodz.pl.it.ssbd2021.ssbd05.auth.ejb.facades.AuthenticationViewEntityFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.AuthenticationViewEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import java.util.List;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@DenyAll
public class AuthenticationManager extends AbstractAuthManager implements AuthenticationManagerLocal {

    @Inject
    IdentityStoreHandler identityStoreHandler;

    @Inject
    AuthenticationViewEntityFacade authenticationViewEntityFacade;

    @PermitAll
    @Override
    public CredentialValidationResult authenticate(UsernamePasswordCredential credential) throws AbstractAppException {
        return identityStoreHandler.validate(credential);
    }

    @PermitAll
    @Override
    public boolean checkIfUserExists(String username) throws AbstractAppException {
        List<AuthenticationViewEntity> byLogin = authenticationViewEntityFacade.findByLogin(username);
        return !byLogin.isEmpty();
    }
}
