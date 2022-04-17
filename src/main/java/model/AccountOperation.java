package model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="AccountOperation")
@NamedQueries({
                @NamedQuery(name="operationsOnSpecificAccountInRangeOfDates",
                        query="SELECT o FROM AccountOperation o " +
                                "WHERE o.account.id=?1 " +
                                "AND o.creationDate BETWEEN ?2 AND ?3"),
                @NamedQuery(name="whichOperationWasTheMostFrequent",
                            query="SELECT o.type " +
                                    "FROM AccountOperation o "+
                                    "GROUP BY o.type " +
                                    "HAVING count(o.id) >= ALL(select count(a.id)" +
                                                    "FROM AccountOperation a " +
                                                    "GROUP BY a.type )"
                )
})
public class AccountOperation extends AbstractModel{

    public enum OperationType { DEPOSIT, WITHDRAW, TRANSFER}
    @ManyToOne
    private Account account;
    private BigDecimal amount;
    private OperationType type;

    public OperationType getType() {
        return type;
    }

    public AccountOperation(Account account, BigDecimal amount, OperationType type){
        this.account = account;
        this.amount = amount;
        this.type = type;
    }
    public AccountOperation() {}

}
