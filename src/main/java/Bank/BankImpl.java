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
            bankAccount.add( BigDecimal.valueOf( 0 ) );
            Long idOfBankAccount = Long.valueOf(bankAccount.size() - 1);
            accounts.put( name + address, idOfBankAccount );
            return idOfBankAccount;
        }

        parentLogger.debug("Finished function: " + nameofCurrMethod );

        return accountFoundID;
    }

    @Override
    public Long findAccount(String name, String address) {
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        parentLogger.debug("Started function: " + nameofCurrMethod );

        Long foundAccount = accounts.get(name + address);

        parentLogger.debug("Finished function: " + nameofCurrMethod );

        return foundAccount;
    }

    @Override
    public void deposit(Long id, BigDecimal amount) throws AccountIdException {
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        parentLogger.debug("Started function: " + nameofCurrMethod );

        checkIfIdsGood(id);
        BigDecimal moneyInAccount = bankAccount.get( Math.toIntExact( id ) );
        bankAccount.set(Math.toIntExact(id), moneyInAccount.add( amount, MathContext.DECIMAL128) );

        parentLogger.debug("Finished function: " + nameofCurrMethod );
    }

    @Override
    public BigDecimal getBalance(Long id) throws AccountIdException{
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        parentLogger.debug("Started function: " + nameofCurrMethod );

        checkIfIdsGood(id);
        BigDecimal bankAccountFound = bankAccount.get(Math.toIntExact(id) );

        parentLogger.debug("Finished function: " + nameofCurrMethod );

        return bankAccountFound;
    }

    @Override
    public void withdraw(Long id, BigDecimal amount) throws AccountIdException, InsufficientFundsException{
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        parentLogger.debug("Started function: " + nameofCurrMethod );

        checkIfIdsGood(id);

        BigDecimal moneyInAccount = bankAccount.get( Math.toIntExact(id) );

        if( moneyInAccount.subtract( amount ).compareTo( BigDecimal.valueOf( 0 ) ) < 0 )
            throw new InsufficientFundsException("Insufficient Funds");
        bankAccount.set( Math.toIntExact(id), moneyInAccount.subtract( amount, MathContext.DECIMAL128 ) );

        parentLogger.debug("Finished function: " + nameofCurrMethod );
    }

    @Override
    public void transfer(Long idSource, Long idDestination, BigDecimal amount) throws AccountIdException,
                                                                                        InsufficientFundsException {
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        parentLogger.debug("Started function: " + nameofCurrMethod );

        checkIfIdsGood(idSource);
        checkIfIdsGood(idDestination);

        withdraw(idSource, amount);
        deposit(idDestination, amount);

        parentLogger.debug("Finished function: " + nameofCurrMethod );
    }
    private void checkIfIdsGood(Long id) throws AccountIdException{
        String exceptionMessage = "";
        if( bankAccount.size() < id )
            exceptionMessage = "Cannot find - too big id";
        else if( id < 0 )
             exceptionMessage = "Cannot find - too small id";
        if( !exceptionMessage.isEmpty() ) {
            parentLogger.error("Thrown error: " + AccountIdException.class + " with communicate: " + exceptionMessage);
            throw new AccountIdException(exceptionMessage);
        }
    }
}
