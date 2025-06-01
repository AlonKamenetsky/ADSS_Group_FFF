package HR.Domain;

import java.time.LocalTime;
import java.util.*;

public class Shift {



    public enum ShiftTime {
        Morning, Evening
    }

    private final String ID;
    private final Date date;
    private final ShiftTime Type;
    private final Map<Role, ArrayList<Employee>> requiredRoles;
    // New mapping: Required count for each role.
    private final Map<Role, Integer> requiredCounts;
    private final List<ShiftAssignment> assignedEmployees;

    // Updated constructor to include requiredCounts mapping.
    public Shift(String ID, Date date, ShiftTime type, Map<Role, ArrayList<Employee>> requiredRoles, Map<Role, Integer> requiredCounts) {
        this.ID = ID;
        this.date = date;
        this.Type = type;
        this.requiredRoles = requiredRoles;
        this.requiredCounts = requiredCounts;
        this.assignedEmployees = new ArrayList<>();
    }

    public static ShiftTime fromTime(LocalTime time) {
        LocalTime startMorning = LocalTime.of(8, 0);    // 08:00
        LocalTime endMorning = LocalTime.of(17, 0);     // 17:00 (start of evening)

        if (!time.isBefore(startMorning) && time.isBefore(endMorning)) {
            return ShiftTime.Morning;
        }

        if (!time.isBefore(endMorning)) {
            return ShiftTime.Evening;
        }

        // 00:00 to 07:59 — closed
        return null;
    }


    public String getID() {
        return ID;
    }

    public String getDateString() {
        return date.toString();
    }
    public Date getDate() {
        return date;
    }

    public ShiftTime getType() {
        return Type;
    }

    public Map<Role, ArrayList<Employee>> getRequiredRoles() {
        return requiredRoles;
    }

    public Map<Role, Integer> getRequiredCounts() {
        return requiredCounts;
    }

    public void addRequiredRole(Role role, Employee employee) {
        requiredRoles.get(role).add(employee);
    }

    public void assignEmployee(Employee employee, Role role) {
        // Optionally, check if the role's assigned list size is less than requiredCounts.get(role)
        if (requiredRoles.containsKey(role)) {
            List<Employee> assigned = requiredRoles.get(role);
            int requiredCount = requiredCounts.get(role);
            if (assigned.size() < requiredCount) {
                assigned.add(employee);
                assignedEmployees.add(new ShiftAssignment(employee.getId(), this.ID, role));
            } else {
                System.out.println("The required number of employees for role " + role.getName() + " is already met.");
            }
        }
    }
    public void setAssignedEmployees(List<ShiftAssignment> assignments) {
        this.assignedEmployees.clear();
        this.assignedEmployees.addAll(assignments);
    }
    public List<ShiftAssignment> getAssignedEmployees() {
        return assignedEmployees;
    }

}