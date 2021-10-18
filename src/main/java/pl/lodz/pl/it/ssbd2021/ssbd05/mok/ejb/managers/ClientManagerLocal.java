package pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.managers;

import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.ManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.AccessLevelEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.UserEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;

@Local
public interface ClientManagerLocal extends ManagerLocal {
    @PermitAll
    UserEntity createUserAccountWithAccessLevel(UserEntity userEntity, AccessLevelEntity accessLevel) throws AbstractAppException;

    @RolesAllowed({"CLIENT", "MANAGEMENT"})
    void deleteUser(String login, String password) throws AbstractAppException;

    @RolesAllowed({"ENTERTAINER"})
    UserEntity getClientInfo(Long id) throws AbstractAppException;
}
