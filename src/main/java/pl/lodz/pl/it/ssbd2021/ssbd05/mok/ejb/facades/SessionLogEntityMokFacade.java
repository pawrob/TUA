package pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades;

import pl.lodz.pl.it.ssbd2021.ssbd05.entities.SessionLogEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.OptimisticLockExceptionInterceptor;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.PersistenceExceptionInterceptor;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

/**
 * Fasada gromadząca funkcjonalności do zarządzania encją SessionLog
 */
@Stateless
@LocalBean
@DenyAll
@Interceptors({PersistenceExceptionInterceptor.class, OptimisticLockExceptionInterceptor.class})
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class SessionLogEntityMokFacade extends AbstractMokFacade<SessionLogEntity> {
    public SessionLogEntityMokFacade() {
        super(SessionLogEntity.class);
    }

    @Override
    @PermitAll
    public void create(SessionLogEntity entity) throws AbstractAppException {
        super.create(entity);
    }

    @PermitAll
    public SessionLogEntity findLastFailedLogin(Object id) throws AbstractAppException {

        TypedQuery<SessionLogEntity> tq = getEntityManager().createNamedQuery("UserEntity.lastFailedLogin", SessionLogEntity.class);
        tq.setParameter("id", id);
        try {
            SessionLogEntity singleResult = tq.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            return tq.getResultList().get(0);
        }
    }

    @PermitAll
    public SessionLogEntity findLastSuccessfulLogin(Object id) throws AbstractAppException {

        TypedQuery<SessionLogEntity> tq = getEntityManager().createNamedQuery("UserEntity.lastSuccessfulLogin", SessionLogEntity.class);
        tq.setParameter("id", id);
        try {
            SessionLogEntity singleResult = tq.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            return tq.getResultList().get(0);
        }
    }
}
