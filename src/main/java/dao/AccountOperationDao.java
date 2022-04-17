package dao;

import model.AccountOperation;

import java.time.LocalDateTime;
import java.util.List;

public interface AccountOperationDao extends GenericDao<AccountOperation,Long> {
    List<AccountOperation> operationsOnSpecificAccountInRangeOfDates(Long id, LocalDateTime start, LocalDateTime end);
    AccountOperation.OperationType whichOperationWasTheMostFrequent();
}
