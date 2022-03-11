package Bank;




import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BankImpl implements Bank{
    HashMap<String, Long> accounts = new HashMap();
    ArrayList<BigDecimal> bankAccount = new ArrayList<>();
    protected static final Logger parentLogger = LogManager.getLogger();

    public BankImpl(){

        parentLogger.info("dwoja");
//
        parentLogger.info("dwoja");
//        parentLogger.info("dwoja");
//        parentLogger.info("dwoja");
//        parentLogger.info("dwoja");
//        parentLogger.info("dwoja");
//        parentLogger.info("dwoja");
//        parentLogger.info("dwoja");
//        parentLogger.info("dwoja");
    }

    @Override
    public Long createAccount(String name, String address) {
        Long accountFoundID = findAccount(name, address);
        if( accountFoundID == null) {
            bankAccount.add( BigDecimal.valueOf( 0 ) );
            Long idOfBankAccount = Long.valueOf(bankAccount.size() - 1);
            accounts.put( name + address, idOfBankAccount );
            return idOfBankAccount;
        }
        return accountFoundID;
    }

    @Override
    public Long findAccount(String name, String address) {
        return accounts.get(name + address);
    }

    @Override
    public void deposit(Long id, BigDecimal amount) throws AccountIdException {
        checkIfIdsGood(id);
        BigDecimal moneyInAccount = bankAccount.get( Math.toIntExact( id ) );
        bankAccount.set(Math.toIntExact(id), moneyInAccount.add( amount, MathContext.DECIMAL128) );
    }

    @Override
    public BigDecimal getBalance(Long id) throws AccountIdException{
        checkIfIdsGood(id);

        return bankAccount.get(Math.toIntExact(id) );
    }

    @Override
    public void withdraw(Long id, BigDecimal amount) throws AccountIdException, InsufficientFundsException{
        checkIfIdsGood(id);

        BigDecimal moneyInAccount = bankAccount.get( Math.toIntExact(id) );

        if( moneyInAccount.subtract( amount ).compareTo( BigDecimal.valueOf( 0 ) ) < 0 )
            throw new InsufficientFundsException("Insufficient Funds");
        bankAccount.set( Math.toIntExact(id), moneyInAccount.subtract( amount, MathContext.DECIMAL128 ) );
    }

    @Override
    public void transfer(Long idSource, Long idDestination, BigDecimal amount) throws AccountIdException, InsufficientFundsException {
        checkIfIdsGood(idSource);
        checkIfIdsGood(idDestination);

        withdraw(idSource, amount);
        deposit(idDestination, amount);
    }
    private void checkIfIdsGood(Long id) throws AccountIdException{
        if( bankAccount.size() < id )
            throw new AccountIdException("Cannot find - too big id");
        if( id < 0 )
            throw new AccountIdException("Cannot find - too small id");
    }
}
