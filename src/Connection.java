import javax.swing.plaf.nimbus.State;
import java.util.HashMap;

public class Connection {

    private User currentUser;
    private User targetUser;
    private String accumulatedKeys;
    private BankServer currentServer;
    private Account currentAccount;
    private Screen screen;
    private ATM currentAtm;
    private Operator operator;
    private double amount;
    private int numFives;
    private int numTwenties;
    private int numFifties;
    private int numHundreds;
    private boolean refill;
    private static enum State {
        CONNECTED,
        USER_FOUND,
        LOGGED_IN,
        MAIN_MENU,
        WITHDRAW,
        DEPOSIT,
        LOCAL_TRANSFER,
        SERVER_TRANSFER,
        CHECKING_SELECTED,
        SAVING_SELECTED,
        GET_TARGET_USER,
        OP_MAIN_MENU,
        OP_FOUND,
        GET_FIVES,
        GET_TWENTIES,
        GET_FIFTIES,
        GET_HUNDREDS_REFILL,
        GET_HUNDREDS_REMOVE

    }
    private State state;

    private String INITIAL_PROMPT = "Please enter your account number: ";
//    private String MAIN_MENU_TEXT = "\nSelect an account (1 or 2):\n"
//                                    + "1. Checking\n"
//                                    + "2. Savings";
    private String OPTIONS_MENU_TEXT = "Please select an option (1, 2, 3, or 4):\n"
                                       + "1. Withdraw\n"
                                       + "2. Deposit\n"
                                       + "3. Local Transfer\n"
                                       + "4. Server Transfer";
    private String OPTIONS_MENU_TEXT_NO_SERVER = "Please select an option (1, 2, 3, or 4):\n"
                                                 + "1. Withdraw\n"
                                                 + "2. Deposit\n"
                                                 + "3. Local Transfer";

    /**
     * Constructor for the Connection object
     *
     * @param server the BankServer that the ATM is connected to
     * @param currentAtm the ATM being operated/used
     * @param screen gets user/operator input and reacts properly based on state/input
     */
    public Connection(BankServer server, ATM currentAtm, Screen screen)  {
        this.currentServer = server;
        this.screen = screen;
        this.currentAtm = currentAtm;
        resetConnection();
    }

    /**
     * Respond to the user's input.
     *
     * @param key the user's input
     */
    public void dial(String key) {

        switch(state) {
            // Connects based on the account number entered...asks for password if account was found
            case CONNECTED:
                connect(key);
                break;

            // Connects to a user account if the password was correct
            case USER_FOUND:
                loginToAccount(key);
                break;

            // Connects to an operator account if password was correct...NOTE: this design means that operator numbers and account numbers CANNOT be the same
            case OP_FOUND:
                loginToOperatorMode(key);
                break;

            // Handles what the operator chose to do
            case OP_MAIN_MENU:
                operatorMenu(key);
                break;

            // Handles which account the user wants to conduct a transaction with
            case MAIN_MENU:
                mainMenu(key);
                break;

            // Handles options allowed for checking accounts
            case CHECKING_SELECTED:
                optionsMenuChecking(key);
                break;

            // Handles options allowed for savings accounts
            case SAVING_SELECTED:
                optionsMenuSaving(key);
                break;

            // Handles transactions when in the proper states
            case WITHDRAW, DEPOSIT, LOCAL_TRANSFER, SERVER_TRANSFER:
                manageTransactions(key);
                break;

            // Finds the target user (this is only used when transferring from one account to another)
            case GET_TARGET_USER:
                findTargetAccount(key);
                break;

            // Handles getting the number of five dollar bills the operator wants to refill or remove
            case GET_FIVES:
                getFives(key);
                break;

            // Handles getting the number of twenty dollar bills the operator wants to refill or remove
            case GET_TWENTIES:
                getTwenties(key);
                break;

            // Handles getting the number of fifty dollar bills the operator wants to refill or remove
            case GET_FIFTIES:
                getFifties(key);
                break;

            // Handles getting the number of hundred dollar bills the operator wants to refill
            case GET_HUNDREDS_REFILL:
                getHundreds(key);
                currentAtm.increaseBillCount(numFives, numTwenties, numFifties, numHundreds);
                screen.speak("\nOptions:\n"
                        + "\n1. Display Number of Bills"
                        + "\n2. Refill Bills"
                        + "\n3. Remove Bills");
                break;

            // Handles getting the number of hundred dollar bills the operator wants to remove
            // This is more complex that refilling because I must prevent the operator from removing too many bills...
            // removing too many bills would result in negative amounts within the ATM.
            case GET_HUNDREDS_REMOVE:
                getHundreds(key);
                HashMap<Integer, Integer> prevNumBills = new HashMap<Integer,Integer>();
                prevNumBills.put(5, currentAtm.getNumFives());
                prevNumBills.put(20, currentAtm.getNumTwenties());
                prevNumBills.put(50, currentAtm.getNumFifties());
                prevNumBills.put(100, currentAtm.getNumHundreds());
                currentAtm.increaseBillCount(-numFives, -numTwenties, -numFifties, -numHundreds);

                // Tells the operator if the atm was unable to produce the requested amount of bills.
                if (currentAtm.getBillCount().get(5) == prevNumBills.get(5) && currentAtm.getBillCount().get(20) == prevNumBills.get(20) && currentAtm.getBillCount().get(50) == prevNumBills.get(50) && currentAtm.getBillCount().get(100) == prevNumBills.get(100)) {
                    screen.speak("Not enough bills in ATM.");
                }
                else {
                    screen.speak("Success!");
                }
                screen.speak("\nOptions:\n"
                        + "\n1. Display Number of Bills"
                        + "\n2. Refill Bills"
                        + "\n3. Remove Bills");
                break;

        }
    }

