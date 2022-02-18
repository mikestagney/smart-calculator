package calculator;

import java.util.Scanner;

public class Menu {
    Scanner scanner = new Scanner(System.in);
    String userInput;
    Calculator calculator = new Calculator();

    public void startRunning() {
        while(true) {
            userInput = scanner.nextLine();
            switch (userInput) {
                case "/exit":
                    System.out.println("Bye!");
                    System.exit(0);
                    break;
                case "/help":
                    System.out.println("The program calculates the addition, subtraction, multiplication and division of numbers.");
                    System.out.println("Parenthesis are supported.");
                    break;
                case "":
                    break;
                default:
                    calculator.validateInput(userInput);
            }
        }
    }
}
