package Domain;

import java.util.*;

public class ShiftDL {

    public enum ShiftTime{
        Morning , Evening
    }

    private final String ID;
    private final Date date;
    private final ShiftTime Type;
    private final Map<RoleDL,List<EmployeeDL>> RequiredRoles;
    private List<ShiftAssignmentDL> assignedEmployees;

    public ShiftDL(String ID, Date date, ShiftTime type, Map<RoleDL,List<EmployeeDL>> RequiredRoles) {
        this.ID = ID;
        this.date = date;
        this.Type = type;
        this.RequiredRoles = RequiredRoles;
        this.assignedEmployees = new ArrayList<>();
    }

    public String getID() {
        return ID;
    }

    public Date getDate() {
        return date;
    }

    public ShiftTime getType() {
        return Type;
    }

    public Map<RoleDL, List<EmployeeDL>> getRequiredRoles() {
        return RequiredRoles;
    }

    public void AddRequiredRole(RoleDL role, EmployeeDL employee) {
        RequiredRoles.get(role).add(employee);
    }

    public void assignEmployee(EmployeeDL employee, RoleDL role) {
        assignedEmployees.add(new ShiftAssignmentDL(employee.getId(), this.ID, role));
    }

    public List<ShiftAssignmentDL> getAssignedEmployees() {
        return assignedEmployees;
    }

}
