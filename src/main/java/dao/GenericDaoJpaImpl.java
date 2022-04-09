package dao;

import dao.GenericDao;
import utils.JpaFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public class GenericDaoJpaImpl<T, K> implements GenericDao<T, K> {

    protected final Class<T> type;

    public GenericDaoJpaImpl() {
        Type t = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        type = (Class) pt.getActualTypeArguments()[0];
    }

    @Override
    public void save(T t) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        em.persist(t);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void delete(T t) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        t = em.merge(t);
        em.remove(t);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void update(T t) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        em.merge(t);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public Optional<T> findById(K id) {
        EntityManager em = getEntityManager();
        T dto = em.find(type, id);
        em.close();
        return Optional.ofNullable(dto);
    }

    @Override
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        Root<T> rootEntry = cq.from(type);
        CriteriaQuery<T> all = cq.select(rootEntry);
        TypedQuery<T> allQuery = em.createQuery(all);
        return allQuery.getResultList();
    }

    protected EntityManager getEntityManager() {
        return JpaFactory.getEntityManager();
    }
}