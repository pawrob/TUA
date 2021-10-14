package pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.managers;

import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.ManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.ClientEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;

@Local
public interface ClientMooManagerLocal extends ManagerLocal {
    @RolesAllowed({"CLIENT"})
    ClientEntity getClient(Long id) throws AbstractAppException;
}
