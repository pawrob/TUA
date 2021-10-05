package pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades;

import pl.lodz.pl.it.ssbd2021.ssbd05.entities.ManagementEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.OptimisticLockExceptionInterceptor;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.PersistenceExceptionInterceptor;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

/**
 * Fasada gromadząca funkcjonalności do zarządzania encją Management
 */
@Stateless
@LocalBean
@Interceptors({PersistenceExceptionInterceptor.class, OptimisticLockExceptionInterceptor.class})
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ManagementEntityMokFacade extends AbstractMokFacade<ManagementEntity> {
    public ManagementEntityMokFacade() {
        super(ManagementEntity.class);
    }
}
