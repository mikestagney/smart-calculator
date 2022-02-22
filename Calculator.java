package calculator;

import java.math.BigInteger;
import java.util.*;

public class Calculator {

    RegexCheck regexCheck;
    private Deque<String> postFixEquation;
    ValuesStorage valuesStorage;
    PostfixConverter postfixConverter;

    Calculator() {
        regexCheck = RegexCheck.getInstance();
        valuesStorage = new ValuesStorage();
        postfixConverter = new PostfixConverter();
    }

    public void validateInput(String userInput) {
        if (userInput.charAt(0) == '/') {
            System.out.println("Unknown command");
            return;
        }
        BigInteger currentResult;
        if (regexCheck.isVariableAssignment(userInput)) {
            if (!valuesStorage.addVariable(userInput)) {
                handleError(userInput);
            }
        } else {
           postFixEquation = postfixConverter.infixToPostfix(userInput);
           if (postFixEquation.isEmpty()) {
               handleError(userInput);
           } else {
                currentResult = evaluatePostFixEquation();
                if (currentResult != null) {
                    System.out.println(currentResult);
                } else {
                    handleError(userInput);
                }
            }
        }
    }

    private BigInteger evaluatePostFixEquation() {
        BigInteger finalResult;
        Deque<BigInteger> stack = new ArrayDeque<>();

        while (!postFixEquation.isEmpty()) {
            String token = postFixEquation.pollFirst();
            BigInteger currentResult = null;

            if (regexCheck.isSignedNumberOrVariable(token)) {
                currentResult = valuesStorage.getValue(token);
            } else if (regexCheck.isOperatorNoParentheses(token)) {
                try {
                    BigInteger secondOperand = stack.pollLast();
                    BigInteger firstOperand = stack.pollLast();
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

    private BigInteger performOperation(BigInteger op1, BigInteger op2, String operator) {
        BigInteger result = null;
        try {
            switch (operator) {
                case "+":
                    result = op1.add(op2);
                    break;
                case "-":
                    result = op1.subtract(op2);
                    break;
                case "*":
                    result = op1.multiply(op2);
                    break;
                case "/":
                    result = op1.divide(op2);
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

