public class Account {
    private double balance;
    private String accountType;

    /**
     * Represents an account to be used within the User class
     *
     * @param bal the balance of the account
     * @param type the type of account ('Checking' or 'Savings')
     */
    public Account(double bal, String type) {
        balance = bal;
        accountType = type;
    }


    /**
     * Updates the balance of a particular account...adds if it is a deposit type and subtracts if it is a withdrawal.
     *
     * @param amount the amount to increase or decrease the balance by
     * @param transactionType the type of transaction...either withdraw, or deposit
     */
    public void updateBalance(double amount, String transactionType) {
        // Handles withdrawals (for the account balance)...subtracts amount from the previous balance
        if (transactionType.equalsIgnoreCase("withdraw")) {
            balance -= amount;
        }
        // Handles deposits (for the account balance)...adds amount to the previous balance
        else if (transactionType.equalsIgnoreCase("deposit")) {
            balance += amount;
        }
    }

    public double getBalance() {
        return balance;
    }
    public String getAccountType() {
        return accountType;
    }

}
