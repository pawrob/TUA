package pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades;

import pl.lodz.pl.it.ssbd2021.ssbd05.entities.ClientEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.OptimisticLockExceptionInterceptor;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.PersistenceExceptionInterceptor;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Fasada gromadząca funkcjonalności do zarządzania encją ClientEntity
 */
@Stateless
@LocalBean
@DenyAll
@Interceptors({PersistenceExceptionInterceptor.class, OptimisticLockExceptionInterceptor.class})
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ClientEntityMokFacade extends AbstractMokFacade<ClientEntity> {
    public ClientEntityMokFacade() {
        super(ClientEntity.class);
    }

    @RolesAllowed({"ENTERTAINER"})
    public ClientEntity findByUserId(Long id){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Object> cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<ClientEntity> root = cq.from(ClientEntity.class);
        cq.select(root).where(cb.equal(root.get("user").get("id"), id));
        Query q = getEntityManager().createQuery(cq);
        return (ClientEntity) q.getResultList().get(0);
    }
}
