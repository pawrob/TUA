package pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades;

import pl.lodz.pl.it.ssbd2021.ssbd05.entities.ManagementEntity;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Fasada gromadząca funkcjonalności do zarządzania encją Management
 */
@Stateless
@LocalBean
public class ManagementEntityMooFacade extends AbstractMooFacade<ManagementEntity> {
    public ManagementEntityMooFacade() {
        super(ManagementEntity.class);
    }
}
