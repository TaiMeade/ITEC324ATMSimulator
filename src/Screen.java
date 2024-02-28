import java.util.Scanner;

public class Screen {

    private Scanner scanner;

    /**
     * Construct a screen object.
     *
     * @param aScanner reads text from the ATM user's input
     */
    public Screen(Scanner aScanner) {
        scanner = aScanner;
    }

    /**
     * Speak a message to the terminal
     *
     * @param output the text that will be output to terminal
     */
    public void speak(String output) {
        System.out.println(output);
    }

    /**
     * Loops reading the user's input and passes
     * the input to the Connection object's methods
     * logout, login, etc.
     *
     * @param connection the Connection object to track the state of the ATM
     */
    public void run(Connection connection) {
        boolean continueOn = true;
        while (continueOn) {
            String input = scanner.nextLine();
            if (input == null) {
                return;
            }

            // If the user enters 'L' it will reset the simulation back to the initial state...prompts for the account number
            if (input.equalsIgnoreCase("L")) {
                connection.logout();

            // If the user enters 'Q' it will end the simulation
            } else if (input.equalsIgnoreCase("Q")) {
                continueOn = false;

            // If the user enters 'H' then the simulator will return them to the initial menu screen...
            // for users the menu for which account to operate on (Checking, Savings)
            // for operators the menu to select the function they want to conduct (Display, Refill, Remove)
            } else if (input.equalsIgnoreCase("H")) {
                connection.home();

            // Handles the majority of inputs
            } else if (input.length() >= 1) {
                connection.dial(input);
            }
        }

    }
}

