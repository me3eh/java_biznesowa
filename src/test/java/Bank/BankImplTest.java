package Bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BankImplTest {
    BankImpl bankImpl;
    private final String accountIdExceptionTooBig = "Cannot find - too big id";
    private final String accountIdExceptionTooSmall = "Cannot find - too small id";
    @BeforeEach
    void setUp() {
        bankImpl = new BankImpl();
    }

    @Nested
    @DisplayName("Group CreateAccount")
    class CreateAccount {
        private String name = "Maciek";
        private String address = "Address";
        Long firstBankAccountID;
        @BeforeEach
        void setUp(){
            firstBankAccountID = bankImpl.createAccount(name, address);
        }
        @Test
        @DisplayName("Creating account and checking that they're not the same ids")
        public void createAccount() {
            Long secondBankAccountID = bankImpl.createAccount(name, "tratata");
            assertNotEquals(firstBankAccountID, secondBankAccountID);
        }

        @Test
        @DisplayName("Creating account - same ids")
        public void createAccount2() {
            Long l = bankImpl.createAccount(name, address);
            Long l2 = bankImpl.createAccount(name, address);
            assertEquals(l2, l);
        }
    }
    @Nested
    @DisplayName("Group finding account")
    class FindAccount{
        private String name = "Maciek";
        private String address = "Ratata";
        @Test
        @DisplayName("Finding account not previously created return null")
        public void findAccount() {
            Long l = bankImpl.findAccount(name, address);
            assertNull(l);
        }
        @Test
        @DisplayName("Finding account previously created return id")
        public void findAccount2() {
            Long idWhileCreatingAccount = bankImpl.createAccount(name, address);
            Long idWhileFindingAccount = bankImpl.findAccount(name, address);
            assertEquals(idWhileCreatingAccount, idWhileFindingAccount);
        }
    }
    @Nested
    @DisplayName("Group Deposit")
    class Deposit{
        private Long idOfBankAccount;
        private BigDecimal amountOfMoney;
        @BeforeEach
        void setUp() {
            idOfBankAccount = bankImpl.createAccount("Maciek", "Cos");
            amountOfMoney = BigDecimal.valueOf(300);
        }

        @Test
        @DisplayName("Deposited money equals money in bank")
        void deposit() {
            bankImpl.deposit(idOfBankAccount, amountOfMoney);
            assertEquals(bankImpl.getBalance(idOfBankAccount), amountOfMoney);
        }
        @Test
        @DisplayName("Deposited money double times equals double the money in bank")
        void deposit2() {
            bankImpl.deposit(idOfBankAccount, amountOfMoney);
            bankImpl.deposit(idOfBankAccount, amountOfMoney);
            assertEquals(bankImpl.getBalance(idOfBankAccount), amountOfMoney.multiply( BigDecimal.valueOf( 2 ) ) );
        }
        @Test
        @DisplayName("Throwing error if id is bigger than array size")
        void deposit3() {
            Exception exception = assertThrows(Bank.AccountIdException.class, () -> {
                bankImpl.deposit(1000L, amountOfMoney);
            });
            assertEquals( accountIdExceptionTooBig, exception.getMessage() );
        }
        @Test
        @DisplayName("Throwing error if id is smaller than array size")
        void deposit4() {
            Exception secondException = assertThrows(Bank.AccountIdException.class, () -> {
                bankImpl.deposit(-2L, amountOfMoney);
            });
            assertEquals( accountIdExceptionTooSmall, secondException.getMessage() );
        }
    }

    @Nested
    @DisplayName("Group getBalance")
    class getBalance{
        private Long idOfBankAccount;
        @BeforeEach
        void setUp(){
            idOfBankAccount = bankImpl.createAccount("Maciek", "Cos");
        }
        @Test
        @DisplayName("Created account has 0 on it's account")
        void getBalance() {
            assertEquals(bankImpl.getBalance(idOfBankAccount), BigDecimal.valueOf(0));
        }
        @Test
        @DisplayName("Account after deposit 300 value some kind of unit has 300 on account")
        void getBalance2() {
            BigDecimal amountOfMoney = BigDecimal.valueOf( 300 );
            bankImpl.deposit(idOfBankAccount, amountOfMoney);
            assertEquals(bankImpl.getBalance(idOfBankAccount), amountOfMoney);
        }
        @Test
        @DisplayName("Throwing exception if too big id")
        void getBalance3() {
            Exception exception = assertThrows(Bank.AccountIdException.class, () -> {
                bankImpl.getBalance(1000L);
            });
            assertEquals( accountIdExceptionTooBig, exception.getMessage() );
        }
        @Test
        @DisplayName("Throwing exception if too small id")
        void getBalance4() {
            Exception secondException = assertThrows(Bank.AccountIdException.class, () -> {
                bankImpl.getBalance(-2L);
            });
            assertEquals( accountIdExceptionTooSmall, secondException.getMessage() );
        }
    }
    @Nested
    @DisplayName("Group withdraw")
    class Withdraw{
        private Long idOfBankAccount;
        private BigDecimal amountOfMoneyDeposited;
        private BigDecimal amountOfMoneyWithdrawed;

        @BeforeEach
        void setUp(){
            idOfBankAccount = bankImpl.createAccount("Maciek", "Cos");
            amountOfMoneyDeposited = BigDecimal.valueOf( 300 );
            amountOfMoneyWithdrawed = BigDecimal.valueOf( 200 );
        }
        @Test
        @DisplayName("After deposit and withdraw assert that money has been withdrawed")
        void withdraw() {
            bankImpl.deposit(idOfBankAccount, amountOfMoneyDeposited);
            assertEquals(bankImpl.getBalance(idOfBankAccount), amountOfMoneyDeposited);
            bankImpl.withdraw( idOfBankAccount, amountOfMoneyWithdrawed );
            assertEquals( bankImpl.getBalance(idOfBankAccount), amountOfMoneyDeposited.subtract( amountOfMoneyWithdrawed ) );
        }
        @Test
        @DisplayName("Throw exception if not sufficient funds ")
        void withdraw2() {
            Exception exceptionOfInsufficientFunds = assertThrows(Bank.InsufficientFundsException.class, () -> {
                bankImpl.withdraw( idOfBankAccount, amountOfMoneyWithdrawed);
            });
            assertEquals( exceptionOfInsufficientFunds.getMessage(), "Insufficient Funds");
        }
        @Test
        @DisplayName("Throw exception if id is too small")
        void withdraw3() {
            Exception secondException = assertThrows(Bank.AccountIdException.class, () -> {
                bankImpl.getBalance(-2L);
            });
            assertEquals( accountIdExceptionTooSmall, secondException.getMessage() );
        }
        @Test
        @DisplayName("Throw exception if id is too big")
        void withdraw4() {
            Exception secondException = assertThrows(Bank.AccountIdException.class, () -> {
                bankImpl.getBalance(1000L);
            });
            assertEquals( accountIdExceptionTooBig, secondException.getMessage() );
        }
    }

    @Nested
    @DisplayName("Group transfer")
    class Transfer{
        private Long idOfBankAccount;
        private Long secondIdOfBankAccount;
        private BigDecimal amountOfMoneyTransfered;

        @BeforeEach
        void setUp(){
            idOfBankAccount = bankImpl.createAccount("Maciek", "Cos");
            secondIdOfBankAccount = bankImpl.createAccount("Maciek2", "Cos");
            amountOfMoneyTransfered = BigDecimal.valueOf( 300 );
        }
        @Test
        @DisplayName("Transfer money from one account to another and assert amount of money on accounts are good")
        void transfer() {
            bankImpl.deposit(idOfBankAccount, amountOfMoneyTransfered);
            assertEquals(bankImpl.getBalance(idOfBankAccount), amountOfMoneyTransfered);

            bankImpl.transfer(idOfBankAccount, secondIdOfBankAccount, amountOfMoneyTransfered);
            assertEquals(bankImpl.getBalance(idOfBankAccount), BigDecimal.valueOf(0));
            assertEquals(bankImpl.getBalance(secondIdOfBankAccount), amountOfMoneyTransfered);
        }
        @Test
        @DisplayName("Throw error if id is too small")
        void transfer2() {
            Exception exception = assertThrows(Bank.AccountIdException.class, () -> {
                bankImpl.getBalance(-2L);
            });
            assertEquals( accountIdExceptionTooSmall, exception.getMessage() );
        }
        @Test
        @DisplayName("Throw error if money on account which is withdrawed are not sufficient in funds")
        void transfer3() {
            Exception secondException = assertThrows(Bank.InsufficientFundsException.class, () -> {
                bankImpl.transfer(idOfBankAccount, secondIdOfBankAccount, amountOfMoneyTransfered );
            });
            assertEquals( secondException.getMessage(), "Insufficient Funds");
        }
    }
}