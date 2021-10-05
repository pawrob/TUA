package pl.lodz.pl.it.ssbd2021.ssbd05.auth.ejb.managers;

import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.ManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.DatabaseErrorAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.IncorrectCredentialsAppException;

import javax.ejb.Local;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;

@Local
public interface AuthenticationManagerLocal extends ManagerLocal {

    CredentialValidationResult authenticate(UsernamePasswordCredential credential) throws AbstractAppException;

    boolean checkIfUserExists(String username) throws DatabaseErrorAppException, IncorrectCredentialsAppException, AbstractAppException;
}
