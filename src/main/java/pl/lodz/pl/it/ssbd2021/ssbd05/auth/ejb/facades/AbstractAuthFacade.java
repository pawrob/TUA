package pl.lodz.pl.it.ssbd2021.ssbd05.auth.ejb.facades;

import lombok.Getter;
import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.facades.AbstractFacade;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Getter
public abstract class AbstractAuthFacade<T> extends AbstractFacade<T> {

    @PersistenceContext(unitName = "ssbd05authPU")
    private EntityManager entityManager;

    protected AbstractAuthFacade(Class<T> entityClass) {
        super(entityClass);
    }
}
