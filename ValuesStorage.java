package calculator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValuesStorage {

    private Map<String, Integer> variableStore;
    private final String SIGNED_DIGIT = "[+-]?\\d+\\s*";
    private final String VARIABLE = "[A-Za-z]+\\s*";
    private final Pattern VARIABLE_PATTERN;
    private final Pattern SIGNED_NUMBER_PATTERN;

    ValuesStorage(){
        variableStore = new HashMap<>();
        VARIABLE_PATTERN = Pattern.compile(VARIABLE);
        SIGNED_NUMBER_PATTERN = Pattern.compile(SIGNED_DIGIT);
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
        Matcher variableMatcher = VARIABLE_PATTERN.matcher(item);
        Matcher numberMatcher = SIGNED_NUMBER_PATTERN.matcher(item);
        if (variableMatcher.matches()) {
            value = variableStore.get(item);
            if (value == null) {
                System.out.println("Unknown variable");
            }
        } else if (numberMatcher.matches()) {
            value = Integer.parseInt(item);
        }
        return value;
    }

}
