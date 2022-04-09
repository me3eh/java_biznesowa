package Bank;

import dao.AccountDao;
import dao.AccountDaoJpaImpl;
import model.Account;
import model.AccountOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class BankImpl implements Bank{
    HashMap<String, Long> accounts = new HashMap<>();
    ArrayList<BigDecimal> bankAccount = new ArrayList<>();
    protected static final Logger parentLogger = LogManager.getLogger(BankImpl.class);

    public BankImpl(){
        parentLogger.info("Object " + this.getClass().toString() + " created");
    }

    @Override
    public Long createAccount(String name, String address) {
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        parentLogger.debug("Started function: " + nameofCurrMethod );

        Long accountFoundID = findAccount(name, address);
        if( accountFoundID == null) {
            AccountDao dao = new AccountDaoJpaImpl();
            Account account = new Account(name, address);
            dao.save(account);
            return account.getId();
        }

        parentLogger.debug("Finished function: " + nameofCurrMethod );

        return accountFoundID;
    }

    @Override
    public Long findAccount(String name, String address) {
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        parentLogger.debug("Started function: " + nameofCurrMethod );

        AccountDao dao = new AccountDaoJpaImpl();
        Account foundAccount = dao.getByClass(name, address);

//        Long foundAccount = accounts.get(name + address);

        parentLogger.debug("Finished function: " + nameofCurrMethod );
        Long foundID = null;
        if( foundAccount != null)
            foundID = foundAccount.getId();
        return foundID;
    }
    public Account findAccountByID(Long id){
        AccountDaoJpaImpl accountDao = new AccountDaoJpaImpl();
        return accountDao.findById(id).orElse(null);
    }

    @Override
    public void deposit(Long id, BigDecimal amount) throws AccountIdException {
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        parentLogger.debug("Started function: " + nameofCurrMethod );

        Account account = findAccountByID(id);
        if(account == null) {
            String exceptionMessage = "Cannot find account with that id";
            parentLogger.error("Thrown error: " + AccountIdException.class + " with communicate: " + exceptionMessage);
            throw new AccountIdException(exceptionMessage);
        }
        account.addAmount( amount );

        account.addAccountOperation(amount, AccountOperation.OperationType.DEPOSIT);

        AccountDaoJpaImpl accountDao = new AccountDaoJpaImpl();
        accountDao.update( account );
        parentLogger.debug("Finished function: " + nameofCurrMethod );
    }

    @Override
    public BigDecimal getBalance(Long id) throws AccountIdException{
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        parentLogger.debug("Started function: " + nameofCurrMethod );

        Account account = findAccountByID(id);
        if(account == null) {
            String exceptionMessage = "Cannot find account with that id";
            parentLogger.error("Thrown error: " + AccountIdException.class + " with communicate: " + exceptionMessage);
            throw new AccountIdException(exceptionMessage);
        }

        parentLogger.debug("Finished function: " + nameofCurrMethod );

        return account.getBalance();
    }

    @Override
    public void withdraw(Long id, BigDecimal amount) throws AccountIdException, InsufficientFundsException{
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        parentLogger.debug("Started function: " + nameofCurrMethod );

        Account account = findAccountByID(id);
        if(account == null) {
            String exceptionMessage = "Cannot find account with that id";
            parentLogger.error("Thrown error: " + AccountIdException.class + " with communicate: " + exceptionMessage);
            throw new AccountIdException(exceptionMessage);
        }

        if( !account.subtractAmount( amount ) )
            throw new InsufficientFundsException("Insufficient Funds");
        System.out.println("Odjete" + amount + " zostało - " + account.getBalance());
        account.addAccountOperation(amount, AccountOperation.OperationType.WITHDRAW);

        AccountDaoJpaImpl accountDao = new AccountDaoJpaImpl();
        accountDao.update( account );

        parentLogger.debug("Finished function: " + nameofCurrMethod );
    }

    @Override
    public void transfer(Long idSource, Long idDestination, BigDecimal amount) throws AccountIdException,
                                                                                        InsufficientFundsException {
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        parentLogger.debug("Started function: " + nameofCurrMethod );

        if(idDestination.equals(idSource)){
            String exceptionMessage = "Same ids";
            parentLogger.error("Thrown error: " + AccountIdException.class + " with communicate: " + exceptionMessage);
            throw new AccountIdException(exceptionMessage);
        }


        withdraw(idSource, amount);
        deposit(idDestination, amount);

        Account account = findAccountByID(idSource);
        Account accountDestination = findAccountByID(idDestination);

        account.addTransferOperation(amount, AccountOperation.OperationType.TRANSFER, accountDestination, "Some name");

        AccountDaoJpaImpl accountDao = new AccountDaoJpaImpl();
        accountDao.update(account);
        accountDao.update(accountDestination);

        parentLogger.debug("Finished function: " + nameofCurrMethod );
    }
}
