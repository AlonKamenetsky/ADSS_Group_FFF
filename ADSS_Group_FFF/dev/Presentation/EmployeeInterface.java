package Presentation;

import Domain.*;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeInterface {
    private final Employee employee;
    // only this week’s shifts
    private final List<Shift> shifts = ShiftsRepo.getInstance().getCurrentWeekShifts();

    public EmployeeInterface(Employee employee) {
        this.employee = employee;
    }

    public void employeeMainMenu(Scanner scanner) {
        boolean exit = false;
        while (!exit) {
            System.out.println("\nEmployee Menu:");
            System.out.println("1. View My Shifts");
            System.out.println("2. Send Swap Request");
            System.out.println("3. Send Weekly Availability (next week)");
            System.out.println("4. View Weekly Availability (this week)");
            System.out.println("5. Add Vacation");
            System.out.println("6. View Vacation Dates");
            System.out.println("7. Exit");
            System.out.print("> ");

            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> viewAssignedShifts();
                case 2 -> sendSwapRequest(scanner);
                case 3 -> sendWeeklyAvailability(scanner);
                case 4 -> viewWeeklyAvailability();
                case 5 -> addVacation(scanner);
                case 6 -> viewHolidays();
                case 7 -> exit = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void viewAssignedShifts() {
        boolean found = false;
        System.out.println("\nYour Assigned Shifts:");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy‑MM‑dd");
        for (Shift s : shifts) {
            for (ShiftAssignment sa : s.getAssignedEmployees()) {
                if (sa.getEmployeeId().equals(employee.getId())) {
                    System.out.printf("• %s on %s (%s)%n",
                            s.getID(),
                            fmt.format(s.getDate()),
                            sa.getRole().getName());
                    found = true;
                }
            }
        }
        if (!found) System.out.println("You are not assigned to any shifts.");
    }

    private void sendSwapRequest(Scanner scanner) {
        var assigned = shifts.stream()
                .filter(s -> s.getAssignedEmployees().stream()
                        .anyMatch(sa -> sa.getEmployeeId().equals(employee.getId())))
                .collect(Collectors.toList());

        if (assigned.isEmpty()) {
            System.out.println("No assigned shifts; cannot request a swap.");
            return;
        }

        System.out.println("\nYour Assigned Shifts:");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy‑MM‑dd");
        for (int i = 0; i < assigned.size(); i++) {
            Shift s = assigned.get(i);
            String role = s.getAssignedEmployees().stream()
                    .filter(sa -> sa.getEmployeeId().equals(employee.getId()))
                    .findFirst()
                    .map(sa -> sa.getRole().getName())
                    .orElse("—");
            System.out.printf("%d) %s on %s (%s)%n", i+1, s.getID(), fmt.format(s.getDate()), role);
        }
        System.out.print("Select shift to swap: ");
        int idx = scanner.nextInt() - 1;
        scanner.nextLine();
        if (idx < 0 || idx >= assigned.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Shift target = assigned.get(idx);
        Role myRole = target.getAssignedEmployees().stream()
                .filter(sa -> sa.getEmployeeId().equals(employee.getId()))
                .findFirst()
                .map(ShiftAssignment::getRole)
                .orElse(null);
        if (myRole == null) {
            System.out.println("Could not determine your role on that shift.");
            return;
        }

        SwapRequest req = new SwapRequest(employee, target, myRole);
        SwapRequestsRepo.getInstance().addSwapRequest(req);
        System.out.println("Swap request sent: " + req);
    }

    private void sendWeeklyAvailability(Scanner scanner) {
        List<Shift> nextShifts = ShiftsRepo.getInstance().getNextWeekShifts();
        if (nextShifts.isEmpty()) {
            System.out.println("No shifts scheduled for next week.");
            return;
        }

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy‑MM‑dd");
        while (true) {
            // build the list of shifts not yet marked available
            List<Shift> toPick = new ArrayList<>();
            for (Shift s : nextShifts) {
                DayOfWeek dow = s.getDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .getDayOfWeek();
                WeeklyAvailability slot = new WeeklyAvailability(dow, s.getType());
                if (!employee.getAvailabilityNextWeek().contains(slot)) {
                    toPick.add(s);
                }
            }

            if (toPick.isEmpty()) {
                System.out.println("You’ve marked availability for every shift in next week.");
                return;
            }

            System.out.println("\nChoose a shift to mark available (0 to finish):");
            for (int i = 0; i < toPick.size(); i++) {
                Shift s = toPick.get(i);
                DayOfWeek dow = s.getDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .getDayOfWeek();
                System.out.printf(
                        "%d) %s — %s on %s (%s)%n",
                        i + 1,
                        s.getID(),
                        dow,                   // <-- day of week
                        fmt.format(s.getDate()),
                        s.getType()
                );
            }
            System.out.print("> ");

            int choice = scanner.nextInt();
            scanner.nextLine();
            if (choice == 0) {
                System.out.println("Finished marking availability.");
                return;
            }
            if (choice < 1 || choice > toPick.size()) {
                System.out.println("Invalid choice.");
                continue;
            }

            // record the selection
            Shift selected = toPick.get(choice - 1);
            DayOfWeek dow = selected.getDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .getDayOfWeek();
            employee.addAvailability(dow, selected.getType());
            System.out.printf(
                    "Marked available: %s — %s on %s (%s)%n",
                    selected.getID(),
                    dow,
                    fmt.format(selected.getDate()),
                    selected.getType()
            );
        }
    }


    private void viewWeeklyAvailability() {
        System.out.println("\nYour Weekly Availability (this week):");
        for (WeeklyAvailability w : employee.getAvailabilityThisWeek()) {
            System.out.println("• " + w.getDay() + " " + w.getTime());
        }
        if (employee.getAvailabilityThisWeek().isEmpty()) {
            System.out.println("No availability set for this week.");
        }
    }

    private void addVacation(Scanner scanner) {
        if (employee.getHolidays().size() >= 5) {
            System.out.println("Max 5 vacations reached.");
            return;
        }
        System.out.print("\nEnter vacation date (yyyy-MM-dd): ");
        String str = scanner.nextLine();
        try {
            // use ordinary hyphens here:
            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(str);
            employee.addHoliday(d);
            System.out.println("Vacation added: " + str);
        } catch (ParseException e) {
            System.out.println("Bad date format. Please use yyyy-MM-dd.");
        }
    }


    private void viewHolidays() {
        System.out.println("\nYour Vacations:");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy‑MM‑dd");
        for (Date d : employee.getHolidays()) {
            System.out.println("• " + fmt.format(d));
        }
        if (employee.getHolidays().isEmpty()) {
            System.out.println("No vacations scheduled.");
        }
    }
}
