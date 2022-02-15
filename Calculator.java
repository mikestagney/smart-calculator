package calculator;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {

    private final String SIGNED_DIGIT = "[+-]?\\d+\\s*";  //  SIGNED_DIGIT = "[+-]?\\d+\\s*";
    private final String VARIABLE = "[A-Za-z]+\\s*";
    private final String LOWER_EXPRESSIONS = "(\\++|-+)";
    private final String HIGHER_EXPRESSIONS = "(/|\\*)";
    private final String PARENTHESIS_EXPRESSIONS = "(\\(|\\))";
    private final String EXPRESSIONS;
    private final String ASSIGNMENT = "=\\s*";
    private final String NUMBER_OR_VARIABLE;
    private final Pattern VARIABLE_PATTERN;
    private final Pattern NUMBER_PATTERN;
    private final Pattern VARIABLE_OR_NUMBER_PATTERN;
    private final Pattern EXPRESSIONS_PATTERN;
    private final Pattern EXPRESSIONS_NO_PARENTTHESIS_PATTERN;
    // private final Pattern LOWER_EXPRESSIONS_PATTERN;
    private final Pattern VARIABLE_ASSIGNMENT_PATTERN;
    private Map<String, Integer> variableStore;
    private Deque<String> postFixEquation;


    Calculator() {
        NUMBER_OR_VARIABLE = "(" + SIGNED_DIGIT + "|" + VARIABLE + ")";
        //EQUATION_PATTERN = Pattern.compile(NUMBER_OR_VARIABLE + "(" + EXPRESSIONS + NUMBER_OR_VARIABLE + ")*");
        VARIABLE_ASSIGNMENT_PATTERN = Pattern.compile("\\s*" + VARIABLE + ASSIGNMENT + NUMBER_OR_VARIABLE);

        VARIABLE_PATTERN = Pattern.compile(VARIABLE);
        NUMBER_PATTERN = Pattern.compile(SIGNED_DIGIT);
        VARIABLE_OR_NUMBER_PATTERN = Pattern.compile(NUMBER_OR_VARIABLE);

        EXPRESSIONS = "(" + LOWER_EXPRESSIONS + "|" + HIGHER_EXPRESSIONS + "|" + PARENTHESIS_EXPRESSIONS + ")";
        EXPRESSIONS_PATTERN = Pattern.compile(EXPRESSIONS);
        String EXPRESSIONS_NO_PARENTHESIS = "(" + HIGHER_EXPRESSIONS + "|" + LOWER_EXPRESSIONS + ")";
        EXPRESSIONS_NO_PARENTTHESIS_PATTERN = Pattern.compile(EXPRESSIONS_NO_PARENTHESIS);
        //LOWER_EXPRESSIONS_PATTERN = Pattern.compile(LOWER_EXPRESSIONS);

        variableStore = new HashMap<>();
    }

    public void validateInput(String userInput) {
        Integer currentResult = null;
        if (userInput.charAt(0) == '/') {
            System.out.println("Unknown command");
            return;
        }
        //Matcher equationMatcher = EQUATION_PATTERN.matcher(userInput);
        Matcher assignmentMatcher = VARIABLE_ASSIGNMENT_PATTERN.matcher(userInput);
        if (assignmentMatcher.matches()) {
            addVariable(userInput);
        } else {
           postFixConverter(userInput);
           currentResult = evaluatePostFixEquation();

            if (currentResult != null) {
            System.out.println(currentResult);
        } else {
            handleError(userInput);
            }
        }
    }
    private String[] parseUserInput(String userInput) {
        String holder = userInput
                        .replaceAll(" ", "")
                        .replaceAll("(\\+){2,}", "+")  //  .replaceAll("(\\+)\\1{2,}", "+")
                        .replaceAll("--", "+")
                        .replaceAll("\\+-|-\\+", "-")
                        .replaceAll("(\\+){2,}", "+");

        //System.out.println(holder);

        String[] operandsHolder = holder.split(EXPRESSIONS + "+");
        String VARIABLE_OR_DIGIT = "(" + "\\d+" + "|" + VARIABLE + ")";
        String[] operands = Arrays.stream(operandsHolder)
                .filter(e -> {
                    Pattern digits = Pattern.compile(VARIABLE_OR_DIGIT);
                    Matcher matcher = digits.matcher(e);
                    return matcher.matches();})
                .toArray(String[]::new);

        String charHolder = holder.replaceAll(VARIABLE_OR_DIGIT, "");
        char[] operators = charHolder.toCharArray();

        // System.out.println(Arrays.toString(operands));
        // System.out.println(Arrays.toString(operators));
        String[] equation = new String[operators.length + operands.length];

        int operatorCounter = 0;
        int operandCounter = 0;
        int equationCounter = 0;

        for (int i = 0; i < holder.length(); i++) {
            if (operandCounter <= operands.length - 1 && holder.charAt(i) == operands[operandCounter].charAt(0)) {
                equation[equationCounter] = operands[operandCounter];
                equationCounter++;
                operandCounter++;
            } else if (operatorCounter <= operators.length - 1 && holder.charAt(i) == operators[operatorCounter]) {
                equation[equationCounter] = Character.toString(operators[operatorCounter]);
                equationCounter++;
                operatorCounter++;
            }
        }
        //System.out.println(Arrays.toString(equation));
        return equation;
    }


    private void postFixConverter(String userInput) {
        String[] equation = parseUserInput(userInput);

        postFixEquation = new ArrayDeque<>();
        Deque<String> stack = new ArrayDeque<>();

        for (String token: equation) {
            Matcher valueMatch = VARIABLE_OR_NUMBER_PATTERN.matcher(token);
            Matcher expressionMatch = EXPRESSIONS_PATTERN.matcher(token);
            if (valueMatch.matches()) {
                postFixEquation.offerLast(token); // add number or variable to result
            } else if (expressionMatch.matches()) {
                String topStackItem = stack.peekLast();
                if (topStackItem != null && token.equals(")")) {
                    while (stack.peekLast() != null && !stack.peekLast().equals("(")) {
                        postFixEquation.offerLast(stack.pollLast());
                    }
                    if (stack.peekLast() != null && stack.peekLast().equals("(")) {
                        // remove left parenthesis from stack and discard
                        stack.pollLast();
                        continue; // right parenthesis is the token, so don't add it to postFixEquation
                    } else {
                        postFixEquation.offerLast("(");
                        stack.clear();
                        break;
                    }

                }
                if (stack.size() > 0 && !higherPrecedenceNewOperator(topStackItem, token) && !"(".equals(topStackItem)) {
                    while (stack.size() > 0 && !stack.peekLast().equals("(")) {
                        if (!lowerPrecedenceNewOperator(topStackItem, token)) {
                            postFixEquation.offerLast(stack.pollLast());
                            break;
                        } else {
                            postFixEquation.offerLast(stack.pollLast());
                        }
                    }
                }
                stack.addLast(token);
            }
        }
        while (stack.size() > 0) {
            postFixEquation.addLast(stack.pollLast());
        }
        // System.out.println(postFixEquation);

    }

    private boolean higherPrecedenceNewOperator(String oldOperator, String newOperator) {
        int oldValue = getPrecedenceValue(oldOperator);
        int newValue = getPrecedenceValue(newOperator);
        return oldValue < newValue;

    }
    private boolean lowerPrecedenceNewOperator(String oldOperator, String newOperator) {
        int oldValue = getPrecedenceValue(oldOperator);
        int newValue = getPrecedenceValue(newOperator);
        return oldValue > newValue;
    }
    private int getPrecedenceValue(String operator) {
        int precedence = -1;
        switch (operator) {
            case "(":
            case ")":
                precedence = 3;
                break;
            case "*":
            case "/":
                precedence = 2;
                break;
            case "+":
            case "-":
                precedence = 1;

        }
        return precedence;
    }
    private Integer evaluatePostFixEquation() {
        if (postFixEquation.contains("(")) {
            return null;
        }

        Integer finalResult;
        Deque<Integer> stack = new ArrayDeque<>();

        while (!postFixEquation.isEmpty()) {
            String token = postFixEquation.pollFirst();
            Matcher matchNumberVariable = VARIABLE_OR_NUMBER_PATTERN.matcher(token);
            Matcher matchOperator = EXPRESSIONS_NO_PARENTTHESIS_PATTERN.matcher(token);

            // if variable or number, get value, push on stack
            Integer currentResult = null;
            if (matchNumberVariable.matches()) {
                currentResult = getValue(token);
            } else if (matchOperator.matches()) {
                try {
                    Integer secondOperand = stack.pollLast();
                    Integer firstOperand = stack.pollLast();
                    currentResult = performOperation(firstOperand, secondOperand, token);
                } catch (Exception e) {
                    return null;
                }
            }
            if (currentResult != null) {
                stack.offerLast(currentResult);
            } else {
                return null;
            }
        }
        finalResult = stack.pollLast();
        // System.out.println(finalResult);
        return finalResult;
    }

    private Integer performOperation(Integer op1, Integer op2, String operator) {
        Integer result = null;
        try {
            switch (operator) {
                case "+":
                    result = op1 + op2;
                    break;
                case "-":
                    result = op1 - op2;
                    break;
                case "*":
                    result = op1 * op2;
                    break;
                case "/":
                    result = op1 / op2;
                    break;
            }
        } catch (Exception ignored) {
        }
        return result;
    }

    private void parseEquation(String userInput) {
        String[] equation = userInput.split("\\s+");

        Integer result = getValue(equation[0]);
        for (int i = 1; i < equation.length; i += 2) {
            Integer temp;
            try {
                temp = getValue(equation[i + 1]);
                if (equation[i].contains("-") && equation[i].length() % 2 != 0) {
                    result = subtraction(temp, result);
                } else {
                    result = addition(temp, result);
                }
            } catch (Exception e) {
                System.out.println("Invalid expression");
                return;
            }
        }
        if (result != null) {
            System.out.println(result);
        }
    }
    private void handleError(String equation) {
            if (equation.contains("=")) {
                String[] tokens = equation.split("\\s+");
                if (tokens[0].matches(".*\\d+.*") && tokens[0].matches(".*[A-Za-z]+.*")) {
                    System.out.println("Invalid identifier");
                } else {
                    System.out.println("Invalid assignment");
                }
            } else {
                System.out.println("Invalid expression");
            }
        }

    private void addVariable(String userInput) {
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
    private Integer getValue(String item) {
        Integer value = null;
        Matcher variableMatcher = VARIABLE_PATTERN.matcher(item);
        Matcher numberMatcher = NUMBER_PATTERN.matcher(item);
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
    private int addition(int num, int total) {
        return total + num;
    }
    private int subtraction(int num, int total) {
        return total - num;
    }

}