    /**
     * Reset the connection to the initial state
     * and prompt for the account number...resembles logging out of an ATM.
     */
    private void resetConnection() {
        amount = 0;
        accumulatedKeys = "";
        state = State.CONNECTED;
        currentUser = null;     // prevents people from using the 'H' or home function to access the last logged in user
        currentAccount = null;
        operator = null;
        screen.speak(INITIAL_PROMPT);
    }

    /**
     * Try to connect the user with
     * an account associated with the
     * account number they input
     *
     * @param key the key(s) typed in by the user
     */
    private void connect(String key) {
        accumulatedKeys += key;
        currentUser = currentServer.findUser(Integer.parseInt(accumulatedKeys));
        operator = currentServer.findOperator(Integer.parseInt(accumulatedKeys));
        if (currentUser != null) {
            state = State.USER_FOUND;
            screen.speak("Please enter your password: ");
        }
        else if (operator != null) {
            state = State.OP_FOUND;
            screen.speak("Please enter your operator password: ");
        }
        else {
            screen.speak("Invalid account number. Try again!");
        }
        accumulatedKeys = "";

    }

    /**
     * Logs the operator into their account if they enter the proper password
     *
     * @param key the key(s) typed in by the user
     */
    private void loginToOperatorMode(String key) {
        accumulatedKeys += key;
        if (operator.checkPasscode(key)) {
            state = State.OP_MAIN_MENU;
            screen.speak("Welcome operator " + operator.getOpNum() + "!");
            screen.speak("\nOptions:\n"
                    + "\n1. Display Number of Bills"
                    + "\n2. Refill Bills"
                    + "\n3. Remove Bills");
        }
        else {
            screen.speak("Incorrect passcode. Try again!");
        }
        accumulatedKeys = "";
    }

    /**
     * Carries out the proper command based on the operator's input from the options menu
     *
     * @param key the key(s) typed in by the user
     */
    private void operatorMenu(String key) {
        if (key.equals("1")) {
            for (HashMap.Entry<Integer, Integer> numBills : currentAtm.getBillCount().entrySet()) {
                screen.speak(numBills.getKey() + "s - " + numBills.getValue());
            }
            state = State.OP_MAIN_MENU;
            screen.speak("\nOptions:\n"
                    + "\n1. Display Number of Bills"
                    + "\n2. Refill Bills"
                    + "\n3. Remove Bills");
        }
        else if (key.equals("2")) {
            state = State.GET_FIVES;
            refill = true;
            screen.speak("Number of fives: ");
        }
        else if (key.equals("3")) {
            state = State.GET_FIVES;
            refill = false;
            screen.speak("Number of fives: ");
        }
        accumulatedKeys = "";
    }

