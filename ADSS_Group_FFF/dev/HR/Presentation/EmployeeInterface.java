package HR.Presentation;

import HR.DTO.*;
import HR.Service.EmployeeService;
import HR.Service.ShiftService;
import HR.Service.SwapService;
import HR.Presentation.PresentationUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeInterface {
    private final String employeeId;
    private final EmployeeService employeeService = EmployeeService.getInstance();
    private final ShiftService shiftService       = ShiftService.getInstance();
    private final SwapService swapService         = SwapService.getInstance();

    public EmployeeInterface(String employeeId) {
        this.employeeId = employeeId;
    }

    public void employeeMainMenu(Scanner scanner) {
        boolean exit = false;
        while (!exit) {
            PresentationUtils.typewriterPrint("\nEmployee Menu:", 20);
            PresentationUtils.typewriterPrint("1. View My Info", 20);
            PresentationUtils.typewriterPrint("2. View My Shifts", 20);
            PresentationUtils.typewriterPrint("3. View Current Shift", 20);
            PresentationUtils.typewriterPrint("4. Send Weekly Availability (next week)", 20);
            PresentationUtils.typewriterPrint("5. View Weekly Availability (this week)", 20);
            PresentationUtils.typewriterPrint("6. View Weekly Availability (next week)", 20);
            PresentationUtils.typewriterPrint("7. Send Swap Request", 20);
            PresentationUtils.typewriterPrint("8. Cancel Swap Request", 20);
            PresentationUtils.typewriterPrint("9. Add Vacation", 20);
            PresentationUtils.typewriterPrint("10. View Vacation Dates", 20);
            PresentationUtils.typewriterPrint("11. Exit", 20);

            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> viewMyInfo();
                case 2 -> viewAssignedShifts();
                case 3 -> viewCurrentShift();
                case 4 -> sendWeeklyAvailability(scanner);
                case 5 -> viewWeeklyAvailability();
                case 6 -> viewNextWeeklyAvailability();
                case 7 -> sendSwapRequest(scanner);
                case 8 -> cancelSwapRequest(scanner);
                case 9 -> addVacation(scanner);
                case 10 -> viewHolidays();
                case 11 -> exit = true;
                default -> PresentationUtils.typewriterPrint("Invalid choice.", 20);
            }
        }
    }

    public void viewMyInfo() {
        EmployeeDTO dto = employeeService.getEmployeeById(employeeId);
        if (dto == null) {
            PresentationUtils.typewriterPrint("Employee not found.", 20);
        } else {
            PresentationUtils.printEmployeeDTO(dto);
        }
    }

    public void viewAssignedShifts() {
        List<ShiftDTO> shifts = shiftService.getAssignedShifts(employeeId);
        if (shifts.isEmpty()) {
            PresentationUtils.typewriterPrint("You are not assigned to any shifts this week.", 20);
            return;
        }
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        for (ShiftDTO s : shifts) {
            String roleForMe = s.getAssignedEmployees().stream()
                    .filter(sa -> sa.getEmployeeId().equals(employeeId))
                    .map(sa -> sa.getRoleName())
                    .findFirst()
                    .orElse("—");
            PresentationUtils.typewriterPrint(
                    String.format("• %s on %s (%s)",
                            s.getId(),
                            fmt.format(s.getDate()),
                            roleForMe
                    ), 20
            );
        }
    }

    private void viewCurrentShift() {
        Optional<ShiftDTO> maybeShift = shiftService.getCurrentShift();
        if (maybeShift.isEmpty()) {
            PresentationUtils.typewriterPrint("There is no shift currently active.", 20);
        } else {
            PresentationUtils.printShiftDTO(maybeShift.get());
        }
    }

    public void sendWeeklyAvailability(Scanner scanner) {
        List<ShiftDTO> nextWeekShifts = shiftService.getNextWeekShifts();

        List<WeeklyAvailabilityDTO> slots = nextWeekShifts.stream()
                .map(s -> new WeeklyAvailabilityDTO(
                        s.getDate().toInstant().atZone(ZoneId.systemDefault()).getDayOfWeek(),
                        s.getType()
                ))
                .distinct()
                .sorted(Comparator
                        .comparing(WeeklyAvailabilityDTO::getDay)
                        .thenComparing(WeeklyAvailabilityDTO::getTime))
                .collect(Collectors.toList());

        if (slots.isEmpty()) {
            PresentationUtils.typewriterPrint("No shifts scheduled for next week.", 20);
            return;
        }

        List<WeeklyAvailabilityDTO> currentNext =
                employeeService.getEmployeeAvailabilityNextWeek(employeeId);
        Set<WeeklyAvailabilityDTO> selected = new HashSet<>(currentNext);

        while (true) {
            PresentationUtils.typewriterPrint(
                    "\nToggle availability for next-week slots (0 to finish):", 20);
            for (int i = 0; i < slots.size(); i++) {
                WeeklyAvailabilityDTO w = slots.get(i);
                boolean isSelected = selected.contains(w);
                System.out.printf("%2d) [%s] %s %s%n",
                        i + 1,
                        isSelected ? "X" : " ",
                        w.getDay(),
                        w.getTime()
                );
            }
            PresentationUtils.typewriterPrint("", 20);
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 0) {
                PresentationUtils.typewriterPrint("Finished updating availability.", 20);
                break;
            }
            if (choice < 1 || choice > slots.size()) {
                PresentationUtils.typewriterPrint("Invalid choice, try again.", 20);
                continue;
            }
            WeeklyAvailabilityDTO picked = slots.get(choice - 1);
            if (selected.contains(picked)) {
                selected.remove(picked);
                PresentationUtils.typewriterPrint(
                        "Removed availability: " + picked.getDay() + " " + picked.getTime(), 20);
            } else {
                selected.add(picked);
                PresentationUtils.typewriterPrint(
                        "Marked available: " + picked.getDay() + " " + picked.getTime(), 20);
            }
        }

        employeeService.updateEmployeeAvailabilityNextWeek(
                employeeId,
                new ArrayList<>(selected)
        );
    }

    public void viewWeeklyAvailability() {
        PresentationUtils.typewriterPrint("\nYour Weekly Availability (this week):", 20);
        List<WeeklyAvailabilityDTO> availThis =
                employeeService.getEmployeeAvailabilityThisWeek(employeeId);

        if (availThis.isEmpty()) {
            PresentationUtils.typewriterPrint("No availability set for this week.", 20);
        } else {
            for (WeeklyAvailabilityDTO w : availThis) {
                PresentationUtils.typewriterPrint(
                        "• " + w.getDay() + " " + w.getTime(), 20);
            }
        }
    }

    public void viewNextWeeklyAvailability() {
        PresentationUtils.typewriterPrint("\nYour Weekly Availability (next week):", 20);
        List<WeeklyAvailabilityDTO> availNext =
                employeeService.getEmployeeAvailabilityNextWeek(employeeId);

        if (availNext.isEmpty()) {
            PresentationUtils.typewriterPrint("No availability set for next week.", 20);
        } else {
            for (WeeklyAvailabilityDTO w : availNext) {
                PresentationUtils.typewriterPrint(
                        "• " + w.getDay() + " " + w.getTime(), 20);
            }
        }
    }

    public void sendSwapRequest(Scanner scanner) {
        List<ShiftDTO> assigned = shiftService.getAssignedShifts(employeeId);

        if (assigned.isEmpty()) {
            System.out.println("No assigned shifts; cannot request a swap.");
            return;
        }

        System.out.println("\nYour Assigned Shifts:");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < assigned.size(); i++) {
            ShiftDTO s = assigned.get(i);
            String roleName = s.getAssignedEmployees().stream()
                    .filter(sa -> sa.getEmployeeId().equals(employeeId))
                    .map(sa -> sa.getRoleName())
                    .findFirst()
                    .orElse("—");
            System.out.printf("%d) %s on %s (%s)%n",
                    i + 1, s.getId(), fmt.format(s.getDate()), roleName);
        }
        System.out.print("Select shift to swap: ");
        int idx = scanner.nextInt() - 1;
        scanner.nextLine();
        if (idx < 0 || idx >= assigned.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        ShiftDTO target = assigned.get(idx);
        String myRole = target.getAssignedEmployees().stream()
                .filter(sa -> sa.getEmployeeId().equals(employeeId))
                .map(ShiftAssignmentDTO::getRoleName)
                .findFirst().orElseThrow();

        swapService.sendSwapRequest(employeeId, target.getId(), myRole);
    }

    public void cancelSwapRequest(Scanner scanner) {
        List<SwapRequestDTO> requests = swapService.getSwapRequests().stream()
                .filter(r -> r.getEmployeeId().equals(employeeId))
                .collect(Collectors.toList());

        if (requests.isEmpty()) {
            PresentationUtils.typewriterPrint("No swap requests available to cancel.", 20);
            return;
        }

        PresentationUtils.typewriterPrint("Select a swap request to cancel:", 20);
        for (int i = 0; i < requests.size(); i++) {
            SwapRequestDTO req = requests.get(i);
            String line = String.format(
                    "%d. Shift on %s [%s] – Role: %s",
                    i + 1,
                    req.getShiftId(),
                    req.getRoleName()
            );
            PresentationUtils.typewriterPrint(line, 20);
        }

        PresentationUtils.typewriterPrint("Enter the number of the request to cancel: ", 20);
        String input = scanner.nextLine().trim();
        try {
            int choice = Integer.parseInt(input);
            if (choice < 1 || choice > requests.size()) {
                PresentationUtils.typewriterPrint("Invalid selection.", 20);
                return;
            }
            SwapRequestDTO toCancel = requests.get(choice - 1);
            swapService.cancelSwapRequest(toCancel.getId());
        } catch (NumberFormatException e) {
            PresentationUtils.typewriterPrint("Invalid input. Please enter a number.", 20);
        }
    }

    public void addVacation(Scanner scanner) {
        List<Date> holidays = employeeService.getEmployeeHolidays(employeeId);
        if (holidays.size() >= 5) {
            PresentationUtils.typewriterPrint("Max 5 vacations reached.", 20);
            return;
        }

        PresentationUtils.typewriterPrint("\nEnter vacation date (yyyy-MM-dd): ", 20);
        String str = scanner.nextLine();
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(str);
            employeeService.addVacation(employeeId, d);
        } catch (ParseException e) {
            PresentationUtils.typewriterPrint("Bad date format. Please use yyyy-MM-dd.", 20);
        }
    }

    public void viewHolidays() {
        List<Date> hols = employeeService.getEmployeeHolidays(employeeId);
        if (hols.isEmpty()) {
            PresentationUtils.typewriterPrint("No vacations scheduled.", 20);
        } else {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            for (Date d : hols) {
                PresentationUtils.typewriterPrint("• " + fmt.format(d), 20);
            }
        }
    }
}
