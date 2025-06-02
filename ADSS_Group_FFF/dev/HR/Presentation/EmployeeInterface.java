//package HR.Presentation;
//
//import HR.Domain.*;
//import HR.Service.EmployeeService;
//import HR.Service.ShiftService;
//import HR.Service.SwapService;
//
//import java.text.ParseException;
//import java.time.DayOfWeek;
//import java.time.ZoneId;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class EmployeeInterface {
//    private final Employee employee;
//    // only this week’s shifts
//    private final SwapService swapService = SwapService.getInstance();
//    private final EmployeeService employeeService = EmployeeService.getInstance();
//    private final ShiftService shiftService = ShiftService.getInstance();
//
//
//    public EmployeeInterface(Employee employee) {
//        this.employee = employee;
//
//    }
//
//    public void employeeMainMenu(Scanner scanner) {
//        boolean exit = false;
//        while (!exit) {
//            PresentationUtils.typewriterPrint("\nEmployee Menu:", 20
//);
//            PresentationUtils.typewriterPrint("1. View My Info", 20
//);
//            PresentationUtils.typewriterPrint("2. View My Shifts", 20
//);
//            PresentationUtils.typewriterPrint("3. View Current Shift", 20
//);
//            PresentationUtils.typewriterPrint("4. Send Weekly Availability (next week)", 20
//);
//            PresentationUtils.typewriterPrint("5. View Weekly Availability (this week)", 20
//);
//            PresentationUtils.typewriterPrint("6. View Weekly Availability (next week)", 20
//);
//            PresentationUtils.typewriterPrint("7. Send Swap Request", 20
//);
//            PresentationUtils.typewriterPrint("8. Cancel Swap Request", 20
//);
//            PresentationUtils.typewriterPrint("9. Add Vacation", 20
//);
//            PresentationUtils.typewriterPrint("10. View Vacation Dates", 20
//);
//            PresentationUtils.typewriterPrint("11. Exit", 20
//);
//            PresentationUtils.typewriterPrint("", 20
//);
//
//            int choice = scanner.nextInt();
//            scanner.nextLine();
//            switch (choice) {
//                case 1 -> employeeService.ShowInfo(employee);
//                case 2 -> viewAssignedShifts();
//                case 3 -> viewCurrentShift();
//                case 4 -> sendWeeklyAvailability(scanner);
//                case 5 -> viewWeeklyAvailability();
//                case 6 -> viewNextWeeklyAvailability();
//                case 7 -> sendSwapRequest(scanner);
//                case 8 -> cancelSwapRequest(scanner);
//                case 9 -> addVacation(scanner);
//                case 10 -> viewHolidays();
//                case 11 -> exit = true;
//                default -> PresentationUtils.typewriterPrint("Invalid choice.", 20
//);
//            }
//        }
//    }
//
//    private void cancelSwapRequest(Scanner scanner) {
//        // 1. fetch only this employee’s swap-requests
//        List<SwapRequest> requests = swapService.getSwapRequests().stream()
//                .filter(s -> s.getEmployee().getId().equals(employee.getId()))
//                .collect(Collectors.toList());
//
//        if (requests.isEmpty()) {
//            PresentationUtils.typewriterPrint("No swap requests available to cancel.", 20);
//            return;
//        }
//
//        // 2. list them out
//        PresentationUtils.typewriterPrint("Select a swap request to cancel:", 20);
//        for (int i = 0; i < requests.size(); i++) {
//            SwapRequest req = requests.get(i);
//            String line = String.format(
//                    "%d. Shift on %s [%s] – Role: %s",
//                    i + 1,
//                    req.getShift().getDate(),
//                    req.getShift().getType(),
//                    req.getRole().getName()
//            );
//            PresentationUtils.typewriterPrint(line, 20);
//        }
//
//        // 3. prompt for choice
//        PresentationUtils.typewriterPrint("Enter the number of the request to cancel: ", 20);
//        String input = scanner.nextLine().trim();
//
//        try {
//            int choice = Integer.parseInt(input);
//            if (choice < 1 || choice > requests.size()) {
//                PresentationUtils.typewriterPrint("Invalid selection.", 20);
//                return;
//            }
//
//            // 4. cancel the selected request
//            SwapRequest toCancel = requests.get(choice - 1);
//            swapService.CancelSwapRequest(toCancel);
//        } catch (NumberFormatException e) {
//            PresentationUtils.typewriterPrint("Invalid input. Please enter a number.", 20);
//        }
//    }
//
//    private void viewAssignedShifts() {
//        shiftService.GetAssignedShifts(employee);
//    }
//
//    private void sendSwapRequest(Scanner scanner) {
//        // Always fetch up-to-date current-week shifts
//        List<Shift> assigned = shiftService.getAssignedShifts(employee);
//
//        if (assigned.isEmpty()) {
//            System.out.println("No assigned shifts; cannot request a swap.");
//            return;
//        }
//
//        System.out.println("\nYour Assigned Shifts:");
//        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
//        for (int i = 0; i < assigned.size(); i++) {
//            Shift s = assigned.get(i);
//            String role = s.getAssignedEmployees().stream()
//                    .filter(sa -> sa.getEmployeeId().equals(employee.getId()))
//                    .findFirst()
//                    .map(sa -> sa.getRole().getName())
//                    .orElse("—");
//            System.out.printf("%d) %s on %s (%s)%n",
//                    i + 1, s.getID(), fmt.format(s.getDate()), role);
//        }
//        System.out.print("Select shift to swap: ");
//        int idx = scanner.nextInt() - 1;
//        scanner.nextLine();
//        if (idx < 0 || idx >= assigned.size()) {
//            System.out.println("Invalid selection.");
//            return;
//        }
//
//        Shift target = assigned.get(idx);
//        Role myRole = shiftService.getMyRoleForShift(employee, target);
//        if (myRole == null) {
//            System.out.println("Could not determine your role for that shift.");
//            return;
//        }
//        swapService.SendSwapRequest(employee, target, myRole);
//    }
//
//    private void sendWeeklyAvailability(Scanner scanner) {
//            // Gather all distinct day‑of‑week + time slots from next week’s shifts
//            List<WeeklyAvailability> slots = shiftService.getNextWeekShifts().stream()
//                    .map(s -> {
//                        DayOfWeek dow = s.getDate().toInstant()
//                                .atZone(ZoneId.systemDefault())
//                                .getDayOfWeek();
//                        return new WeeklyAvailability(dow, s.getType());
//                    })
//                    .distinct()
//                    .sorted(Comparator
//                            .comparing(WeeklyAvailability::getDay)
//                            .thenComparing(WeeklyAvailability::getTime))
//                    .collect(Collectors.toList());
//
//            if (slots.isEmpty()) {
//                PresentationUtils.typewriterPrint("No shifts scheduled for next week.", 20);
//                return;
//            }
//
//            Set<WeeklyAvailability> selectedAvailabilities = new HashSet<>(employee.getAvailabilityNextWeek());
//
//            while (true) {
//                PresentationUtils.typewriterPrint("\nToggle availability for next‑week slots (0 to finish):", 20);
//                for (int i = 0; i < slots.size(); i++) {
//                    WeeklyAvailability w = slots.get(i);
//                    boolean selected = selectedAvailabilities.contains(w);
//                    System.out.printf("%2d) [%s] %s %s%n",
//                            i + 1,
//                            selected ? "X" : " ",
//                            w.getDay(),
//                            w.getTime()
//                    );
//                }
//                PresentationUtils.typewriterPrint("", 20);
//                int choice = scanner.nextInt();
//                scanner.nextLine();
//
//                if (choice == 0) {
//                    PresentationUtils.typewriterPrint("Finished updating availability.", 20);
//                    break;
//                }
//                if (choice < 1 || choice > slots.size()) {
//                    PresentationUtils.typewriterPrint("Invalid choice, try again.", 20);
//                    continue;
//                }
//
//                WeeklyAvailability picked = slots.get(choice - 1);
//                if (selectedAvailabilities.contains(picked)) {
//                    selectedAvailabilities.remove(picked);
//                    PresentationUtils.typewriterPrint("Removed availability: " + picked.getDay() + " " + picked.getTime(), 20);
//                } else {
//                    selectedAvailabilities.add(picked);
//                    PresentationUtils.typewriterPrint("Marked available: " + picked.getDay() + " " + picked.getTime(), 20);
//                }
//            }
//
//            // Delegate the actual update to the service
//            employeeService.updateWeeklyAvailability(employee, selectedAvailabilities);
//        }
//
//
//    private void viewWeeklyAvailability() {
//        PresentationUtils.typewriterPrint("\nYour Weekly Availability (this week):", 20
//);
//        List<WeeklyAvailability> currentWeekAvailability = employeeService.getAvailabilityThisWeek(employee);
//
//        for (WeeklyAvailability w : currentWeekAvailability) {
//            PresentationUtils.typewriterPrint("• " + w.getDay() + " " + w.getTime(), 20
//);
//        }
//        if (currentWeekAvailability.isEmpty()) {
//            PresentationUtils.typewriterPrint("No availability set for this week.", 20
//);
//        }
//    }    private void viewNextWeeklyAvailability() {
//        PresentationUtils.typewriterPrint("\nYour Weekly Availability (next week):", 20
//);
//        List<WeeklyAvailability> nextWeekAvailability = employeeService.getAvailabilityNextWeek(employee);
//        for (WeeklyAvailability w : nextWeekAvailability) {
//            PresentationUtils.typewriterPrint("• " + w.getDay() + " " + w.getTime(), 20
//);
//        }
//        if (nextWeekAvailability.isEmpty()) {
//            PresentationUtils.typewriterPrint("No availability set for next week.", 20
//);
//        }
//    }
//
//    private void addVacation(Scanner scanner) {
//        if (employee.getHolidays().size() >= 5) {
//            PresentationUtils.typewriterPrint("Max 5 vacations reached.", 20
//);
//            return;
//        }
//        PresentationUtils.typewriterPrint("\nEnter vacation date (yyyy-MM-dd): ", 20
//);
//        String str = scanner.nextLine();
//        try {
//            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(str);
//            employeeService.AddVacation(employee,d);
//        } catch (ParseException e) {
//            PresentationUtils.typewriterPrint("Bad date format. Please use yyyy-MM-dd.", 20
//);
//        }
//    }
//
//
//    private void viewHolidays() {
//
//        employeeService.viewHolidays(employee);
//
//    }
//
//    private void viewCurrentShift() {
//        shiftService.getCurrentShift();
//    }
//}
