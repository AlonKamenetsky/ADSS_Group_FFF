package HR.Service;

import HR.DataAccess.ShiftsRepo;
import HR.DataAccess.WeeklyAvailabilityDAO;
import HR.Domain.*;
import HR.Presentation.PresentationUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ShiftService {

    private final RoleService roleService = RoleService.getInstance();
    private static ShiftService instance;
    private final ShiftsRepo repo;

    private ShiftService() {
        repo = ShiftsRepo.getInstance();
    }

    public static ShiftService getInstance() {
        if (instance == null) {
            instance = new ShiftService();
        }
        return instance;
    }


    public Shift getShiftById(String id) {
        return repo.getShiftByID(id);
    }


    public void AssignEmployeeToShift(Shift shift, Employee employee, Role role) {
        shift.assignEmployee(employee, role);
        System.out.println("Employee " + employee.getName() + " assigned to role " +
                role.getName() + " in shift " + shift.getID());
    }



    public Shift getShiftsForDate(Shift.ShiftTime shiftTime, Date date) {
        repo.ensureUpToDate();
        List<Shift> shifts = new ArrayList<>();
        shifts.addAll(repo.getCurrentWeekShifts());
        shifts.addAll(repo.getNextWeekShifts());
        for (Shift shift : shifts) {
            if (repo.isSameDay(shift.getDate(), date)&&shift.getType()==shiftTime) {
                return shift;
            }
        }
        return null;
    }


    public void GenerateWeeklyShifts() {
        repo.ensureUpToDate();
    }


    public List<Shift> getCurrentWeekShifts() {
        return repo.getCurrentWeekShifts();
    }

    public List<Shift> getNextWeekShifts() {
        return repo.getNextWeekShifts();
    }

    public String getShiftID(Shift shift) {
        return shift.getID();
    }
    public Date getDate(Shift shift) {
        return shift.getDate();
    }
    public String getDateString(Shift shift) {
        return shift.getDate().toString();
    }
    public Shift.ShiftTime getShiftType(Shift shift) {
        return shift.getType();
    }
    public Map<Role, ArrayList<Employee>> getRequiredRoles(Shift shift) {
        return shift.getRequiredRoles();
    }

    public Map<Role, Integer> getRequiredCounts(Shift shift) {
        return shift.getRequiredCounts();
    }

    public List<ShiftAssignment> getAssignedEmployees(Shift shift) {
        return shift.getAssignedEmployees();
    }

    public void ensureShiftsRepoUpToDate() {
        repo.ensureUpToDate();
    }

// In ShiftService.java

    public void ConfigureShiftRoles(Shift shift, Map<Role, Integer> requiredCounts) {
        Role managerRole = roleService.getRoleByName("Shift Manager");
        Role hrRole = roleService.getRoleByName("HR Manager");

        // Always set Shift Manager to 1
        shift.getRequiredCounts().put(managerRole, 1);
        shift.getRequiredRoles().put(managerRole, new ArrayList<>(1));

        for (Map.Entry<Role, Integer> entry : requiredCounts.entrySet()) {
            Role role = entry.getKey();
            if (role.equals(managerRole) || role.equals(hrRole)) continue;
            int cnt = entry.getValue();
            shift.getRequiredCounts().put(role, cnt);
            shift.getRequiredRoles().put(role, new ArrayList<>(cnt));
        }
        PresentationUtils.typewriterPrint("Shift roles updated successfully.", 20);
    }

    public void GetAssignedShifts(Employee employee) {
        List<Shift> shifts = repo.getCurrentWeekShifts();
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

    public List<Shift> getAssignedShifts(Employee employee) {
        return this.getCurrentWeekShifts().stream().filter(s -> this.getAssignedEmployees(s).stream().anyMatch(sa -> sa.getEmployeeId().equals(employee.getId()))).collect(Collectors.toList());
    }

    public Role getMyRoleForShift(Employee employee, Shift target) {
        return this.getAssignedEmployees(target).stream().filter(sa -> sa.getEmployeeId().equals(employee.getId())).findFirst().map(ShiftAssignment::getRole).orElse(null);
    }

    public void getCurrentShift() {
        Optional<Shift> currentShift = repo.getCurrentShift();
        if (currentShift.isPresent()) {
            PresentationUtils.printShift(currentShift.get());
        } else {
            PresentationUtils.typewriterPrint("There is no shift currently active.",20);
        }
    }

    public void addTemplate(ShiftTemplate shiftTemplate) {
        repo.addTemplate(shiftTemplate);
    }

    public List<ShiftTemplate> getTemplates() {
        return repo.getTemplates();
    }

    public WeeklyShiftsSchedule getSchedule() {
        return repo.getSchedule();
    }
}

