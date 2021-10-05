package pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades;

import pl.lodz.pl.it.ssbd2021.ssbd05.entities.OfferAvailabilityEntity;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Fasada gromadząca funkcjonalności do zarządzania encją OfferAvailability
 */
@Stateless
@LocalBean
public class OfferAvailabilityEntityMooFacade extends AbstractMooFacade<OfferAvailabilityEntity> {
    public OfferAvailabilityEntityMooFacade() {
        super(OfferAvailabilityEntity.class);
    }
}
