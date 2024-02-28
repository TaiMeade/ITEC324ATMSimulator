import java.util.ArrayList;
import java.util.HashMap;

public class ATM {

    private HashMap<Integer, Integer> billCount = new HashMap<Integer, Integer>();

    /**
     * Represents the ATM (Automated Teller Machine)
     * that the User can interact with
     *
     * @param numFives the number of five dollar bills in the machine
     * @param numTwenties the number of twenty dollar bills in the machine
     * @param numFifties the number of fifty dollar bills in the machine
     * @param numHundreds the number of hundred dollar bills in the machine
     */
    public ATM(Integer numFives, Integer numTwenties, Integer numFifties, Integer numHundreds) {
        billCount.put(5, numFives);
        billCount.put(20, numTwenties);
        billCount.put(50, numFifties);
        billCount.put(100, numHundreds);
    }

    /**
     *
     * @return returns a hashmap of the number of each bill type
     */
    public HashMap<Integer, Integer> getBillCount() {
        return billCount;
    }

//    public int totalInATM() {
//        int total = 0;
//        for (int i : billCount.keySet()) {
//            total += (i * billCount.get(i));
//        }
//        return total;
//    }
//
//    public boolean canProduce(int amount) {
//        if (amount < totalInATM()) {
//            return true;
//        }
//        return false;
//    }

    /**
     * Produces the requested amount (if possible), and updates the number of bills in the ATM
     *
     * @param amount the amount to attempt to produce from the remaining bills in the ATM
     * @return returns null if the amount requested is not a multiple of 5, or if it cannot be produced using the remaining bills...otherwise returns a hashmap of the bills to produce
     */
    public HashMap<Integer, Integer> produceBills(int amount) {
        // Initialize variables
        HashMap<Integer, Integer> billsToProduce = new HashMap<Integer, Integer>();
        int hundreds = 0;
        int fifties = 0;
        int twenties = 0;
        int fives = 0;

        // Returns null immediately if the user requests an amount that can't be reproduced in 5s, 20s, 50s, or 100s
        if ((amount % 5) != 0) {
            return null;
        }

        // Gets the largest amount of hundreds possible, while not allowing the number of hundreds in the ATM to go below 0
        while ((amount >= 100) && (getNumHundreds() > 0)) {
            hundreds += 1;
            amount -= 100;
            billCount.put(100, billCount.get(100) - 1);
        }

        // Gets the largest amount of fifties possible, while not allowing the number of hundreds in the ATM to go below 0
        while ((amount >= 50) && (getNumFifties() > 0)) {
            fifties += 1;
            amount -= 50;
            billCount.put(50, billCount.get(50) - 1);
        }

        // Gets the largest amount of twenties possible, while not allowing the number of hundreds in the ATM to go below 0
        while ((amount >= 20) && (getNumTwenties() > 0)) {
            twenties += 1;
            amount -= 20;
            billCount.put(20, billCount.get(20) - 1);
        }

        // Gets the largest amount of fives possible, while not allowing the number of hundreds in the ATM to go below 0
        while ((amount > 0) && (getNumFives() > 0)) {
            fives += 1;
            amount -= 5;
            billCount.put(5, billCount.get(5) - 1);
        }

        // Handles the case that the ATM didn't have enough bills to fulfill the request...essentially cancels the user's request
        if (amount != 0) {

            // Replaces the bills that were removed...not super efficient, but it works.
            billCount.put(5, billCount.get(5) + fives);
            billCount.put(20, billCount.get(20) + twenties);
            billCount.put(50, billCount.get(50) + fifties);
            billCount.put(100, billCount.get(100) + hundreds);

            System.out.println("The ATM cannot produce this amount with the remaining bills.  We apologize for the inconvenience.");
            return null;
        }

        // Create the hashmap for the amount of each bill to return based on the request withdrawal
        billsToProduce.put(5, fives);
        billsToProduce.put(20, twenties);
        billsToProduce.put(50, fifties);
        billsToProduce.put(100, hundreds);

        return billsToProduce;
    }

    /**
     * Increases, or decreases, the number of bills in the ATM
     *
     * @param numFivesToAdd number of fives to add or remove from the ATM
     * @param numTwentiesToAdd number of twenties to add or remove from the ATM
     * @param numFiftiesToAdd number of fifties to add or remove from the ATM
     * @param numHundredsToAdd number of hundreds to add or remove from the ATM
     */
    public void increaseBillCount(int numFivesToAdd, int numTwentiesToAdd, int numFiftiesToAdd, int numHundredsToAdd) {
        // Checks if increasing (technically decreasing by adding a negative number) results in a negative amount of bills...returns if it does
        if (((billCount.get(5) + numFivesToAdd) < 0)
                || ((billCount.get(20) + numTwentiesToAdd) < 0)
                || ((billCount.get(50) + numFiftiesToAdd) < 0)
                || ((billCount.get(100) + numHundredsToAdd) < 0)) {
            // do nothing
        }
        // If it does not result in a negative number, then carry out the removal
        else {
            billCount.put(5, billCount.get(5) + numFivesToAdd);
            billCount.put(20, billCount.get(20) + numTwentiesToAdd);
            billCount.put(50, billCount.get(50) + numFiftiesToAdd);
            billCount.put(100, billCount.get(100) + numHundredsToAdd);
        }
    }

    public int getNumFives() {
        return getBillCount().get(5);
    }

    public int getNumTwenties() {
        return getBillCount().get(20);
    }

    public int getNumFifties() {
        return getBillCount().get(50);
    }

    public int getNumHundreds() {
        return getBillCount().get(100);
    }

}