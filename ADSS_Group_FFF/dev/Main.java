import java.text.ParseException;
import java.util.Scanner;

import HR.Presentation.DemoDataLoader;
import HR.Presentation.LoginScreen;
import HR.Presentation.PresentationUtils;
import HR.Service.UserService;

public class Main {
    public static void main(String[] args) throws ParseException {
        int demoData = 0;
        try {
            Scanner scanner = new Scanner(System.in);
            PresentationUtils.typewriterPrint("Would you like to load demo data? (yes/no) ", 20);
            String input = scanner.nextLine().trim();
            while (!input.equalsIgnoreCase("yes") && !input.equalsIgnoreCase("no")) {
                PresentationUtils.typewriterPrint("Invalid choice. Please enter 'yes' or 'no': ", 20);
                input = scanner.nextLine().trim();
            }
            if (input.equalsIgnoreCase("yes")) {
                demoData = 1;
                PresentationUtils.typewriterPrint("Initializing demo data..", 20);
            } else {
                PresentationUtils.typewriterPrint("Initializing new system..", 20);
            }
            DemoDataLoader.initializeExampleData(demoData);

            // Launch login UI
            LoginScreen loginScreen = new LoginScreen(UserService.getInstance());
            loginScreen.run(scanner);

            scanner.close();
            System.out.println("Goodbye!");
        } catch (ParseException e) {
            System.err.println("Error loading example data: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
