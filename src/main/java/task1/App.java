package task1;

import dao.AccountOperationDao;
import dao.AccountOperationDaoJpaImpl;
import model.AccountOperation;

public class App {

    public static void main(String[] args) {

        System.out.println("start");
        AccountOperationDao dao = new AccountOperationDaoJpaImpl();
        for( AccountOperation a : dao.findAll()){
            System.out.println(a);
        }
    }

    public static double max(double a, double b) {
        return a>b ? a : b;
    }
}
