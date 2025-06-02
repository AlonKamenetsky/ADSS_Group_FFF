//import java.text.ParseException;
//import java.util.Scanner;
//
//import HR.Presentation.DemoDataLoader;
//import HR.Presentation.LoginScreen;
//import HR.Presentation.PresentationUtils;
//
//public class Main {
//    public static void main(String[] args) throws ParseException {
//        // 1) Bootstrap example data unconditionally (or via a config flag)
//        int demoData = 0;
//        try {
//            Scanner scanner = new Scanner(System.in);
//            PresentationUtils.typewriterPrint("Would You like to Load Demo Data? (yes/no) ", 20);
//            String loadDemoData = scanner.nextLine().trim();
//            while (!loadDemoData.equalsIgnoreCase("yes") && !loadDemoData.equalsIgnoreCase("no")) {
//                PresentationUtils.typewriterPrint("Invalid choice. Please enter 'yes' or 'no': ", 20);
//                loadDemoData = scanner.nextLine().trim();
//            }
//            if (loadDemoData.equalsIgnoreCase("no")) {
//                PresentationUtils.typewriterPrint("Initializing new system..", 20);
//                DemoDataLoader.initializeExampleData(0);
//            }
//            else {
//                demoData=1;
//                PresentationUtils.typewriterPrint("Initializing demo data..", 20);
//                DemoDataLoader.initializeExampleData(1);
//            }
//        } catch (ParseException e) {
//            System.err.println("Error loading example data: " + e.getMessage());
//            e.printStackTrace();
//            System.exit(1);
//        }
//
//        // 2) Launch the login/UI loop
//        Scanner scanner = new Scanner(System.in);
//        LoginScreen login = new LoginScreen(
//                EmployeesRepo.getInstance().getEmployees());
//        login.run(scanner);
//
//        // 3) Clean up and exit
//        scanner.close();
//        System.out.println("Goodbye!");
//    }
//}
