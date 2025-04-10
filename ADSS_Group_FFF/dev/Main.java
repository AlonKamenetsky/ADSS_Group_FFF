import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import Domain.*;
import Presentation.HRInterface;
import Presentation.LoginScreen;

public class Main {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);

        RolesRepo rolesRepo = RolesRepo.getInstance();

        RoleDL hrRole = rolesRepo.getRoleByName("HR");
        RoleDL cashierRole = rolesRepo.getRoleByName("Cashier");
        RoleDL warehouseRole = rolesRepo.getRoleByName("Warehouse");

        List<RoleDL> rolesDana = Arrays.asList(hrRole, cashierRole);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date employmentDate = dateFormat.parse("01-01-2020");

        EmployeeDL dana = new EmployeeDL(
                "1",
                rolesDana,
                "Dana",
                "123456789",
                "IL123BANK",
                5000f,
                employmentDate
        );

        List<UserDL> users = Arrays.asList(dana);

        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date shiftDate1 = null;
        Date shiftDate2 = null;
        try {
            shiftDate1 = dateFormat.parse("01-05-2025");
            shiftDate2 = dateFormat.parse("02-05-2025");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ShiftDL morningShift = new ShiftDL("S1", shiftDate1, ShiftDL.ShiftTime.Morning, Map.of(hrRole, Arrays.asList(dana)));
        ShiftDL eveningShift = new ShiftDL("S2", shiftDate2, ShiftDL.ShiftTime.Evening, Map.of(warehouseRole, Arrays.asList(dana)));

        List<ShiftDL> shifts = Arrays.asList(morningShift, eveningShift);

        // Start Login Process
        LoginScreen loginScreen = new LoginScreen(users);
        UserDL user = loginScreen.login(scanner);

        if (user != null) {
            if (user.getRoles().stream().anyMatch(r -> r.getName().equals("HR"))) {
                HRInterface hrInterface = new HRInterface(user.getId());
                hrInterface.assignEmployeeToShift(scanner, Arrays.asList(dana), shifts);
                System.out.println("Would you like to add a new role? (yes/no)");
                if (scanner.nextLine().equalsIgnoreCase("yes")) {
                    System.out.println("Enter new role name:");
                    String newRoleName = scanner.nextLine();
                    rolesRepo.addRole(new RoleDL(newRoleName));
                    System.out.println("New Role Added Successfully!");
                }
            } else {
                System.out.println("Logged in as Employee. No HR permissions.");
            }
        }
    }
}