package pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades;

import pl.lodz.pl.it.ssbd2021.ssbd05.entities.ClientEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.ReservationEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.ReservationAppException;
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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Fasada gromadząca funkcjonalności do zarządzania encją Reservation
 */
@DenyAll
@Stateless
@LocalBean
@Interceptors({PersistenceExceptionInterceptor.class, OptimisticLockExceptionInterceptor.class})
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ReservationEntityMooFacade extends AbstractMooFacade<ReservationEntity> {
    public ReservationEntityMooFacade() {
        super(ReservationEntity.class);
    }

    @PermitAll
    public List<ReservationEntity> findReservationByClient(ClientEntity user) {
        TypedQuery<ReservationEntity> tq = getEntityManager().createNamedQuery("ReservationEntity.findByClient", ReservationEntity.class);
        tq.setParameter("client", user);
        try {
            return tq.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    @Override
    @PermitAll
    public ReservationEntity findAndRefresh(Object id) throws AbstractAppException {
        return super.findAndRefresh(id);
    }

    @Override
    @RolesAllowed({"CLIENT"})
    public ReservationEntity find(Object id) throws AbstractAppException {
        return super.find(id);
    }

    @Override
    @RolesAllowed({"CLIENT"})
    public void create(ReservationEntity entity) throws AbstractAppException {
        super.create(entity);
    }

    @Override
    @RolesAllowed({"CLIENT", "ENTERTAINER"})
    public void flush() throws AbstractAppException {
        super.flush();
    }
    @RolesAllowed({"CLIENT", "ENTERTAINER"})
    public List<ReservationEntity> findAllForEntertainer(Long entertainer_id){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Object> cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<ReservationEntity> root = cq.from(ReservationEntity.class);
        cq.select(root).where(cb.equal(root.get("offer").get("entertainer").get("id"), entertainer_id));
        Query q = getEntityManager().createQuery(cq);
        return q.getResultList();
    }

    @RolesAllowed({"CLIENT", "ENTERTAINER"})
    public ReservationEntity findForEntertainer(Long reservation_id, Long entertainer_id) throws AbstractAppException {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Object> cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<ReservationEntity> root = cq.from(ReservationEntity.class);
        cq.select(root).where(
                cb.equal(root.get("offer").get("entertainer").get("id"), entertainer_id),
                cb.equal(root.get("id"), reservation_id)
        );
        Query q = getEntityManager().createQuery(cq);
        List resultList = q.getResultList();
        if (resultList.isEmpty())
            throw ReservationAppException.createReservationNotFoundAppException(reservation_id);
        return (ReservationEntity) resultList.get(0);
    }

    @Override
    @RolesAllowed({"ENTERTAINER", "CLIENT"})
    public void edit(ReservationEntity entity) {
        super.edit(entity);
    }
}
