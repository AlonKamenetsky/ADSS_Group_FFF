import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Scanner;

import HR.Presentation.LoginScreen;
import HR.Presentation.PresentationUtils;
import HR.Service.UserService;
import HR.DataAccess.EmployeeDAO;
import HR.DataAccess.EmployeeDAOImpl;
import Util.DatabaseInitializer;

public class Main {
    public static void main(String[] args) throws ParseException {
        DatabaseInitializer dbInit = new DatabaseInitializer();

        try {
            Scanner scanner = new Scanner(System.in);
            PresentationUtils.typewriterPrint("Would you like to load demo data? (yes/no) ", 20);
            String input = scanner.nextLine().trim();
            while (!input.equalsIgnoreCase("yes") && !input.equalsIgnoreCase("no")) {
                PresentationUtils.typewriterPrint("Invalid choice. Please enter 'yes' or 'no': ", 20);
                input = scanner.nextLine().trim();
            }

            if (input.equalsIgnoreCase("yes")) {
                // loading Transportation data
                dbInit.loadTransportationFakeData();
                dbInit.loadItems();
                dbInit.InitializeFullHRData();
                PresentationUtils.typewriterPrint("Initializing demo data..", 20);
            } else {
                // loading just items for create a task
                dbInit.loadItems();
                dbInit.InitializePartHRData();
                PresentationUtils.typewriterPrint("Initializing new system..", 20);
            }

            // --- REFRACTOR: Replace singleton UserService.getInstance() ---
            // Obtain the same Connection that DatabaseInitializer used to set up schemas/data
            Connection conn = dbInit.getConnection();
            // Construct the DAO(s) needed by UserService
            EmployeeDAO employeeDAO = new EmployeeDAOImpl(conn);
            // Now inject the DAO into UserService
            UserService userService = new UserService(employeeDAO);

            // Launch login UI
            LoginScreen loginScreen = new LoginScreen(userService);
            loginScreen.run(scanner);

            scanner.close();
            System.out.println("Goodbye!");
        }
        catch (ParseException e) {
            System.err.println("Error loading example data: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
