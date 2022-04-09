package model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="AccountOperation")
public class AccountOperation extends AbstractModel{

    public enum OperationType { DEPOSIT, WITHDRAW, TRANSFER}
    @ManyToOne
    private Account account;
    private BigDecimal amount;
    private OperationType type;
    public AccountOperation(Account account, BigDecimal amount, OperationType type){
        this.account = account;
        this.amount = amount;
        this.type = type;
    }
    public AccountOperation() {}

}