    /**
     * Logs the user into the account if they enter the proper password associated with that account.
     *
     * @param key the key(s) typed in by the user
     */
    private void loginToAccount(String key) {
        accumulatedKeys += key;
        if (currentUser.checkPassword(accumulatedKeys)) {
            state = State.MAIN_MENU;
            screen.speak("Welcome " + currentUser.getName() + "!");
            screen.speak("\nSelect an account (1 or 2): \n" +
                                "1. Checking - $" + currentUser.getCheckAccountBalance()
                                + "\n2. Savings - $" +currentUser.getSavingAccountBalance());
        }
        else {
            screen.speak("Incorrect passcode. Try again!");
        }
        accumulatedKeys = "";
    }

    /**
     * Reacts to the user's input from the Main Menu options (selecting which account)
     *
     * @param key the key(s) typed in by the user
     */
    private void mainMenu(String key) {
        if (key.equals("1")) {
            state = State.CHECKING_SELECTED;
            currentAccount = currentUser.getCheckAccount();
            screen.speak(OPTIONS_MENU_TEXT);
        }
        else if (key.equals("2")) {
            state = State.SAVING_SELECTED;
            currentAccount = currentUser.getSavingAccount();
            screen.speak(OPTIONS_MENU_TEXT_NO_SERVER);
        }
        accumulatedKeys = "";
    }

    /**
     * Reacts to user's input if they chose to do something via their checking account...note
     * that the checking account is able to do server-wide/bank-wide transfers whereas the
     * savings account cannot (based on instructions)
     *
     * @param key the key(s) typed in by the user
     */
    private void optionsMenuChecking(String key) {
        if (key.equals("1")) {
            state = State.WITHDRAW;
            screen.speak("Enter the amount you'd like to withdraw: ");
        }
        else if (key.equals("2")) {
            state = State.DEPOSIT;
            screen.speak("Enter the amount you'd like to deposit (REMINDER - we only take 20s):");
        }
        else if (key.equals("3")) {
            state = State.LOCAL_TRANSFER;
            screen.speak("Enter the amount you'd like to transfer: ");
        }
        else if (key.equals("4")) {
            state = State.GET_TARGET_USER;
            screen.speak("Enter account number you'd like to transfer to: ");
        }
        accumulatedKeys = "";
    }

    /**
     * Reacts to user's input if they chose to do something via their savings account
     *
     * @param key the key(s) typed in by the user
     */
    private void optionsMenuSaving(String key) {
        if (key.equals("1")) {
            state = State.WITHDRAW;
            screen.speak("Enter the amount you'd like to withdraw: ");
        }
        else if (key.equals("2")) {
            state = State.DEPOSIT;
            screen.speak("Enter the amount you'd like to deposit (REMINDER - we only take 20s):");
        }
        else if (key.equals("3")) {
            state = State.LOCAL_TRANSFER;
            screen.speak("Enter the amount you'd like to transfer: ");
        }
        accumulatedKeys = "";
    }

