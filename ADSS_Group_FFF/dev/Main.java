import java.text.ParseException;
import java.util.Scanner;
import Domain.EmployeesRepo;
import Presentation.ConsoleUtils;
import Presentation.DataInitializer;
import Presentation.LoginScreen;

public class Main {
    public static void main(String[] args) throws ParseException {
        // 1) Bootstrap example data unconditionally (or via a config flag)
        try {
            DataInitializer.initializeExampleData();
        } catch (ParseException e) {
            System.err.println("Error loading example data: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        // 2) Launch the login/UI loop
        Scanner scanner = new Scanner(System.in);
        LoginScreen login = new LoginScreen(
                EmployeesRepo.getInstance().getEmployees()
        );
        login.run(scanner);

        // 3) Clean up and exit
        scanner.close();
        System.out.println("Goodbye!");
    }
}
