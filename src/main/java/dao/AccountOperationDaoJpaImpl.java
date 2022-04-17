package dao;

import model.Account;
import model.AccountOperation;
import model.TransferOperation;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AccountOperationDaoJpaImpl extends GenericDaoJpaImpl<AccountOperation,Long> implements AccountOperationDao{
    public List<AccountOperation> operationsOnSpecificAccountInRangeOfDates(Long id, LocalDateTime start, LocalDateTime end){
        TypedQuery<AccountOperation> query =
                getEntityManager().createNamedQuery("operationsOnSpecificAccountInRangeOfDates",
                                                    AccountOperation.class);
        query.setParameter(1, id);
        query.setParameter(2, start);
        query.setParameter(3, end);

        List<AccountOperation> results = query.getResultList();
        if( results.isEmpty() )
            return null;
        return results;
    }
    public AccountOperation.OperationType whichOperationWasTheMostFrequent(){
        EntityManager em = getEntityManager();
        TypedQuery<AccountOperation.OperationType> query = em.createNamedQuery("whichOperationWasTheMostFrequent",
                                                                    AccountOperation.OperationType.class);

        List<AccountOperation.OperationType> results = query.getResultList();
        if( results.isEmpty() )
            return null;
        return results.get(0);
    }
}
