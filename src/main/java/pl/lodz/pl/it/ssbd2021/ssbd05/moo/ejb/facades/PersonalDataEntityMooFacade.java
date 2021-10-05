package pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades;

import pl.lodz.pl.it.ssbd2021.ssbd05.entities.PersonalDataEntity;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Fasada gromadząca funkcjonalności do zarządzania encją PersonalData
 */
@Stateless
@LocalBean
public class PersonalDataEntityMooFacade extends AbstractMooFacade<PersonalDataEntity> {
    public PersonalDataEntityMooFacade() {
        super(PersonalDataEntity.class);
    }
}
