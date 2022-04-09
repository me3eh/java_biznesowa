package model;

import dao.TransferOperationDao;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="TransferOperation")
@Inheritance(strategy= InheritanceType.JOINED)
public class TransferOperation extends AccountOperation{

    private String nameOfTransfer;

    @ManyToOne
    private Account otherAccount;

    public TransferOperation(Account account, BigDecimal amount,
                             OperationType type, String nameOfTransfer, Account otherAccount){
        super(account, amount, type);
        this.nameOfTransfer =  nameOfTransfer;
        this.otherAccount = otherAccount;
    }

    public TransferOperation() {

    }
}
