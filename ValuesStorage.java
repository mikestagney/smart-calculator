package calculator;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ValuesStorage {
    private Map<String, BigInteger> variableStore;
    private RegexCheck regexCheck;

    ValuesStorage(){
        variableStore = new HashMap<>();
        regexCheck = RegexCheck.getInstance();
    }
    public boolean addVariable(String userInput) {
        boolean isAdded = false;
        userInput = userInput
                .replaceAll("\\s+", "")
                .replace('=', ' ');
        String[] assignment = userInput.split("\\s+");
        String key = assignment[0];
        BigInteger value = getValue(assignment[assignment.length - 1]);
        if (value != null) {
             variableStore.put(key, value);
             isAdded = true;

        }
        return isAdded;
    }
    public BigInteger getValue(String item) {
        BigInteger value = null;
        if (regexCheck.isVariable(item)) {
            value = variableStore.get(item);
            if (value == null) {
                System.out.println("Unknown variable");
            }
        } else if (regexCheck.isSignedNumber(item)) {
            value = new BigInteger(item);
        }
        return value;
    }
}
