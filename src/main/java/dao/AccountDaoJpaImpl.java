package dao;

import model.Account;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
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
    public Account accountByNameAndAddress(String name, String address){
        EntityManager em = new AccountDaoJpaImpl().getEntityManager();
        TypedQuery<Account> query = em.createNamedQuery("getAccountByNameAndAddress", Account.class);
        query.setParameter(1, address);
        query.setParameter(2, name);
        List<Account> results = query.getResultList();
        if( results.isEmpty() )
            return null;
        return results.get(0);
    }
    public List<Account> accountByPrefix(String name){
        EntityManager em = getEntityManager();
        TypedQuery<Account> query = em.createNamedQuery("getAccountByPrefix", Account.class);
        query.setParameter(1, name + "%");
        List<Account> results = query.getResultList();
        if( results.isEmpty() )
            return null;
        return results;
    }

    public List<Account> accountByRangedBalance(BigDecimal firstAmount, BigDecimal secondAmount){
        EntityManager em = getEntityManager();
        TypedQuery<Account> query = em.createNamedQuery("getAccountsWithBalanceInRange", Account.class);
        query.setParameter(1, firstAmount);
        query.setParameter(2, secondAmount);

        List<Account> results = query.getResultList();
        if( results.isEmpty() )
            return null;
        return results;
    }

    public List<Account> accountWithMaxBalance(){
        EntityManager em = getEntityManager();
        TypedQuery<Account> query = em.createNamedQuery("getAccountWithMaxBalance", Account.class);

        List<Account> results = query.getResultList();
        if( results.isEmpty() )
            return null;
        return results;
    }

    public List<Account> accountsWithNoAccountOperation(){
        EntityManager em = getEntityManager();
        TypedQuery<Account> query = em.createNamedQuery("AccountWithNoAccountOperation", Account.class);

        List<Account> results = query.getResultList();
        if( results.isEmpty() )
            return null;
        return results;
    }

    public List<Account> accountsWithBiggestAmountOfOperations(){
        EntityManager em = getEntityManager();
        TypedQuery<Account> query = em.createNamedQuery("getAccountsWithBiggestAmountOfOperations", Account.class);

        List<Account> results = query.getResultList();
        if( results.isEmpty() )
            return null;
        return results;
    }




}
