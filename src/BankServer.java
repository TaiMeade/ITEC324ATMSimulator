import java.util.ArrayList;

public class BankServer {

    private ArrayList<User> userList;
    private Operator certifiedOperator;

    /**
     * Represents the bank server that the user's are stored on
     *
     * @param userList a list of User objects
     */
    public BankServer(ArrayList userList, Operator operator) {
        this.userList = userList;
        this.certifiedOperator = operator;
    }

    /**
     * Finds the User within the bank server...
     * if the User is not found it returns null
     *
     * @param accountNumber represents the account number to be searched for
     */
    public User findUser(int accountNumber) {
        // Iterates through each user and returns the user if the accountNumber is within the list of users
        for (int i = 0; i < userList.size(); i++) {
            User tempUser = userList.get(i);
            if (accountNumber == tempUser.getAccountNum()) {
                return tempUser;
            }
        }
        return null;
    }

    /**
     * Handles transferring from one User's checking to another
     *
     * @param srcAccountNum represents the account to be transferred from
     * @param targetAccountNum represents the account to be transferred to
     * @param amount the amount to be transferred
     */
    public void serverTransferFunds(int srcAccountNum, int targetAccountNum, double amount) {
        User source = findUser(srcAccountNum);
        User target = findUser(targetAccountNum);

        // Transfer from one user to the other...withdraws from the source and deposits to the target
        source.withdraw("checking", amount);
        target.deposit("checking", amount);
    }

    /**
     * Attempts to find the proper operator for that particular bank by checking the requested operator number against the proper one
     *
     * @param opNum the operator number to check
     * @return returns the Operator object if it is found...otherwise returns null indicating no operator was found
     */
    public Operator findOperator(int opNum) {
        if (opNum == certifiedOperator.getOpNum()) {
            return certifiedOperator;
        }
        else {
            return null;
        }
    }
}
