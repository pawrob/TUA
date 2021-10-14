package pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades;

import pl.lodz.pl.it.ssbd2021.ssbd05.entities.UserEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.OptimisticLockExceptionInterceptor;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.PersistenceExceptionInterceptor;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import java.util.List;


/**
 * Fasada gromadząca funkcjonalności do zarządzania encją UserEntity
 */
@Stateless
@LocalBean
@DenyAll
@Interceptors({PersistenceExceptionInterceptor.class, OptimisticLockExceptionInterceptor.class})
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class UserEntityMokFacade extends AbstractMokFacade<UserEntity> {

    public UserEntityMokFacade() {
        super(UserEntity.class);
    }

    @Override
    @PermitAll
    public void create(UserEntity entity) throws AbstractAppException {
        super.create(entity);
    }

    @Override
    @PermitAll
    public void flush() throws AbstractAppException {
        super.flush();
    }

    @Override
    @PermitAll
    public void refresh(Object entity) throws AbstractAppException {
        super.refresh(entity);
    }

    @Override
    @PermitAll
    public List<UserEntity> findAllAndRefresh() throws AbstractAppException {
        return super.findAllAndRefresh();
    }

    @Override
        @RolesAllowed({"MANAGEMENT", "CLIENT"})
    public UserEntity findAndRefresh(Object id) throws AbstractAppException {
        return super.findAndRefresh(id);
    }

    @Override
    @PermitAll
    public UserEntity find(Object id) throws AbstractAppException {
        return super.find(id);
    }

    @Override
    @PermitAll
    public void edit(UserEntity entity) {
        super.edit(entity);
    }

    @PermitAll
    public UserEntity findByEmail(Object email) {
        TypedQuery<UserEntity> tq = getEntityManager().createNamedQuery("UserEntity.findByEmail", UserEntity.class);
        tq.setParameter("email", email);
        try {
            UserEntity singleResult = tq.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            return tq.getResultList().get(0);
        }
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


    @PermitAll
    @Override
    public void remove(UserEntity entity) {
        super.remove(entity);
    }
}
