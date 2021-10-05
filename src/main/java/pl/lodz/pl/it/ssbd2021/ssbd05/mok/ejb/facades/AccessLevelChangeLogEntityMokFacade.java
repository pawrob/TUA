package pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades;

import pl.lodz.pl.it.ssbd2021.ssbd05.entities.AccessLevelChangeLogEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.OptimisticLockExceptionInterceptor;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.PersistenceExceptionInterceptor;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

/**
 * Fasada gromadząca funkcjonalności do zarządzania encją AccessLevelChangeLog
 */
@Stateless
@LocalBean
@Interceptors({PersistenceExceptionInterceptor.class, OptimisticLockExceptionInterceptor.class})
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AccessLevelChangeLogEntityMokFacade extends AbstractMokFacade<AccessLevelChangeLogEntity> {
    public AccessLevelChangeLogEntityMokFacade() {
        super(AccessLevelChangeLogEntity.class);
    }
}
