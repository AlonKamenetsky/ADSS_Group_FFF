package HR.Presentation;

import HR.DataAccess.WeeklyAvailabilityDAO;
import HR.Domain.*;
import HR.Service.SwapService;

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
            PresentationUtils.typewriterPrint("\nEmployee Menu:", 20
);
            PresentationUtils.typewriterPrint("1. View My Info", 20
);
            PresentationUtils.typewriterPrint("2. View My Shifts", 20
);
            PresentationUtils.typewriterPrint("3. View Current Shift", 20
);
            PresentationUtils.typewriterPrint("4. Send Weekly Availability (next week)", 20
);
            PresentationUtils.typewriterPrint("5. View Weekly Availability (this week)", 20
);
            PresentationUtils.typewriterPrint("6. View Weekly Availability (next week)", 20
);
            PresentationUtils.typewriterPrint("7. Send Swap Request", 20
);
            PresentationUtils.typewriterPrint("8. Add Vacation", 20
);
            PresentationUtils.typewriterPrint("9. View Vacation Dates", 20
);
            PresentationUtils.typewriterPrint("10. Exit", 20
);
            PresentationUtils.typewriterPrint("", 20
);

            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> employee.ShowInfo();
                case 2 -> viewAssignedShifts();
                case 3 -> viewCurrentShift();
                case 4 -> sendWeeklyAvailability(scanner);
                case 5 -> viewWeeklyAvailability();
                case 6 -> viewNextWeeklyAvailability();
                case 7 -> sendSwapRequest(scanner);
                case 8 -> addVacation(scanner);
                case 9 -> viewHolidays();
                case 10 -> exit = true;
                default -> PresentationUtils.typewriterPrint("Invalid choice.", 20
);
            }
        }
    }


    private void viewAssignedShifts() {
        List<Shift> shifts = WeeklyAvailabilityDAO.ShiftsRepo.getInstance().getCurrentWeekShifts();
        boolean found = false;
        System.out.println("\nYour Assigned Shifts:");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
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
        if (!found) {
            System.out.println("You are not assigned to any shifts.");
        }
    }

    private void sendSwapRequest(Scanner scanner) {
        // Always fetch up-to-date current-week shifts
        List<Shift> shifts = WeeklyAvailabilityDAO.ShiftsRepo.getInstance().getCurrentWeekShifts();
        List<Shift> assigned = shifts.stream()
                .filter(s -> s.getAssignedEmployees().stream()
                        .anyMatch(sa -> sa.getEmployeeId().equals(employee.getId())))
                .collect(Collectors.toList());

        if (assigned.isEmpty()) {
            System.out.println("No assigned shifts; cannot request a swap.");
            return;
        }

        System.out.println("\nYour Assigned Shifts:");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < assigned.size(); i++) {
            Shift s = assigned.get(i);
            String role = s.getAssignedEmployees().stream()
                    .filter(sa -> sa.getEmployeeId().equals(employee.getId()))
                    .findFirst()
                    .map(sa -> sa.getRole().getName())
                    .orElse("—");
            System.out.printf("%d) %s on %s (%s)%n",
                    i + 1, s.getID(), fmt.format(s.getDate()), role);
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
            System.out.println("Could not determine your role for that shift.");
            return;
        }

        SwapRequest req = new SwapRequest(employee, target, myRole);
        SwapRequestsRepo.getInstance().addSwapRequest(req);
        System.out.println("Swap request sent: " + req);
    }

    private void sendWeeklyAvailability(Scanner scanner) {
        // Gather all distinct day‑of‑week + time slots from next week’s shifts
        List<WeeklyAvailability> slots = WeeklyAvailabilityDAO.ShiftsRepo.getInstance()
                .getNextWeekShifts().stream()
                .map(s -> {
                    DayOfWeek dow = s.getDate().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .getDayOfWeek();
                    return new WeeklyAvailability(dow, s.getType());
                })
                .distinct()
                .sorted(Comparator
                        .comparing(WeeklyAvailability::getDay)
                        .thenComparing(WeeklyAvailability::getTime))
                .collect(Collectors.toList());

        if (slots.isEmpty()) {
            PresentationUtils.typewriterPrint("No shifts scheduled for next week.", 20
);
            return;
        }

        while (true) {
            PresentationUtils.typewriterPrint("\nToggle availability for next‑week slots (0 to finish):", 20
);
            for (int i = 0; i < slots.size(); i++) {
                WeeklyAvailability w = slots.get(i);
                boolean selected = employee.getAvailabilityNextWeek().contains(w);
                System.out.printf("%2d) [%s] %s %s%n",
                        i + 1,
                        selected ? "X" : " ",
                        w.getDay(),
                        w.getTime()
                );
            }
            PresentationUtils.typewriterPrint("", 20
);
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 0) {
                PresentationUtils.typewriterPrint("Finished updating availability.", 20
);
                break;
            }
            if (choice < 1 || choice > slots.size()) {
                PresentationUtils.typewriterPrint("Invalid choice, try again.", 20
);
                continue;
            }

            WeeklyAvailability picked = slots.get(choice - 1);
            if (employee.getAvailabilityNextWeek().contains(picked)) {
                // remove
                employee.removeAvailability(picked.getDay(), picked.getTime());
                PresentationUtils.typewriterPrint("Removed availability: " + picked.getDay() + " " + picked.getTime(), 20
);
            } else {
                // add
                employee.addAvailability(picked.getDay(), picked.getTime());
                PresentationUtils.typewriterPrint("Marked available: " + picked.getDay() + " " + picked.getTime(), 20
);
            }
        }
    }



    private void viewWeeklyAvailability() {
        PresentationUtils.typewriterPrint("\nYour Weekly Availability (this week):", 20
);
        for (WeeklyAvailability w : employee.getAvailabilityThisWeek()) {
            PresentationUtils.typewriterPrint("• " + w.getDay() + " " + w.getTime(), 20
);
        }
        if (employee.getAvailabilityThisWeek().isEmpty()) {
            PresentationUtils.typewriterPrint("No availability set for this week.", 20
);
        }
    }    private void viewNextWeeklyAvailability() {
        PresentationUtils.typewriterPrint("\nYour Weekly Availability (next week):", 20
);
        for (WeeklyAvailability w : employee.getAvailabilityNextWeek()) {
            PresentationUtils.typewriterPrint("• " + w.getDay() + " " + w.getTime(), 20
);
        }
        if (employee.getAvailabilityThisWeek().isEmpty()) {
            PresentationUtils.typewriterPrint("No availability set for next week.", 20
);
        }
    }

    private void addVacation(Scanner scanner) {
        if (employee.getHolidays().size() >= 5) {
            PresentationUtils.typewriterPrint("Max 5 vacations reached.", 20
);
            return;
        }
        PresentationUtils.typewriterPrint("\nEnter vacation date (yyyy-MM-dd): ", 20
);
        String str = scanner.nextLine();
        try {
            // use ordinary hyphens here:
            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(str);
            employee.addHoliday(d);
            PresentationUtils.typewriterPrint("Vacation added: " + str, 20
);
        } catch (ParseException e) {
            PresentationUtils.typewriterPrint("Bad date format. Please use yyyy-MM-dd.", 20
);
        }
    }


    private void viewHolidays() {
        PresentationUtils.typewriterPrint("\nYour Vacations:", 20
);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy‑MM‑dd");
        for (Date d : employee.getHolidays()) {
            PresentationUtils.typewriterPrint("• " + fmt.format(d), 20
);
        }
        if (employee.getHolidays().isEmpty()) {
            PresentationUtils.typewriterPrint("No vacations scheduled.", 20
);
        }
    }

    private void viewCurrentShift() {
        Optional<Shift> currentShift = WeeklyAvailabilityDAO.ShiftsRepo.getInstance().getCurrentShift();
        if (currentShift.isPresent()) {
            PresentationUtils.printShift(currentShift.get());
        } else {
            System.out.println("There is no shift currently active.");
        }
    }
}
