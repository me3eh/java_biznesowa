package Bank;

import com.github.javafaker.Faker;
import dao.*;
import model.Account;
import model.AccountOperation;
import model.TransferOperation;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

        AccountOperationDao operationDao = new AccountOperationDaoJpaImpl();
        for (AccountOperation u : operationDao.findAll())
            operationDao.delete(u);
        TransferOperationDao transferDao = new TransferOperationDaoJpaImpl();
        for (TransferOperation u : transferDao.findAll())
            transferDao.delete(u);
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
    @Nested
    @DisplayName("Named Queries")
    class NamedQueries{
        Faker faker;
        String firstName;
        String firstAddress;
        String secondName;
        String secondAddress;
        @BeforeEach
        void setUp(){
            faker = new Faker();
            firstName = faker.name().firstName();
            firstAddress = faker.address().streetAddress();
            secondName = faker.name().firstName();
            secondAddress = faker.address().streetAddress();
        }
        @Test
        @DisplayName("Named Query checks as good")
        void accountByNameAndAddress(){

            bankImpl.createAccount(firstName, firstAddress);
            Account som = new AccountDaoJpaImpl().accountByNameAndAddress(firstName, firstAddress);

            assertEquals(som.getName(), firstName);
            assertEquals(som.getAddress(), firstAddress);
        }
        @Test
        @DisplayName("accountByPrefix")
        void accountByPrefix(){
            bankImpl.createAccount("Mateusz", "Sio");
            bankImpl.createAccount("Maciek", "Sio");
            bankImpl.createAccount("Piotrek", "Sio");
            bankImpl.createAccount("Matylda", "Sio");
            List<Account> accountList = new AccountDaoJpaImpl().accountByPrefix("Ma");
            assertEquals(accountList.size(), 3);
            assertEquals(accountList.get(0).getName(), "Mateusz");
            assertEquals(accountList.get(1).getName(), "Maciek");
            assertEquals(accountList.get(2).getName(), "Matylda");
        }
        @Test
        @DisplayName("accountsWithBalanceInRange")
        void accountByRangedBalance(){
            long idFirst = bankImpl.createAccount(firstName, firstAddress);
            long idSecond = bankImpl.createAccount(secondName, secondAddress);
            long idThird = bankImpl.createAccount(faker.name().firstName(), faker.address().streetAddress());
            long idFourth = bankImpl.createAccount(faker.name().firstName(), faker.address().streetAddress());
            bankImpl.deposit(idFirst, new BigDecimal("200"));
            bankImpl.deposit(idSecond, new BigDecimal("300"));
            bankImpl.deposit(idThird, new BigDecimal("400"));
            bankImpl.deposit(idFourth, new BigDecimal("100"));

            List<Account> accountList =
                    new AccountDaoJpaImpl().accountByRangedBalance(new BigDecimal("200"), new BigDecimal("350") );
            assertEquals(2, accountList.size());
            assertEquals(firstName, accountList.get(0).getName());
            assertEquals(firstAddress, accountList.get(0).getAddress());
            assertEquals(secondName, accountList.get(1).getName());
            assertEquals(secondAddress, accountList.get(1).getAddress());
        }

        @Test
        @DisplayName("accountsWithMaxBalance")
        void accountsWithMaxBalance(){
            long idFirst = bankImpl.createAccount("Mateusz", "Sio");
            long idSecond = bankImpl.createAccount("Maciek", "Sio");
            long idThird = bankImpl.createAccount(firstName, firstAddress);
            long idFourth = bankImpl.createAccount(secondName, secondAddress);
            bankImpl.deposit(idFirst, new BigDecimal("200"));
            bankImpl.deposit(idSecond, new BigDecimal("300"));
            bankImpl.deposit(idThird, new BigDecimal("400"));
            bankImpl.deposit(idFourth, new BigDecimal("100"));
            AccountDao ac = new AccountDaoJpaImpl();
            List<Account> listOfAccounts = ac.accountWithMaxBalance();
            assertEquals(listOfAccounts.size(), 1);
            assertEquals(listOfAccounts.get(0).getName(), firstName);

            bankImpl.deposit(idFourth, new BigDecimal("300"));
            listOfAccounts = ac.accountWithMaxBalance();
            assertEquals(listOfAccounts.size(), 2);
            assertEquals(listOfAccounts.get(0).getName(), firstName);
            assertEquals(listOfAccounts.get(1).getName(), secondName);
        }
        @Test
        @DisplayName("operationsOnSpecificAccountInRangeOfDates")
        void operationsOnSpecificAccountInRangeOfDates() {
            LocalDateTime startingTime = LocalDateTime.now();

            long firstId = bankImpl.createAccount(firstName, firstAddress);
            long secondId = bankImpl.createAccount(secondName, secondAddress);
            BigDecimal amountOfMoney = new BigDecimal("300");
            bankImpl.deposit(firstId, amountOfMoney);
//          sleep for 5 seconds and do a transfer
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            bankImpl.transfer(firstId, secondId, amountOfMoney);

            AccountOperationDao dao = new AccountOperationDaoJpaImpl();
            LocalDateTime startDate = LocalDateTime.of( startingTime.toLocalDate(), startingTime.toLocalTime() );
            LocalDateTime endDate = LocalDateTime.of(startingTime.toLocalDate(),
                                                        startingTime.toLocalTime().plusSeconds(3));
            LocalDateTime secondEndDate = LocalDateTime.of(startingTime.toLocalDate(),
                    startingTime.toLocalTime().plusSeconds(12));


            List<AccountOperation> listOfAccountOperations =
                dao.operationsOnSpecificAccountInRangeOfDates(firstId, startDate, endDate);

            assertEquals( 1, listOfAccountOperations.size());
            assertEquals( AccountOperation.OperationType.DEPOSIT, listOfAccountOperations.get(0).getType());


            listOfAccountOperations =
                    dao.operationsOnSpecificAccountInRangeOfDates(firstId, startDate, secondEndDate);

            assertEquals( 3, listOfAccountOperations.size());
            assertEquals( AccountOperation.OperationType.DEPOSIT, listOfAccountOperations.get(0).getType());
            assertEquals( AccountOperation.OperationType.WITHDRAW, listOfAccountOperations.get(1).getType());
            assertEquals( AccountOperation.OperationType.TRANSFER, listOfAccountOperations.get(2).getType());
        }
        @Test
        @DisplayName("AccountsWithNoAccountOperation")
        void accountsWithNoAccountOperation() {
            AccountDao dao = new AccountDaoJpaImpl();
            List<Account> listOfAccounts = dao.accountsWithNoAccountOperation();
            assertNull(listOfAccounts);

            long idFirst = bankImpl.createAccount(faker.name().firstName(), faker.address().streetAddress());
            long idSecond = bankImpl.createAccount(faker.name().firstName(), faker.address().streetAddress());
            long idThird = bankImpl.createAccount(faker.name().firstName(), faker.address().streetAddress());
            long idFourth = bankImpl.createAccount(firstName, firstAddress);
            BigDecimal money = new BigDecimal("300");

            bankImpl.deposit(idFirst, money );
            bankImpl.deposit(idSecond, money );
            bankImpl.transfer(idSecond, idThird, money );

            dao = new AccountDaoJpaImpl();
            listOfAccounts = dao.accountsWithNoAccountOperation();
            assertEquals( 1, listOfAccounts.size());
            assertEquals( idFourth, listOfAccounts.get(0).getId());
            assertEquals( firstName, listOfAccounts.get(0).getName());
            assertEquals( firstAddress, listOfAccounts.get(0).getAddress());
        }
        @Test
        @DisplayName("AccountsWithMaxAccountOfOperations")
        void accountsWithMaxAccountOfOperations() {
            AccountDao dao = new AccountDaoJpaImpl();
            List<Account> listOfAccounts = dao.accountsWithBiggestAmountOfOperations();

            assertNull(listOfAccounts);

            long idFirst = bankImpl.createAccount(firstName, firstAddress);
            long idSecond = bankImpl.createAccount(faker.name().firstName(), faker.address().streetAddress());
            long idThird = bankImpl.createAccount(secondName, secondAddress);

            bankImpl.deposit(idFirst, new BigDecimal("300"));
            bankImpl.deposit(idFirst, new BigDecimal("300"));

            bankImpl.deposit(idSecond, new BigDecimal("300"));

            bankImpl.deposit(idThird, new BigDecimal("900"));
            bankImpl.transfer(idThird, idFirst, new BigDecimal("300"));

            dao = new AccountDaoJpaImpl();
            listOfAccounts = dao.accountsWithBiggestAmountOfOperations();

            assertEquals(2, listOfAccounts.size());
            assertEquals(firstName, listOfAccounts.get(0).getName());
            assertEquals(secondName, listOfAccounts.get(1).getName());
            assertEquals(firstAddress, listOfAccounts.get(0).getAddress());
            assertEquals(secondAddress, listOfAccounts.get(1).getAddress());
        }
        @Test
        @DisplayName("mostFrequentOperations")
        void mostFrequentOperations() {
            AccountOperationDao dao = new AccountOperationDaoJpaImpl();
            AccountOperation.OperationType operationType = dao.whichOperationWasTheMostFrequent();

            assertNull(operationType);

            long idFirst = bankImpl.createAccount(firstName, firstAddress);
            long idSecond = bankImpl.createAccount(secondName, secondAddress);

            bankImpl.deposit(idFirst, new BigDecimal("300"));
            bankImpl.deposit(idFirst, new BigDecimal("300"));
            bankImpl.deposit(idSecond, new BigDecimal("300"));

            bankImpl.transfer(idFirst, idSecond, new BigDecimal("300"));
            bankImpl.withdraw(idFirst, new BigDecimal("200"));
            dao = new AccountOperationDaoJpaImpl();
            operationType = dao.whichOperationWasTheMostFrequent();

            assertEquals(operationType, AccountOperation.OperationType.DEPOSIT);
        }
    }
}