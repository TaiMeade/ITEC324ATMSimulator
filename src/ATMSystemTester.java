import java.util.ArrayList;
import java.util.Scanner;

public class ATMSystemTester {
    public static void main(String[] args) {

        /* These were for initial testing.

        User test = new User("tai", 1, "123", new Account(100, "checking"), new Account(150, "savings"));
        User test2 = new User("tester", 2, "1234", new Account(200, "checking"), new Account(500, "savings"));
        ArrayList<User> userList = new ArrayList<User>();
        userList.add(test);
        userList.add(test2);
        BankServer server = new BankServer(userList);
        ATM atm = new ATM(100, 100, 100, 100);
        // System.out.println(atm.totalInATM());
        System.out.println(atm.getBillCount());
        Operator operator = new Operator(10, "321");
        operator.removeBills(atm, 5, 5, 10, 10);
        System.out.println(atm.getBillCount());
        operator.refillBills(atm, 15, 15, 15, 15);
        System.out.println(atm.getBillCount());


        System.out.println("Amount to start:\ntai: " + test.getCheckAccountBalance() + "\t" + test.getSavingAccountBalance());
        System.out.println("test2: " + test2.getCheckAccountBalance() + "\t" + test2.getSavingAccountBalance() + "\nAfter transfer:");
        test.localTransferFunds("checking", 20);
        server.serverTransferFunds(2, 1, 100);
        System.out.println("tai: " + test.getCheckAccountBalance() + "\t" + test.getSavingAccountBalance());
        System.out.println("test2: " + test2.getCheckAccountBalance() + "\t" + test2.getSavingAccountBalance());

         */

        // --------------------------Create necessary components---------------------------------
        User user1 = new User("Tai Meade", 1, "1", new Account(100, "Checking"), new Account(150, "Savings"));
        User user2 = new User("Lonnie Meade", 2, "2", new Account(200, "Checking"), new Account(300, "Savings"));
        User user3 = new User("Leila Meade", 3, "3", new Account(300, "Checking"), new Account(450, "Savings"));
        User user4 = new User("Kiona Meade", 4, "4", new Account(400, "Checking"), new Account(600, "Savings"));
        User user5 = new User("Alayna Meade", 5, "5", new Account(500, "Checking"), new Account(750, "Savings"));
        User user6 = new User("Audra Meade", 6, "6", new Account(600, "Checking"), new Account(900, "Savings"));
        User user7 = new User("Deaira Jones", 7, "7", new Account(700, "Checking"), new Account(1050, "Savings"));
        User user8 = new User("Destiny Jones", 8, "8", new Account(800, "Checking"), new Account(1200, "Savings"));
        User user9 = new User("Lorie Jones", 9, "9", new Account(900, "Checking"), new Account(1350, "Savings"));
        User user10 = new User("Kevin Jones", 10, "10", new Account(1000, "Checking"), new Account(17500, "Savings"));

        // Adds users to a list...to be used within the BankServer
        ArrayList<User> userList = new ArrayList<User>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        userList.add(user5);
        userList.add(user6);
        userList.add(user7);
        userList.add(user8);
        userList.add(user9);
        userList.add(user10);

        // Creates the rest of the necessary components...Operator, BankServer, ATM, Screen, and Connection
        outputUserInstructions();
        Operator operator = new Operator(1001, "op123");
        BankServer server = new BankServer(userList, operator);
        ATM atm = new ATM(100, 100, 100, 100);
        Scanner console = new Scanner(System.in);
        Screen s = new Screen(console);
        Connection c = new Connection(server, atm, s);
        s.run(c);
    }

    /**
     * States the instructions to the terminal.
     */
    private static void outputUserInstructions() {
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tWelcome to Tai Meade's ATM simulator!");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("You will interact with the system through the use of the 'screen', which will be simulated using a text-based interface that uses the account number as the passcode." +
                            "\nThe screen supports many different functions, but once logged in a user will first select which account they'd like to perform a transaction with. " +
                            "\nTransactions allowed are:" +
                            "\n\tChecking - Withdrawals, Deposits, Transferring to savings, Transferring to another user's checking account" +
                            "\n\tSavings - Withdrawals, Deposits, Transferring to checking" +
                            "\nPlease note: This machine only accepts 20s when doing deposits." +
                            "\n\nThere are also options to return to the home screen once logged in by entering 'H', logging out by entering 'L', and exiting the simulator by entering 'Q'" +
                            "\n\nThere is also an operator mode that be accessed via its unique account number and passcode (1001 and op123 respectively)" +
                            "\nFrom this mode the operator can: " +
                            "\n\tDisplay the number of each type of bill" +
                            "\n\tRefill bills" +
                            "\n\tRemove bills");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }
}