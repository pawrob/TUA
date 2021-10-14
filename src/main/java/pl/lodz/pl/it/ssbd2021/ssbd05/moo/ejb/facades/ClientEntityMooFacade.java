package pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades;

import pl.lodz.pl.it.ssbd2021.ssbd05.entities.ClientEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.UserEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.UserNotFoundAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.OptimisticLockExceptionInterceptor;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.PersistenceExceptionInterceptor;

import javax.annotation.security.PermitAll;
import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * Fasada gromadząca funkcjonalności do zarządzania encją ClientEntity
 */
@DenyAll
@Stateless
@LocalBean
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({PersistenceExceptionInterceptor.class, OptimisticLockExceptionInterceptor.class})
public class ClientEntityMooFacade extends AbstractMooFacade<ClientEntity> {
    public ClientEntityMooFacade() {
        super(ClientEntity.class);
    }

    @PermitAll
    public ClientEntity findByUser(UserEntity user) throws AbstractAppException {
        TypedQuery<ClientEntity> tq = getEntityManager().createNamedQuery("ClientEntity.findByUser", ClientEntity.class);
        tq.setParameter("user", user);
        try {
            return tq.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @RolesAllowed({"CLIENT"})
    public ClientEntity find(Object id) throws AbstractAppException {
        Long userId = (long) id;
        List<ClientEntity> allClients = super.findAll();
        return allClients.stream().filter(clientEntity -> clientEntity.getUser().getId() == userId)
                .findFirst().orElseThrow(() -> UserNotFoundAppException.createUserWithProvidedIdNotFoundException(userId));
    }

    @RolesAllowed({"CLIENT"})
    public ClientEntity findByLogin(Object login) throws AbstractAppException {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Object> cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<ClientEntity> client = cq.from(ClientEntity.class);
        Join<ClientEntity, UserEntity> userEntityJoin = client.join("user", JoinType.LEFT);
        cq.select(client).where(cb.equal(userEntityJoin.get("login"), login));
        Query q = getEntityManager().createQuery(cq);
        return (ClientEntity) q.getResultList().get(0);
    }
}
