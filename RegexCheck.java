package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexCheck {

    private final String SIGNED_NUMBER = "[+-]?\\d+\\s*";
    private final String VARIABLE = "[A-Za-z]+\\s*";
    private final String OPERATORS = "(\\++|-+|/|\\*)";
    private final String PARENTHESIS = "(\\(|\\))";
    private final String OPERATORS_PARENTHESIS;
    private final String ASSIGNMENT = "=\\s*";
    private final String SIGNED_NUMBER_OR_VARIABLE;
    private final String UNSIGNED_NUMBER_OR_VARIABLE;
    private final Pattern VARIABLE_PATTERN;
    private final Pattern SIGNED_NUMBER_PATTERN;
    private final Pattern SIGNED_NUMBER_OR_VARIABLE_PATTERN;
    private final Pattern UNSIGNED_NUMBER_OR_VARIABLE_PATTERN;
    private final Pattern OPERATORS_PARENTHESIS_PATTERN;
    private final Pattern OPERATORS_NO_PARENTHESES_PATTERN;
    private final Pattern VARIABLE_ASSIGNMENT_PATTERN;

    private static RegexCheck instance;

    private RegexCheck() {
        UNSIGNED_NUMBER_OR_VARIABLE = "(" + "\\d+" + "|" + VARIABLE + ")";
        UNSIGNED_NUMBER_OR_VARIABLE_PATTERN = Pattern.compile(UNSIGNED_NUMBER_OR_VARIABLE);

        SIGNED_NUMBER_OR_VARIABLE = "(" + SIGNED_NUMBER + "|" + VARIABLE + ")";
        VARIABLE_ASSIGNMENT_PATTERN = Pattern.compile("\\s*" + VARIABLE + ASSIGNMENT + SIGNED_NUMBER_OR_VARIABLE);

        VARIABLE_PATTERN = Pattern.compile(VARIABLE);
        SIGNED_NUMBER_PATTERN = Pattern.compile(SIGNED_NUMBER);
        SIGNED_NUMBER_OR_VARIABLE_PATTERN = Pattern.compile(SIGNED_NUMBER_OR_VARIABLE);

        OPERATORS_PARENTHESIS = "(" + OPERATORS + "|" + PARENTHESIS + ")";
        OPERATORS_PARENTHESIS_PATTERN = Pattern.compile(OPERATORS_PARENTHESIS);

        OPERATORS_NO_PARENTHESES_PATTERN = Pattern.compile(OPERATORS);
    }
    public static RegexCheck getInstance() {
        if (instance == null) {
            instance = new RegexCheck();
        }
        return instance;
    }
    public boolean isVariable(String token) {
        Matcher variableMatcher = VARIABLE_PATTERN.matcher(token);
        return variableMatcher.matches();
    }
    public boolean isSignedNumber(String token) {
        Matcher numberMatcher = SIGNED_NUMBER_PATTERN.matcher(token);
        return numberMatcher.matches();
    }

    public boolean isVariableAssignment(String userInput) {
        Matcher assignmentMatcher = VARIABLE_ASSIGNMENT_PATTERN.matcher(userInput);
        return assignmentMatcher.matches();
    }

    public boolean isSignedNumberOrVariable(String token) {
        Matcher valueMatch = SIGNED_NUMBER_OR_VARIABLE_PATTERN.matcher(token);
        return valueMatch.matches();
    }

    public boolean isOperatorOrParenthesis(String token) {
        Matcher operatorParenthesisMatch = OPERATORS_PARENTHESIS_PATTERN.matcher(token);
        return operatorParenthesisMatch.matches();
    }

    public boolean isUnsignedNumberOrVariable(String token) {
        Matcher matcher = UNSIGNED_NUMBER_OR_VARIABLE_PATTERN.matcher(token);
        return matcher.matches();
    }
    public boolean isOperatorNoParentheses(String token) {
        Matcher operatorMatch = OPERATORS_NO_PARENTHESES_PATTERN.matcher(token);
        return operatorMatch.matches();
    }
    public String getOPERATORS_PARENTHESIS_regex() {
        return OPERATORS_PARENTHESIS;
    }
    public String getUNSIGNED_NUMBER_OR_VARIABLE_regex() {
        return UNSIGNED_NUMBER_OR_VARIABLE;
    }

}
