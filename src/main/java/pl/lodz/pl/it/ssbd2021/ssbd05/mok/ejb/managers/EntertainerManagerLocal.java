package pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.managers;

import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.ManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.AccessLevelEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.EntertainerEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.UserEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.PersistenceExceptionInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.interceptor.Interceptors;

@Local
public interface EntertainerManagerLocal extends ManagerLocal {
    @RolesAllowed("Management")
    UserEntity deactivateEntertainerAccount(Long id) throws AbstractAppException;

    @RolesAllowed("Management")
    UserEntity reactivateEntertainerAccount(Long id) throws AbstractAppException;

    @RolesAllowed("Management")
    @Interceptors(PersistenceExceptionInterceptor.class)
    UserEntity createEnterainer(EntertainerEntity entertainerEntity) throws AbstractAppException;

    @RolesAllowed("Management")
    AccessLevelEntity changeEntertainerAccessLevelStatus(Long id, boolean status) throws AbstractAppException;
}
