package pl.lodz.pl.it.ssbd2021.ssbd05.auth.ejb.managers;

import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.AbstractManager;


/**
 * Fasada abstrakcyjna zarządzania encjami z poziomu użytkownika ssbd05auth
 */
public class AbstractAuthManager extends AbstractManager {
    @Override
    protected String getFunctionalModuleName() {
        return "auth";
    }
}
