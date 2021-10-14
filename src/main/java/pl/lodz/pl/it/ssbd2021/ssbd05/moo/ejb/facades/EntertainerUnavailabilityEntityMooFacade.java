package pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades;

import pl.lodz.pl.it.ssbd2021.ssbd05.entities.EntertainerUnavailabilityEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;

/**
 * Fasada gromadząca funkcjonalności do zarządzania encją EntertainerUnavailability
 */
@Stateless
@LocalBean
@DenyAll
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class EntertainerUnavailabilityEntityMooFacade extends AbstractMooFacade<EntertainerUnavailabilityEntity> {
    public EntertainerUnavailabilityEntityMooFacade() {
        super(EntertainerUnavailabilityEntity.class);
    }

    @Override
    @RolesAllowed({"ENTERTAINER"})
    public void create(EntertainerUnavailabilityEntity entity) throws AbstractAppException {
        super.create(entity);
    }

    @PermitAll
    @Override
    public EntertainerUnavailabilityEntity find(Object id) throws AbstractAppException {
        return super.find(id);
    }

    @PermitAll
    @Override
    public EntertainerUnavailabilityEntity findAndRefresh(Object id) throws AbstractAppException {
        return super.findAndRefresh(id);
    }

    @Override
    public void refresh(Object entity) throws AbstractAppException {
        super.refresh(entity);
    }

    @PermitAll
    @Override
    public List<EntertainerUnavailabilityEntity> findAll() throws AbstractAppException {
        return super.findAll();
    }

    @PermitAll
    @Override
    public List<EntertainerUnavailabilityEntity> findAllAndRefresh() throws AbstractAppException {
        return super.findAllAndRefresh();
    }


    @Override
    @PermitAll
    public void flush() throws AbstractAppException {
        super.flush();
    }
}
