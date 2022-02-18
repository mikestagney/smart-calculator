package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexCheck {

    private final String SIGNED_NUMBER = "[+-]?\\d+\\s*";
    private final String VARIABLE = "[A-Za-z]+\\s*";
    private final String LOWER_EXPRESSIONS = "(\\++|-+)";
    private final String HIGHER_EXPRESSIONS = "(/|\\*)";
    private final String PARENTHESIS_EXPRESSIONS = "(\\(|\\))";
    public final String EXPRESSIONS;
    private final String ASSIGNMENT = "=\\s*";
    private final String SIGNED_NUMBER_OR_VARIABLE;
    public final String UNSIGNED_NUMBER_OR_VARIABLE;
    private final Pattern VARIABLE_PATTERN;
    private final Pattern SIGNED_NUMBER_PATTERN;
    private final Pattern SIGNED_NUMBER_OR_VARIABLE_PATTERN;
    private final Pattern UNSIGNED_NUMBER_OR_VARIABLE_PATTERN;
    private final Pattern EXPRESSIONS_PATTERN;
    private final Pattern EXPRESSIONS_NO_PARENTHESES_PATTERN;
    private final Pattern VARIABLE_ASSIGNMENT_PATTERN;

    RegexCheck() {
        UNSIGNED_NUMBER_OR_VARIABLE = "(" + "\\d+" + "|" + VARIABLE + ")";
        UNSIGNED_NUMBER_OR_VARIABLE_PATTERN = Pattern.compile(UNSIGNED_NUMBER_OR_VARIABLE);

        SIGNED_NUMBER_OR_VARIABLE = "(" + SIGNED_NUMBER + "|" + VARIABLE + ")";
        VARIABLE_ASSIGNMENT_PATTERN = Pattern.compile("\\s*" + VARIABLE + ASSIGNMENT + SIGNED_NUMBER_OR_VARIABLE);

        VARIABLE_PATTERN = Pattern.compile(VARIABLE);
        SIGNED_NUMBER_PATTERN = Pattern.compile(SIGNED_NUMBER);
        SIGNED_NUMBER_OR_VARIABLE_PATTERN = Pattern.compile(SIGNED_NUMBER_OR_VARIABLE);

        EXPRESSIONS = "(" + LOWER_EXPRESSIONS + "|" + HIGHER_EXPRESSIONS + "|" + PARENTHESIS_EXPRESSIONS + ")";
        EXPRESSIONS_PATTERN = Pattern.compile(EXPRESSIONS);

        String EXPRESSIONS_NO_PARENTHESIS = "(" + HIGHER_EXPRESSIONS + "|" + LOWER_EXPRESSIONS + ")";
        EXPRESSIONS_NO_PARENTHESES_PATTERN = Pattern.compile(EXPRESSIONS_NO_PARENTHESIS);

    }

    public boolean isVariableAssignment(String userInput) {
        Matcher assignmentMatcher = VARIABLE_ASSIGNMENT_PATTERN.matcher(userInput);
        return assignmentMatcher.matches();
    }

    public boolean isSignedNumberOrVariable(String token) {
        Matcher valueMatch = SIGNED_NUMBER_OR_VARIABLE_PATTERN.matcher(token);
        return valueMatch.matches();
    }

    public boolean isExpression(String token) {
        Matcher expressionMatch = EXPRESSIONS_PATTERN.matcher(token);
        return expressionMatch.matches();
    }

    public boolean isUnsignedNumberOrVariable(String token) {
        Matcher matcher = UNSIGNED_NUMBER_OR_VARIABLE_PATTERN.matcher(token);
        return matcher.matches();
    }
    public boolean isExpressionNoParentheses(String token) {
        Matcher expressionMatch = EXPRESSIONS_NO_PARENTHESES_PATTERN.matcher(token);
        return expressionMatch.matches();
    }


}
