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

    public ShiftDL(String ID, Date date, ShiftTime type, Map<RoleDL,List<EmployeeDL>> RequiredRoles) {
        this.ID = ID;
        this.date = date;
        this.Type = type;
        this.RequiredRoles = RequiredRoles;
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

}
