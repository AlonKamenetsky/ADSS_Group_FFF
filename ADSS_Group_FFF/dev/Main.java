import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import Domain.*;
import Presentation.EmployeeInterface;
import Presentation.HRInterface;
import Presentation.LoginScreen;

public class Main {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);

        RolesRepo rolesRepo = RolesRepo.getInstance();
        RoleDL hrRole = rolesRepo.getRoleByName("HR");
        RoleDL managerRole = rolesRepo.getRoleByName("Shift Manager");
        RoleDL cashierRole = rolesRepo.getRoleByName("Cashier");
        RoleDL warehouseRole = rolesRepo.getRoleByName("Warehouse");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date employmentDate = dateFormat.parse("01-01-2020");

        EmployeeDL dana = new EmployeeDL("1", new LinkedList<>(Arrays.asList(hrRole, cashierRole)), "Dana", "123", "IL123BANK", 5000f, employmentDate);
        dana.setPassword("123");

        EmployeeDL john = new EmployeeDL("2", new LinkedList<>(Arrays.asList(warehouseRole)), "John", "456", "IL456BANK", 4500f, employmentDate);
        john.setPassword("456");

        List<EmployeeDL> employees = Arrays.asList(dana, john);

        // Create Shifts
        Date shiftDate1 = dateFormat.parse("01-05-2025");
        Date shiftDate2 = dateFormat.parse("02-05-2025");

        ShiftDL morningShift = new ShiftDL("S1", shiftDate1, ShiftDL.ShiftTime.Morning, Map.of(cashierRole, new ArrayList<>()));
        ShiftDL eveningShift = new ShiftDL("S2", shiftDate2, ShiftDL.ShiftTime.Evening, Map.of(warehouseRole, new ArrayList<>()));

        List<ShiftDL> shifts = Arrays.asList(morningShift, eveningShift);

        boolean exitSystem = false;
        while (!exitSystem) {
            System.out.println("Welcome! Enter your ID:");
            String inputId = scanner.nextLine();

            System.out.println("Enter your Password:");
            String inputPassword = scanner.nextLine();

            EmployeeDL loggedInUser = null;
            for (EmployeeDL e : employees) {
                if (e.getId().equals(inputId) && e.getPassword() != null && e.getPassword().equals(inputPassword)) {
                    loggedInUser = e;
                    break;
                }
            }

            if (loggedInUser == null) {
                System.out.println("Invalid ID or Password. Try again.");
                continue;
            }

            System.out.println("Select Role to Login:");
            for (int i = 0; i < loggedInUser.getRoles().size(); i++) {
                System.out.println(i + 1 + ". " + loggedInUser.getRoles().get(i).getName());
            }

            int roleIndex = scanner.nextInt() - 1;
            scanner.nextLine();

            RoleDL selectedRole = loggedInUser.getRoles().get(roleIndex);

            if (selectedRole.getName().equals("HR") || selectedRole.getName().equals("Shift Manager")) {
                HRInterface hrInterface = new HRInterface(loggedInUser.getId());
                hrInterface.setCurrentUserRole(selectedRole);
                hrInterface.managerMainMenu(scanner, employees, shifts);
            } else {
                EmployeeInterface employeeInterface = new EmployeeInterface(loggedInUser);
                employeeInterface.employeeMainMenu(scanner, shifts);
            }

            System.out.println("Would you like to switch user? (yes/no)");
            String choice = scanner.nextLine();
            if (!choice.equalsIgnoreCase("yes")) {
                exitSystem = true;
            }
        }

        System.out.println("Exiting system. Goodbye!");
    }
}