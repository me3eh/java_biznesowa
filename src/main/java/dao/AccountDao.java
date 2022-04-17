package dao;


import model.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountDao extends GenericDao<Account,Long> {
    Account getByClass(String name, String address);

    Account accountByNameAndAddress(String name, String address);

    List<Account> accountByRangedBalance(BigDecimal firstAmount, BigDecimal secondAmount);

    List<Account> accountWithMaxBalance();

    List<Account> accountByPrefix(String name);

    List<Account> accountsWithNoAccountOperation();

    List<Account> accountsWithBiggestAmountOfOperations();
}