import java.util.ArrayList;
import java.util.HashMap;

public class Operator {

    private int opNum;
    private String passcode;

    /**
     * Constructor for an ATM Operator
     *
     * @param opNum the number associated with that operator
     * @param passcode the string of characters representing that operator's passcode
     */
    public Operator(int opNum, String passcode) {
        this.opNum = opNum;
        this.passcode = passcode;
    }

    public int getOpNum() {
        return opNum;
    }

    /**
     * Checks to see if the passcode matches the operator's passcode.
     *
     * @param code
     */
    public boolean checkPasscode(String code) {
        if (code.equals(passcode)) {
            return true;
        }
        else {
            return false;
        }
    }
}
