/*
 * Creates a user to be placed within the BankServer.
 * The purpose of this class is to store individual
 * user information.
 */
public class User {

    private String name;
    private int accountNum;
    private String passcode;
    private Account checkAccount;
    private Account savingAccount;

    /**
     * Construct a User object.
     *
     * @param name the User's name
     * @param accountNum the User's account number
     * @param passcode the User's passcode/pin
     * @param checking the User's checking account
     * @param savings the User's savings account
     */
    public User(String name, int accountNum, String passcode, Account checking, Account savings) {
        this.name = name;
        this.accountNum = accountNum;
        this.passcode = passcode;
        this.checkAccount = checking;
        this.savingAccount = savings;
    }

    /**
     * Allows user to withdraw from one of their
     * accounts and updates balance accordingly
     *
     * @param srcAccount the account to withdraw from
     * @param amount the amount to withdraw
     */

    public void withdraw(String srcAccount, double amount) {
        // Substitutes in the proper values for each method based on which account the user is acting upon
        if (srcAccount.equalsIgnoreCase("checking")) {
            checkAccount.updateBalance(amount, "withdraw");
        }
        else if (srcAccount.equalsIgnoreCase("savings")) {
            savingAccount.updateBalance(amount, "withdraw");
        }
    }

    /**
     * Allows user to deposit from one of their
     * accounts and updates balance accordingly
     *
     * @param srcAccount the account to deposit to
     * @param amount the amount to deposit
     */
    public void deposit(String srcAccount, double amount) {
        // Substitutes in the proper values for each method based on which account the user is acting upon
        if (srcAccount.equalsIgnoreCase("checking")) {
            checkAccount.updateBalance(amount, "deposit");
        }
        else if (srcAccount.equalsIgnoreCase("savings")) {
            savingAccount.updateBalance(amount, "deposit");
        }
    }

    /**
     * Transfers funds within one user's accounts...
     * from checking to saving or vice versa
     *
     * @param srcAccount the account to transfer funds from
     * @param amount the amount to transfer
     */
    public void localTransferFunds(Account srcAccount, double amount) {
        // Handles which account to transfer to absed on which account the user is acting upon...no point in allowing a transfer from an account to itself
        if (srcAccount.getAccountType()== "Checking") {
            checkAccount.updateBalance(amount, "withdraw");
            savingAccount.updateBalance(amount, "deposit");
        }
        else if (srcAccount.getAccountType() == "Savings") {
            savingAccount.updateBalance(amount, "withdraw");
            checkAccount.updateBalance(amount, "deposit");
        }
    }

    /**
     * Checks to see if the input password/passcode
     * is equal to the User's stored password
     *
     * @param password the ATM customer's inputted password
     */
    public boolean checkPassword(String password) {
        if (password.equals(passcode)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Getters for the User class
     */
    public double getCheckAccountBalance() {
        return checkAccount.getBalance();
    }
    public double getSavingAccountBalance() {
        return savingAccount.getBalance();
    }
    public int getAccountNum() {
        return accountNum;
    }
    public String getName() {
        return name;
    }
    public Account getCheckAccount() {
        return checkAccount;
    }
    public Account getSavingAccount() {
        return savingAccount;
    }

}
