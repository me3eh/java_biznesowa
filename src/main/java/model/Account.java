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

    public void addAmount(BigDecimal balance){
        System.out.println( this.balance );
        this.balance = this.balance.add(balance);
        System.out.println( this.balance );
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
}
