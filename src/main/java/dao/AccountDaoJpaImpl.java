package dao;

import model.Account;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

public class AccountDaoJpaImpl extends GenericDaoJpaImpl<Account,Long> implements AccountDao {

    public Account getByClass(String name, String address){
        EntityManager em = getEntityManager();
        String hql = "FROM Account E WHERE E.name = :account_name AND E.address = :account_address";
        Query query = em.createQuery(hql);
        query.setParameter("account_name",name);
        query.setParameter("account_address",address);
        List<Account> account = query.getResultList();
        em.close();
        Account foundAccount = null;
        if( !account.isEmpty() )
            foundAccount = account.get(0);
        return foundAccount;
    }
}
