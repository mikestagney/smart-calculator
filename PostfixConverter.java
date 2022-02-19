package calculator;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class PostfixConverter {

    private Deque<String> postFixEquation;
    RegexCheck regexCheck;

    PostfixConverter() {
        regexCheck = RegexCheck.getInstance();
    }

    public Deque<String> infixToPostfix(String userInput) {
        String[] InfixEquation = tokenizeUserInput(userInput);

        postFixEquation = new ArrayDeque<>();
        Deque<String> stack = new ArrayDeque<>();

        for (String token: InfixEquation) {
            if (regexCheck.isSignedNumberOrVariable(token)) {
                postFixEquation.offerLast(token); // add number or variable to result
            } else if (regexCheck.isOperatorOrParenthesis(token)) {
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
                        // equation has no matching left parenthesis, so add one and break loop - evalutatePostFix() will catch error
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
        return postFixEquation;
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
        String[] operandsHolder = infixNoSpaces.split(regexCheck.getOPERATORS_PARENTHESIS() + "+");
        return Arrays.stream(operandsHolder)
                .filter(e -> regexCheck.isUnsignedNumberOrVariable(e))
                .toArray(String[]::new);
    }
    private char[] extractOperators(String infixNoSpaces) {
        String charHolder = infixNoSpaces.replaceAll(regexCheck.getUNSIGNED_NUMBER_OR_VARIABLE(), "");
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
}
