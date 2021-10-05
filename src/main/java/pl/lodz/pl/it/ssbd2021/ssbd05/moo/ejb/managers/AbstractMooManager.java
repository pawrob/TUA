package pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.managers;

import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.AbstractManager;

/**
 * Bazowa klasa dla menedżerów Modułu Obsługi Ofert (MOO)
 */
public abstract class AbstractMooManager extends AbstractManager {
    @Override
    protected String getFunctionalModuleName() {
        return "moo";
    }
}
