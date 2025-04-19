package SuppliersModule.PresentationLayer;
import java.util.Scanner;

import SuppliersModule.ServiceLayer.ServiceController;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        CLI cli = new CLI(sc);

        while (true) {
            cli.printMenuOptions();
            System.out.println("Please select an option: ");
            int userInput = sc.nextInt();
            sc.nextLine();
            switch (userInput) {
                case 1:
                    cli.printProductOptions();
                    userInput = sc.nextInt();
                    cli.chooseProductsOption(userInput);
                    break;
                case 2:
                    cli.printSupplierOptions();
                    userInput = sc.nextInt();
                    cli.chooseSupplierOption(userInput);
                    break;
                case 3:
                    cli.printContractOptions();
                    userInput = sc.nextInt();
                    cli.chooseContractOption(userInput);
                    break;
                case 4:
                    cli.printOrderOptions();
                    userInput = sc.nextInt();
                    cli.chooseOrderOption(userInput);
                    break;
                case 5:
                    System.out.println("Bye Bye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid option, please choose again");
            }
        }
    }
}