package model;

import dao.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name="getAccountByNameAndAddress",
                    query="SELECT b FROM Account b WHERE b.address=?1 AND b.name=?2"),
        @NamedQuery(name="getAccountByPrefix", query="SELECT b FROM Account b WHERE b.name LIKE ?1"),
        @NamedQuery(name="getAccountsWithBalanceInRange",
                    query="SELECT b FROM Account b where b.balance between ?1 and ?2"),
        @NamedQuery(name="getAccountWithMaxBalance",
                query="SELECT b FROM Account b WHERE b.balance = (SELECT max(a.balance)" +
                                                                    "FROM Account a)"),
        @NamedQuery(name="AccountWithNoAccountOperation",
                    query="SELECT b FROM Account b WHERE b.id NOT IN (SELECT DISTINCT o.account.id " +
                                                                        "FROM AccountOperation o)"),
        @NamedQuery(name="getAccountsWithBiggestAmountOfOperations",
                    query="select b " +
                            "from Account b " +
                            "where b.id IN (SELECT o.account.id " +
                                            "FROM AccountOperation o " +
                                            "GROUP BY o.account.id " +
                                            "HAVING COUNT(o.account.id) >= all( select count( c.account.id ) "+
                                                                            "from AccountOperation c " +
                                                                            "group by c.account.id ) )")
})
@Table(name="Account")
public class Account extends AbstractModel{
    private String name;
    private BigDecimal balance;
    private String address;

    @OneToMany(mappedBy = "account", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<AccountOperation> accountOperations = new LinkedList<>();

    @OneToMany(mappedBy = "account", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TransferOperation> transferOperations = new LinkedList<>();

    public Account(){
        balance = new BigDecimal("0");
    }

    public Account(String name, String address){
        this.name = name;
        this.address = address;
        balance = new BigDecimal("0");
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getName() {
        return name;
    }

    public void addAmount(BigDecimal balance){
        this.balance = this.balance.add(balance);
    }

    public boolean subtractAmount(BigDecimal balance){
        if(this.balance.compareTo(balance) < 0)
            return false;
        this.balance = this.balance.subtract(balance);
        return true;
    }

    public void addAccountOperation(BigDecimal amount, AccountOperation.OperationType type){
        AccountOperation aO = new AccountOperation(this, amount, type);
        AccountOperationDao dao = new AccountOperationDaoJpaImpl();
        dao.save(aO);
        accountOperations.add( aO );
    }

    public void addTransferOperation(BigDecimal amount, AccountOperation.OperationType type,
                                     Account otherAccount, String nameOfTransfer){
        TransferOperation aO = new TransferOperation(this, amount, type, nameOfTransfer, otherAccount);
        TransferOperationDao dao = new TransferOperationDaoJpaImpl();
        dao.save(aO);
        transferOperations.add( aO );
    }

    public String getAddress() {
        return address;
    }
}
