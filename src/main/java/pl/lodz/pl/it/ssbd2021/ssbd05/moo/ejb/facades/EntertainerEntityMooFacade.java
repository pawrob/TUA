package pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades;

import pl.lodz.pl.it.ssbd2021.ssbd05.entities.EntertainerEntity;
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
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Fasada gromadząca funkcjonalności do zarządzania encją Entertainer
 */
@Stateless
@DenyAll
@LocalBean
@Interceptors({PersistenceExceptionInterceptor.class, OptimisticLockExceptionInterceptor.class})
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class EntertainerEntityMooFacade extends AbstractMooFacade<EntertainerEntity> {
    public EntertainerEntityMooFacade() {
        super(EntertainerEntity.class);
    }

    @Override
    @PermitAll
    public EntertainerEntity find(Object id) throws AbstractAppException {
        return super.find(id);
    }

    @Override
    @PermitAll
    public EntertainerEntity findAndRefresh(Object id) throws AbstractAppException {
        return super.findAndRefresh(id);
    }

    @RolesAllowed({"CLIENT"})
    public void lockPessimisticRead(EntertainerEntity entertainer){
        getEntityManager().lock(entertainer, LockModeType.PESSIMISTIC_READ);
    }

    @RolesAllowed({"ENTERTAINER"})
    public EntertainerEntity findByLogin(String login){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Object> cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<EntertainerEntity> root = cq.from(EntertainerEntity.class);
        cq.select(root).where(cb.equal(root.get("user").get("login"), login));
        Query q = getEntityManager().createQuery(cq);
        return (EntertainerEntity) q.getResultList().get(0);
    }

}
