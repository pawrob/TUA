package pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades;

import pl.lodz.pl.it.ssbd2021.ssbd05.entities.ClientEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.OfferEntity;
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
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Fasada gromadząca funkcjonalności do zarządzania encją Offer
 */
@Stateless
@LocalBean
@DenyAll
@Interceptors({PersistenceExceptionInterceptor.class, OptimisticLockExceptionInterceptor.class})
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class OfferEntityMooFacade extends AbstractMooFacade<OfferEntity> {

    public OfferEntityMooFacade() {
        super(OfferEntity.class);
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
    public List<OfferEntity> findAllAndRefresh() throws AbstractAppException {
        return super.findAllAndRefresh();
    }

    @Override
    @PermitAll
    public OfferEntity findAndRefresh(Object id) throws AbstractAppException {
        return super.findAndRefresh(id);
    }

    @Override
    @PermitAll
    public OfferEntity find(Object id) throws AbstractAppException {
        return super.find(id);
    }

    @Override
    @RolesAllowed("Entertainer")
    public void create(OfferEntity offer) throws AbstractAppException {
        super.create(offer);
    }

    @PermitAll
    public List<OfferEntity> findAllFavouritesOffers(ClientEntity user) {
        TypedQuery<OfferEntity> tq = getEntityManager().createNamedQuery("OfferEntity.findAllFavouritesOffers", OfferEntity.class);
        tq.setParameter("clientid", user.getId());
        try {
            return tq.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

}
