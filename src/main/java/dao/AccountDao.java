package dao;


import model.Account;

import java.util.Optional;

public interface AccountDao extends GenericDao<Account,Long> {
    public Account getByClass(String name, String address);
}