    /**
     * Manages all transactions/transaction requests...either
     * informs the user it cannot be done (and why it cannot be done)
     * or it will execute the transaction request
     *
     * @param key the key(s) typed in by the user
     */
    private void manageTransactions(String key) {

        // Handles when a user enters a string rather than an integer/double
        while (true) {
            try {
                amount = Double.parseDouble(key);
                break;
            }
            catch (NumberFormatException e) {
                return;
            }
        }

        // Handles if the user has enough money in their account, and if the machine is capable of producing the request.
        if ((Integer.parseInt(key) % 20 != 0 && state == State.DEPOSIT) || (((Integer.parseInt(key) % 5) != 0 && state == State.WITHDRAW)) || (amount > currentAccount.getBalance() && state == State.WITHDRAW)) {
            if (state == State.WITHDRAW) {
                if (amount > currentAccount.getBalance()) {
                    screen.speak("Insufficient funds.  Please try a smaller amount.");
                }
                else {
                    screen.speak("This machine can only produce 100s, 50s, 20s, and 5s.");
                }
            }
            else if (state == State.DEPOSIT) {
                screen.speak("This machine only takes 20s. Please enter a multiple of 20.");
            }
            else if (state == State.LOCAL_TRANSFER) {
                screen.speak("Insufficient funds.  Please try a smaller amount.");
            }
            else if (state == State.SERVER_TRANSFER) {
                screen.speak("Insufficient funds.  Please try a smaller amount.");
            }
            else {
                screen.speak("Invalid request.");
            }
        }
        else {
            switch1: switch (state) {

                // Handles withdrawals for the ATM
                case WITHDRAW:
                    HashMap<Integer, Integer> tmpHolder = currentAtm.produceBills(Integer.parseInt(key));

                    // Handles case where the ATM does not have enough bills.
                    if (tmpHolder == null) {
                        screen.speak("\nSelect an account (1 or 2): \n" +
                                "1. Checking - $" + currentUser.getCheckAccountBalance()
                                + "\n2. Savings - $" +currentUser.getSavingAccountBalance());
                        break switch1;
                    }
                    currentAccount.updateBalance(amount, "withdraw");
                    screen.speak(currentAccount.getAccountType() + ": $" + currentAccount.getBalance());
                    screen.speak("\nSelect an account (1 or 2): \n" +
                            "1. Checking - $" + currentUser.getCheckAccountBalance()
                            + "\n2. Savings - $" +currentUser.getSavingAccountBalance());
                    // Resets the state back to the MAIN_MENU...so the user can resume transactions if they'd like
                    state = State.MAIN_MENU;
                    break;

                // Handles deposits for the ATM
                case DEPOSIT:
                    currentAtm.increaseBillCount(0, Integer.parseInt(key) / 20, 0, 0);
                    currentAccount.updateBalance(amount, "deposit");
                    screen.speak(currentAccount.getAccountType() + ": $" + currentAccount.getBalance() + "\nDeposit completed!");
                    screen.speak("\nSelect an account (1 or 2): \n" +
                            "1. Checking - $" + currentUser.getCheckAccountBalance()
                            + "\n2. Savings - $" +currentUser.getSavingAccountBalance());
                    // Resets the state back to the MAIN_MENU...so the user can resume transactions if they'd like
                    state = State.MAIN_MENU;
                    break;

                // Handles transfers from checking to savings, or vice versa
                case LOCAL_TRANSFER:
                    currentUser.localTransferFunds(currentAccount, amount);
                    screen.speak(currentAccount.getAccountType() + ": $" + currentAccount.getBalance());
                    if (currentAccount.getAccountType() == "Checking") {
                        screen.speak("Savings: $" + currentUser.getSavingAccountBalance());
                    }
                    else if (currentAccount.getAccountType() == "Savings") {
                        screen.speak("Checking: $" + currentUser.getCheckAccountBalance());
                    }
                    screen.speak("Transfer successful!");
                    screen.speak("\nSelect an account (1 or 2): \n" +
                            "1. Checking - $" + currentUser.getCheckAccountBalance()
                            + "\n2. Savings - $" +currentUser.getSavingAccountBalance());
                    // Resets the state back to the MAIN_MENU...so the user can resume transactions if they'd like
                    state = State.MAIN_MENU;
                    break;

                // Handles servers from one user's checking account to another
                case SERVER_TRANSFER:
                    if (currentAccount.getBalance() >= amount) {
                        currentServer.serverTransferFunds(currentUser.getAccountNum(), targetUser.getAccountNum(), amount);
                        screen.speak("Your Checking: $" + currentAccount.getBalance());
                        screen.speak("Target User Checking: $" + targetUser.getCheckAccountBalance());
                        screen.speak("Transfer successful!");
                        screen.speak("\nSelect an account (1 or 2): \n" +
                                "1. Checking - $" + currentUser.getCheckAccountBalance()
                                + "\n2. Savings - $" + currentUser.getSavingAccountBalance());
                        // Resets the state back to the MAIN_MENU...so the user can resume transactions if they'd like
                        state = State.MAIN_MENU;
                        break;
                    }
                    else {
                        screen.speak("Insufficient funds. Please try a smaller amount.");
                    }

            }

        }

    }


