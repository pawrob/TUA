package pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades;

import pl.lodz.pl.it.ssbd2021.ssbd05.entities.PersonalDataEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.OptimisticLockExceptionInterceptor;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.PersistenceExceptionInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Fasada gromadząca funkcjonalności do zarządzania encją PersonalData
 */
@Stateless
@LocalBean
@Interceptors({PersistenceExceptionInterceptor.class, OptimisticLockExceptionInterceptor.class})
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class PersonalDataEntityMokFacade extends AbstractMokFacade<PersonalDataEntity> {
    public PersonalDataEntityMokFacade() {
        super(PersonalDataEntity.class);
    }

    @Override
    @RolesAllowed({"Client", "Entertainer", "Management"})
    public PersonalDataEntity find(Object id) throws AbstractAppException {
        return super.find(id);
    }

    @Override
    @RolesAllowed({"Client", "Entertainer", "Management"})
    public void create(PersonalDataEntity entity) throws AbstractAppException {
        super.create(entity);
    }

    @Override
    @RolesAllowed({"Client", "Entertainer", "Management"})
    public void flush() throws AbstractAppException {
        super.flush();
    }

    /**
     * Metoda pozwala na wyszukiwanie użytkowników systemu podając frazę zawierającą część ich imienia lub nazwiska
     *
     * @param query - fraza zawierająca część imienia lub nazwiska
     * @return list - lista znalezionych kont uzytkowników
     */
    public List<PersonalDataEntity> findByNameOrSurname(Object query) {
        TypedQuery<PersonalDataEntity> tq = getEntityManager().createNamedQuery("PersonalDataEntity.findByNameOrSurname", PersonalDataEntity.class);
        tq.setParameter("name", "%" + query + "%");
        return tq.getResultList();
    }
}
