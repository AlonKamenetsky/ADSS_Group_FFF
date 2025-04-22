import java.text.ParseException;
import java.util.Scanner;
import Domain.EmployeesRepo;
import Presentation.ConsoleUtils;
import Presentation.DataInitializer;
import Presentation.LoginScreen;

public class Main {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);

        // example‑data prompt
        ConsoleUtils.typewriterPrint("Load example data? (yes/no) ", 20);
        String c = scanner.nextLine().trim();
        while (!c.equalsIgnoreCase("yes") && !c.equalsIgnoreCase("no")) {
            ConsoleUtils.typewriterPrint("Please enter 'yes' or 'no': ", 20);
            c = scanner.nextLine().trim();
        }
        if (c.equalsIgnoreCase("yes")) {
            DataInitializer.initializeExampleData();
        }

        // hand off everything else to LoginScreen
        LoginScreen login = new LoginScreen(
                EmployeesRepo.getInstance().getEmployees()
        );
        login.run(scanner);

        scanner.close();
    }
}
