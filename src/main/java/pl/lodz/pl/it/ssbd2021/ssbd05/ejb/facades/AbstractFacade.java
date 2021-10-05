package pl.lodz.pl.it.ssbd2021.ssbd05.ejb.facades;

import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.PersistenceExceptionInterceptor;

import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;


/**
 * Klasa abstrakcyjna reprezentująca fasadę gromadzącą funkcjonalności zarządzania encjami
 */
@Interceptors(PersistenceExceptionInterceptor.class)
public abstract class AbstractFacade<T> {
    private final Class<T> entityClass;

    public Class<T> getEntityClass() {
        return entityClass;
    }

    protected AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void flush() throws AbstractAppException {
        getEntityManager().flush();
    }

    public void create(T entity) throws AbstractAppException {
        getEntityManager().persist(entity);
    }

    public void edit(T entity) {
        getEntityManager().merge(entity);
    }

    public T find(Object id) throws AbstractAppException {
        return getEntityManager().find(getEntityClass(), id);
    }

    public T findAndRefresh(Object id) throws AbstractAppException {
        T result = find(id);
        if (result != null) {
            getEntityManager().refresh(result);
        }
        return result;
    }

    public void refresh(Object entity) throws AbstractAppException {
        getEntityManager().refresh(entity);
    }

    public List<T> findAll() throws AbstractAppException {
        CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(getEntityClass());
        cq.select(cq.from(getEntityClass()));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findAllAndRefresh() throws AbstractAppException {
        List<T> result = findAll();
        result.forEach(t -> getEntityManager().refresh(t));
        return result;
    }

    public List<T> findRange(int[] range) {
        CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(getEntityClass());
        cq.select(cq.from(getEntityClass()));
        TypedQuery<T> q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count() {
        CriteriaQuery<Object> cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<T> rt = cq.from(getEntityClass());
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
        getEntityManager().flush();
    }
}
