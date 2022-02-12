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
    private final Pattern HIGHER_EXPRESSIONS_PATTERN;
    private final Pattern LOWER_EXPRESSIONS_PATTERN;
    private final Pattern VARIABLE_ASSIGNMENT_PATTERN;
    private Map<String, Integer> variableStore;
    private Deque<String> postFixEquation;

    Calculator() {
        NUMBER_OR_VARIABLE = "(" + SIGNED_DIGIT + "|" + VARIABLE + ")";
        //EQUATION_PATTERN = Pattern.compile(NUMBER_OR_VARIABLE + "(" + EXPRESSIONS + NUMBER_OR_VARIABLE + ")*");
        VARIABLE_ASSIGNMENT_PATTERN = Pattern.compile(VARIABLE + ASSIGNMENT + NUMBER_OR_VARIABLE);

        VARIABLE_PATTERN = Pattern.compile(VARIABLE);
        NUMBER_PATTERN = Pattern.compile(SIGNED_DIGIT);
        VARIABLE_OR_NUMBER_PATTERN = Pattern.compile(NUMBER_OR_VARIABLE);

        EXPRESSIONS = "(" + LOWER_EXPRESSIONS + "|" + HIGHER_EXPRESSIONS + "|" + PARENTHESIS_EXPRESSIONS + ")";
        EXPRESSIONS_PATTERN = Pattern.compile(EXPRESSIONS);
        String HIGHER_EXPRESSIONS_PARENTHESIS = "(" + HIGHER_EXPRESSIONS + "|" + PARENTHESIS_EXPRESSIONS + ")";
        // create the highest expression pattern with ( )
        HIGHER_EXPRESSIONS_PATTERN = Pattern.compile(HIGHER_EXPRESSIONS_PARENTHESIS);
        LOWER_EXPRESSIONS_PATTERN = Pattern.compile(LOWER_EXPRESSIONS);

        variableStore = new HashMap<>();
    }

    public void validateInput(String userInput) {
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
        }
        //else {
          //  handleError(userInput);
        // }
    }
    private String[] parseUserInput(String userInput) {
        String holder = userInput
                        .replaceAll(" ", "")
                        .replaceAll("(\\+)\\1{2,}", "+")
                        .replaceAll("--", "+")
                        .replaceAll("(\\+)\\1{2,}", "+");

        System.out.println(holder);
        String[] operands = holder.split(EXPRESSIONS + "+");
        String VARIABLE_OR_DIGIT = "(" + "\\d+" + "|" + VARIABLE + ")";

        String charHolder = holder.replaceAll(VARIABLE_OR_DIGIT, "");
        char[] operators = charHolder.toCharArray();

        System.out.println(Arrays.toString(operands));
        System.out.println(Arrays.toString(operators));
        String[] equation = new String[operators.length + operands.length];

        // need to figure out loop to build array
        int operatorCounter = 0;
        int operandCounter = 0;
        int equationCounter = 0;

        for (int i = 0; i < holder.length(); i++) {
            System.out.println(holder.charAt(i) + " " + operands[operandCounter].charAt(0) + " " + operators[operatorCounter]);
            if (operandCounter <= operands.length - 1 && holder.charAt(i) == operands[operandCounter].charAt(0)) {
                equation[equationCounter] = operands[operandCounter];
                equationCounter++;
                operandCounter++;
            } else if (operatorCounter <= operators.length - 1 && holder.charAt(i) == operators[operatorCounter]) {
                equation[equationCounter] = Character.toString(operators[operatorCounter]);
                equationCounter++;
                operatorCounter++;
            }
            System.out.println(Arrays.toString(equation));
            System.out.println();
        }



        System.out.println(Arrays.toString(equation));

        // just for testing this method
        System.exit(0);

        return equation;
    }


    private void postFixConverter(String userInput) {


        // String[] equation = userInput.split("\\s+");
        String[] equation = parseUserInput(userInput);
        // System.out.println(Arrays.toString(equation));


        postFixEquation = new ArrayDeque<>();
        Deque<String> stack = new ArrayDeque<>();

        for (String token: equation) {
            // System.out.println("token is " + token);
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
                    String junk = stack.pollLast();  // remove left parenthesis from stack and discard
                    // System.out.println("top of stack " + junk + " and the token is " + token);
                    continue; // right parenthesis is the token, so don't add it to postFixEquation
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
            /*
            System.out.println("Stack is " + stack);
            System.out.println("postfix is " + postFixEquation);
            System.out.println();*/
        }
        // System.out.println("Stack at the end loop" + stack);
        while (stack.size() > 0) {
            postFixEquation.addLast(stack.pollLast());
        }

        System.out.println(postFixEquation);

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
        userInput = userInput.replace('=', ' ');
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
