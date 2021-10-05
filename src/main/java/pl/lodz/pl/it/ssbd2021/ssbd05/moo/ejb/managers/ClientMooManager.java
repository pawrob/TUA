package pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.managers;

import pl.lodz.pl.it.ssbd2021.ssbd05.entities.ClientEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades.ClientEntityMooFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.EjbLoggerInterceptor;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

@DenyAll
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors(EjbLoggerInterceptor.class)
public class ClientMooManager extends AbstractMooManager implements ClientMooManagerLocal {

    @Inject
    ClientEntityMooFacade clientFacade;

    @Override
    @RolesAllowed({"Client"})
    public ClientEntity getClient(Long id) throws AbstractAppException {
        return clientFacade.find(id);
    }
}
