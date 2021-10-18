package pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades;

import pl.lodz.pl.it.ssbd2021.ssbd05.entities.AccessLevelEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.OptimisticLockExceptionInterceptor;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.PersistenceExceptionInterceptor;

import javax.annotation.security.PermitAll;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * Fasada gromadząca funkcjonalności do zarządzania encją AccessLevel
 */
@Stateless
@LocalBean
@PermitAll
@Interceptors({PersistenceExceptionInterceptor.class, OptimisticLockExceptionInterceptor.class})
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AccessLevelEntityMokFacade extends AbstractMokFacade<AccessLevelEntity> {
    public AccessLevelEntityMokFacade() {
        super(AccessLevelEntity.class);
    }

    @Override
    public AccessLevelEntity findAndRefresh(Object id) throws AbstractAppException {
        return super.findAndRefresh(id);
    }

    @Override
    public List<AccessLevelEntity> findAllAndRefresh() throws AbstractAppException {
        return super.findAllAndRefresh();
    }

    @Override
    public AccessLevelEntity find(Object id) throws AbstractAppException {
        return super.find(id);
    }

    @Override
    public List<AccessLevelEntity> findAll() throws AbstractAppException {
        return super.findAll();
    }

    @Override
    public void flush() throws AbstractAppException {
        super.flush();
    }
}
