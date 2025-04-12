import java.text.ParseException;
import java.util.*;

import Domain.*;
import Presentation.DataInitializer;
import Presentation.EmployeeInterface;
import Presentation.HRInterface;

public class Main {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Would you like to load example data? (yes/no)");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("yes")) {
            DataInitializer.initializeExampleData();
        }

        EmployeesRepo employeesRepo = EmployeesRepo.getInstance();
        boolean exitSystem = false;

        while (!exitSystem) {
            // LOGIN FLOW
            System.out.println("Welcome! Enter your ID:");
            String inputId = scanner.nextLine();

            System.out.println("Enter your Password:");
            String inputPassword = scanner.nextLine();

            Employee loggedInUser = employeesRepo.getEmployeeById(inputId);

            if (loggedInUser == null || loggedInUser.getPassword() == null ||
                    !loggedInUser.getPassword().equals(inputPassword)) {
                System.out.println("Invalid ID or Password. Try again.");
                continue;
            }

            System.out.println("Select Role to Login:");
            for (int i = 0; i < loggedInUser.getRoles().size(); i++) {
                System.out.println((i + 1) + ". " + loggedInUser.getRoles().get(i).getName());
            }

            int roleIndex = scanner.nextInt() - 1;
            scanner.nextLine();

            Role selectedRole = loggedInUser.getRoles().get(roleIndex);

            if (selectedRole.getName().equals("HR")) {
                HRInterface hrInterface = new HRInterface(loggedInUser.getId());
                hrInterface.setCurrentUserRole(selectedRole);
                hrInterface.managerMainMenu(scanner);
            } else {
                EmployeeInterface employeeInterface = new EmployeeInterface(loggedInUser);
                employeeInterface.employeeMainMenu(scanner);
            }

            System.out.println("Would you like to switch user? (yes/no)");
            String again = scanner.nextLine();

            if (!again.equalsIgnoreCase("yes")) {
                exitSystem = true;
            }
        }

        System.out.println("Exiting system. Goodbye!");
    }
}