package calculator;

import java.util.HashMap;
import java.util.Map;

public class ValuesStorage {
    private Map<String, Integer> variableStore;
    private RegexCheck regexCheck;

    ValuesStorage(){
        variableStore = new HashMap<>();
        regexCheck = RegexCheck.getInstance();
    }
    public void addVariable(String userInput) {
        userInput = userInput
                .replaceAll("\\s+", "")
                .replace('=', ' ');
        String[] assignment = userInput.split("\\s+");
        String key = assignment[0];
        Integer value = getValue(assignment[assignment.length - 1]);
        if (value != null) {
            variableStore.put(key, value);
        }
    }
    public Integer getValue(String item) {
        Integer value = null;
        if (regexCheck.isVariable(item)) {
            value = variableStore.get(item);
            if (value == null) {
                System.out.println("Unknown variable");
            }
        } else if (regexCheck.isSignedNumber(item)) {
            value = Integer.parseInt(item);
        }
        return value;
    }
}