    /**
     * This function will attempt to find the target user in the BankServer's list...sets
     * the targetUser to the user if found, otherwise asks the user to retry
     *
     * @param key the key(s) the user has entered
     */
    private void findTargetAccount(String key) {
        targetUser = currentServer.findUser(Integer.parseInt(key));
        if (targetUser == null) {
            screen.speak("User not found. Please try again.");
        }
        else {
            state = State.SERVER_TRANSFER;
            screen.speak("Enter the amount you'd like to transfer: ");
        }
    }

    /**
     * Activates when the user/operator enters 'L'...will send the ATM back to the
     * prompt asking for the account number (initial state)
     */
    public void logout() {
        resetConnection();
    }

    /**
     * Sends the user/operator to the home screen, but only if they are already
     * logged in
     */
    public void home() {
        // Prevents people from logging into the last logged in user by using this function of the simulator
        if (currentUser != null) {
            state = State.MAIN_MENU;
            screen.speak("\nSelect an account (1 or 2): \n" +
                    "1. Checking - $" + currentUser.getCheckAccountBalance()
                    + "\n2. Savings - $" +currentUser.getSavingAccountBalance());
        }

        // Prevents people from logging into the last logged in operator by using this function of the simulator
        else if (operator != null) {
            state = State.OP_MAIN_MENU;
            screen.speak("\nOptions:\n"
                    + "\n1. Display Number of Bills"
                    + "\n2. Refill Bills"
                    + "\n3. Remove Bills");
        }

        // Informs the user they cannot go to the Home section unless they're logged in
        else {
            screen.speak("Please login to an account first.");
        }
    }

    /**
     * Handles getting the number of fives when the operator wants to refill
     * or remove bills from the ATM
     *
     * @param key the key(s) the user has entered
     */
    public void getFives(String key) {
        // Handles when a user enters a string rather than an integer/double
        while (true) {
            try {
                numFives = Integer.parseInt(key);
                break;
            }
            catch (NumberFormatException e) {
                return;
            }
        }
        state = State.GET_TWENTIES;
        screen.speak("Number of twenties: ");
    }

    /**
     * Handles getting the number of twenties when the operator wants to refill
     * or remove bills from the ATM
     *
     * @param key the key(s) the user has entered
     */
    public void getTwenties(String key) {

        // Handles when a user enters a string rather than an integer/double
        while (true) {
            try {
                numTwenties = Integer.parseInt(key);
                break;
            }
            catch (NumberFormatException e) {
                return;
            }
        }
        state = State.GET_FIFTIES;
        screen.speak("Number of fifties: ");
    }

    /**
     * Handles getting the number of fifties when the operator wants to refill
     * or remove bills from the ATM
     *
     * @param key the key(s) the user has entered
     */
    public void getFifties(String key) {

        // Handles when a user enters a string rather than an integer/double
        while (true) {
            try {
                numFifties = Integer.parseInt(key);
                break;
            }
            catch (NumberFormatException e) {
                return;
            }
        }

        // Sets the state to either refilling or removing...just determines whether to add or subtract from the ATM billCount
        if (refill) {
            state = State.GET_HUNDREDS_REFILL;
        }
        else {
            state = State.GET_HUNDREDS_REMOVE;
        }
        screen.speak("Number of hundreds: ");
    }

    /**
     * Handles getting the number of hundreds when the operator wants to refill
     * or remove bills from the ATM
     *
     * @param key the key(s) the user has entered
     */
    public void getHundreds(String key) {

        // Handles when a user enters a string rather than an integer/double...based on my implementation you cannot retry the
        // number of hundreds, so instead it defaults the amount to 0 if an invalid amount is entered
        while (true) {
            try {
                numHundreds = Integer.parseInt(key);
                break;
            }
            catch (NumberFormatException e) {
                screen.speak("Invalid number. No hundreds added.");
                return;
            }
        }
        state = State.OP_MAIN_MENU;
    }

}
