package HR.Service;

import HR.DataAccess.*;
import HR.Domain.*;
import HR.Presentation.PresentationUtils;
import Util.Database;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ShiftService {

    private final RoleService roleService = RoleService.getInstance();
    private static ShiftService instance;
    private final ShiftDAO shiftDAO;
    private final ShiftTemplateDAO templateDAO;

    private ShiftService() {
        Connection conn = Database.getConnection();
        this.shiftDAO = new ShiftDAOImpl(conn);
        this.templateDAO = new ShiftTemplateDAOImpl(conn);
    }

    public static ShiftService getInstance() {
        if (instance == null) {
            instance = new ShiftService();
        }
        return instance;
    }

    public Shift getShiftById(String id) {
        return shiftDAO.selectById(id);
    }

    public void AssignEmployeeToShift(Shift shift, Employee employee, Role role) {
        shift.assignEmployee(employee, role);
        shiftDAO.update(shift); // persist the assignment
        System.out.println("Employee " + employee.getName() + " assigned to role " +
                role.getName() + " in shift " + shift.getID());
    }

    public List<Shift> getShiftsForDate(Date date) {
        return shiftDAO.selectByDate(date);
    }

    public List<Shift> getCurrentWeekShifts() {
        return shiftDAO.getCurrentWeekShifts();
    }

    public List<Shift> getNextWeekShifts() {
        return shiftDAO.getNextWeekShifts();
    }

    public void ConfigureShiftRoles(Shift shift, Map<Role, Integer> requiredCounts) {
        Role managerRole = roleService.getRoleByName("Shift Manager");
        Role hrRole = roleService.getRoleByName("HR Manager");

        shift.getRequiredCounts().put(managerRole, 1);
        shift.getRequiredRoles().put(managerRole, new ArrayList<>(1));

        for (Map.Entry<Role, Integer> entry : requiredCounts.entrySet()) {
            Role role = entry.getKey();
            if (role.equals(managerRole) || role.equals(hrRole)) continue;
            int cnt = entry.getValue();
            shift.getRequiredCounts().put(role, cnt);
            shift.getRequiredRoles().put(role, new ArrayList<>(cnt));
        }
        shiftDAO.update(shift);
        PresentationUtils.typewriterPrint("Shift roles updated successfully.", 20);
    }

    public void GetAssignedShifts(Employee employee) {
        List<Shift> shifts = getCurrentWeekShifts();
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
        return getCurrentWeekShifts().stream()
                .filter(s -> s.getAssignedEmployees().stream()
                        .anyMatch(sa -> sa.getEmployeeId().equals(employee.getId())))
                .collect(Collectors.toList());
    }

    public Role getMyRoleForShift(Employee employee, Shift target) {
        return target.getAssignedEmployees().stream()
                .filter(sa -> sa.getEmployeeId().equals(employee.getId()))
                .findFirst()
                .map(ShiftAssignment::getRole)
                .orElse(null);
    }

    public void getCurrentShift() {
        Optional<Shift> currentShift = shiftDAO.getCurrentShift();
        if (currentShift.isPresent()) {
            PresentationUtils.printShift(currentShift.get());
        } else {
            PresentationUtils.typewriterPrint("There is no shift currently active.", 20);
        }
    }

    public void addTemplate(ShiftTemplate shiftTemplate) {
        templateDAO.insert(shiftTemplate);
    }

    public List<ShiftTemplate> getTemplates() {
        return templateDAO.selectAll();
    }

    public WeeklyShiftsSchedule getSchedule() {
        return shiftDAO.getSchedule();
    }

    public boolean isWarehouseAssigned(String shiftId) {
        return shiftDAO.isWarehouseEmployeeAssigned(shiftId);
    }

}
