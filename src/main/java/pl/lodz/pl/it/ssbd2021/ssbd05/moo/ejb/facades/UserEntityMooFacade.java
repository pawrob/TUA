package pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades;

import pl.lodz.pl.it.ssbd2021.ssbd05.entities.UserEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.OptimisticLockExceptionInterceptor;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.PersistenceExceptionInterceptor;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.PermitAll;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 * Fasada gromadząca funkcjonalności do zarządzania encją User
 */
@Stateless
@LocalBean
@DenyAll
@Interceptors({PersistenceExceptionInterceptor.class, OptimisticLockExceptionInterceptor.class})
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class UserEntityMooFacade extends AbstractMooFacade<UserEntity>{
    public UserEntityMooFacade() {
        super(UserEntity.class);
    }


    @PermitAll
    public UserEntity findByLogin(Object login) throws AbstractAppException {
        TypedQuery<UserEntity> tq = getEntityManager().createNamedQuery("UserEntity.findByLogin", UserEntity.class);
        tq.setParameter("login", login);
        try {
            return tq.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @PermitAll
    public UserEntity find(Object id) throws AbstractAppException {
        return super.find(id);
    }

}
