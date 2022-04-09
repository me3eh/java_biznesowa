package Bank;

import dao.*;
import model.Account;
import model.AccountOperation;
import model.TransferOperation;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BankImplTest {
    BankImpl bankImpl;
    private final String accountIdExceptionTooBig = "Cannot find account with that id";
    private final String accountIdExceptionTooSmall = "Cannot find account with that id";
    @BeforeEach
    void setUp() {
        bankImpl = new BankImpl();
    }
    @AfterEach
    void setUp2(){
        AccountDao userDao = new AccountDaoJpaImpl();
        for (Account u : userDao.findAll())
            userDao.delete(u);
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
            idOfBankAccount = bankImpl.createAccount("Maciek", "Cos2");
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
                bankImpl.getBalance(-1L);
            });
            assertEquals( accountIdExceptionTooBig, exception.getMessage() );
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
            idOfBankAccount = bankImpl.createAccount("Maciek", "Cos44444");
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
                bankImpl.withdraw(-2L, BigDecimal.valueOf(300L));
            });
            assertEquals( accountIdExceptionTooSmall, secondException.getMessage() );
        }
        @Test
        @DisplayName("Throw exception if id is too big")
        void withdraw4() {
            Exception secondException = assertThrows(Bank.AccountIdException.class, () -> {
                bankImpl.getBalance(-5L);
            });
            assertEquals( accountIdExceptionTooBig, secondException.getMessage() );
        }
    }

    @Nested
    @DisplayName("Group transfer")
    class Transfer{
        private Long idSource;
        private Long idDestination;
        private BigDecimal amountOfMoneyTransfered;

        @BeforeEach
        void setUp(){
            idSource = bankImpl.createAccount("Maciek", "Cos5");
            idDestination = bankImpl.createAccount("Maciek2", "Cos13");
            amountOfMoneyTransfered = BigDecimal.valueOf( 300 );
        }
        @Test
        @DisplayName("Transfer money from one account to another and assert amount of money on accounts are good")
        void transfer() {
            bankImpl.deposit(idSource, amountOfMoneyTransfered);
            BigDecimal s = bankImpl.getBalance(idSource);
            assertEquals(amountOfMoneyTransfered, s);

            bankImpl.transfer(idSource, idDestination, amountOfMoneyTransfered);
            assertEquals(BigDecimal.valueOf(0), bankImpl.getBalance(idSource));
            assertEquals(bankImpl.getBalance(idDestination), amountOfMoneyTransfered);
        }
        @Test
        @DisplayName("Cannot find account with that id")
        void transfer2() {
            Exception exception = assertThrows(Bank.AccountIdException.class, () -> {
                bankImpl.getBalance(-1L);
            });
            assertEquals( accountIdExceptionTooSmall, exception.getMessage() );
        }

        @Test
        @DisplayName("Throw error if money on account which is withdrawed are not sufficient in funds")
        void transfer3() {
            Exception secondException = assertThrows(Bank.InsufficientFundsException.class, () -> {
                bankImpl.transfer(idSource, idDestination, amountOfMoneyTransfered );
            });
            assertEquals( secondException.getMessage(), "Insufficient Funds");
        }
        @Test
        @DisplayName("Throw error if same ids")
        void transfer4() {
            Exception secondException = assertThrows(Bank.AccountIdException.class, () -> {
                bankImpl.transfer(idSource, idSource, amountOfMoneyTransfered );
            });
            assertEquals( secondException.getMessage(), "Same ids");
        }
    }
    @Nested
    @DisplayName("Group transfer")
    class AccountOperationTransfer {
        private Long idSource;
        private Long idDestination;
        private BigDecimal amountOfMoneyTransfered;

        @BeforeEach
        void setUp(){
            idSource = bankImpl.createAccount("Mateusz", "Sio");
            idDestination = bankImpl.createAccount("mmm", "rrr");
            amountOfMoneyTransfered = BigDecimal.valueOf( 300 );
        }
        @Test
        @DisplayName("When we transfer, we want to see that account and transfer operations are in good amount")
        void transfer() {
            bankImpl.deposit(idSource, amountOfMoneyTransfered);
            BigDecimal s = bankImpl.getBalance(idSource);
            assertEquals(amountOfMoneyTransfered, s);

            bankImpl.transfer(idSource, idDestination, amountOfMoneyTransfered);
            assertEquals(BigDecimal.valueOf(0), bankImpl.getBalance(idSource));
            assertEquals(bankImpl.getBalance(idDestination), amountOfMoneyTransfered);

            AccountOperationDao dao = new AccountOperationDaoJpaImpl();
            TransferOperationDao dao2 = new TransferOperationDaoJpaImpl();
            List<AccountOperation> listOfAccountOperations = dao.findAll();
            List<TransferOperation> listOfTransferOperations = dao2.findAll();
            assertEquals( 1, listOfTransferOperations.size());
            assertEquals( 4, listOfAccountOperations.size());
        }
    }
}