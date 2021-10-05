package pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades;

import lombok.Getter;
import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.facades.AbstractFacade;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Fasada abstrakcyjna zarządzania encjami z poziomu użytkownika ssbd05mok
 * @param <T> Zarządzana encja
 */
@Getter
public abstract class AbstractMokFacade<T> extends AbstractFacade<T> {
    @PersistenceContext(unitName = "ssbd05mokPU")
    private EntityManager entityManager;

    protected AbstractMokFacade(Class<T> entityClass) {
        super(entityClass);
    }
}
