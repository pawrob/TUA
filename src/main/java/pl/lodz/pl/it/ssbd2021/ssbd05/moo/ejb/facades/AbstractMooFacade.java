package pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades;

import lombok.Getter;
import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.facades.AbstractFacade;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Fasada abstrakcyjna zarządzania encjami z poziomu użytkownika ssbd05moo
 * @param <T> Zarządzana encja
 */
@Getter
public abstract class AbstractMooFacade<T> extends AbstractFacade<T> {
    @PersistenceContext(unitName = "ssbd05mooPU")
    private EntityManager entityManager;

    protected AbstractMooFacade(Class<T> entityClass) {
        super(entityClass);
    }
}
