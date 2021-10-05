package pl.lodz.pl.it.ssbd2021.ssbd05.auth.ejb.facades;

import lombok.Getter;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.AuthenticationViewEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.DatabaseErrorAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.IncorrectCredentialsAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.PersistenceExceptionInterceptor;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.HashGenerator;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Fasada gromadząca funkcjonalności do zarządzania encją AuthenticationView
 */
@Getter
@Stateless
@LocalBean
@Interceptors(PersistenceExceptionInterceptor.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AuthenticationViewEntityFacade extends AbstractAuthFacade<AuthenticationViewEntity> {

    public AuthenticationViewEntityFacade() {
        super(AuthenticationViewEntity.class);
    }

    /**
     * Zwraca listę informacji o użytkowniku na podstawie loginu i hasła
     * @param login login
     * @param password hasło
     * @return lista danych użyktownika i jego roli
     * @throws IncorrectCredentialsAppException
     * @throws DatabaseErrorAppException
     */
    public List<AuthenticationViewEntity> findByLoginAndPassword(String login, String password) throws IncorrectCredentialsAppException, DatabaseErrorAppException {
        List<AuthenticationViewEntity> users = new ArrayList<>();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Object> cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<AuthenticationViewEntity> rt = cq.from(getEntityClass());
        cq.select(rt).where(cb.equal(rt.get("login"), login));
        Query q = getEntityManager().createQuery(cq);
        users = q.getResultList();
        if (users.isEmpty())
            return null;
        for (AuthenticationViewEntity user : users) {
            if (!HashGenerator.checkPassword(password, user.getPassword()))
                throw new IncorrectCredentialsAppException();
        }
        return users;
    }

    /**
     *
     * Zwraca listę informacji o użytkowniku na podstawie loginu
     * @param login login
     * @return lista danych użyktownika i jego roli
     * @throws IncorrectCredentialsAppException
     * @throws DatabaseErrorAppException
     */
    public List<AuthenticationViewEntity> findByLogin(String login) throws IncorrectCredentialsAppException, DatabaseErrorAppException{
        List<AuthenticationViewEntity> users = new ArrayList<>();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Object> cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<AuthenticationViewEntity> rt = cq.from(getEntityClass());
        cq.select(rt).where(cb.equal(rt.get("login"), login));
        Query q = getEntityManager().createQuery(cq);
        users = q.getResultList();
        return users;
    }
}
