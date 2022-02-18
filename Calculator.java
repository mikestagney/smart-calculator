package calculator;

import java.util.*;

public class Calculator {

    RegexCheck regexCheck;
    private Deque<String> postFixEquation;
    ValuesStorage valuesStorage;

    Calculator() {
        regexCheck = new RegexCheck();
        valuesStorage = new ValuesStorage();
    }

    public void validateInput(String userInput) {
        if (userInput.charAt(0) == '/') {
            System.out.println("Unknown command");
            return;
        }
        Integer currentResult;
        if (regexCheck.isVariableAssignment(userInput)) {
            valuesStorage.addVariable(userInput);
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
    private void postFixConverter(String userInput) {
        String[] InfixEquation = tokenizeUserInput(userInput);

        postFixEquation = new ArrayDeque<>();
        Deque<String> stack = new ArrayDeque<>();

        for (String token: InfixEquation) {
            if (regexCheck.isSignedNumberOrVariable(token)) {
                postFixEquation.offerLast(token); // add number or variable to result
            } else if (regexCheck.isExpression(token)) {
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
                        // equation has no matching left parenthesis, so
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
    }
    private String[] tokenizeUserInput(String userInput) {
        String infixNormalized = normalizeInput(userInput);
        String[] operands = extractOperands(infixNormalized);
        char[] operators = extractOperators(infixNormalized);

        return createInfixEquation(operands, operators, infixNormalized);
    }
    private String normalizeInput(String userInput) {
        return userInput
                .replaceAll(" ", "")
                .replaceAll("(\\+){2,}", "+")
                .replaceAll("--", "+")
                .replaceAll("\\+-|-\\+", "-")
                .replaceAll("(\\+){2,}", "+");
    }
    private String[] extractOperands(String infixNoSpaces) {
        String[] operandsHolder = infixNoSpaces.split(regexCheck.EXPRESSIONS + "+");
        return Arrays.stream(operandsHolder)
                .filter(e -> regexCheck.isUnsignedNumberOrVariable(e))
                .toArray(String[]::new);
    }
    private char[] extractOperators(String infixNoSpaces) {
        String charHolder = infixNoSpaces.replaceAll(regexCheck.UNSIGNED_NUMBER_OR_VARIABLE, "");
        return charHolder.toCharArray();
    }
    private String[] createInfixEquation(String[] operands, char[] operators, String infixNoSpaces) {
        String[] equation = new String[operators.length + operands.length];

        int operatorCounter = 0;
        int operandCounter = 0;
        int equationCounter = 0;

        for (int i = 0; i < infixNoSpaces.length(); i++) {
            if (operandCounter <= operands.length - 1 && infixNoSpaces.charAt(i) == operands[operandCounter].charAt(0)) {
                equation[equationCounter] = operands[operandCounter];
                equationCounter++;
                operandCounter++;
            } else if (operatorCounter <= operators.length - 1 && infixNoSpaces.charAt(i) == operators[operatorCounter]) {
                equation[equationCounter] = Character.toString(operators[operatorCounter]);
                equationCounter++;
                operatorCounter++;
            }
        }
        return equation;
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

            // if variable or number, get value, push on stack
            Integer currentResult = null;
            if (regexCheck.isSignedNumberOrVariable(token)) {
                currentResult = valuesStorage.getValue(token);
            } else if (regexCheck.isExpressionNoParentheses(token)) {
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
}

