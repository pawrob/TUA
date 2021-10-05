package pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.managers;

import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.AbstractManager;

/**
 * Klasa abstrakcyjna managera modułu mok.
 */
public class AbstractMokManager extends AbstractManager {
    @Override
    protected String getFunctionalModuleName() {
        return "mok";
    }
}
