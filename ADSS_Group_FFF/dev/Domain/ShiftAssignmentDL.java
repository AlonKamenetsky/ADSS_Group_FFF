package Domain;

public class ShiftAssignmentDL {
    private String employeeId;
    private String shiftId;
    private RoleDL role;

    public ShiftAssignmentDL(String employeeId, String shiftId, RoleDL role) {
        this.employeeId = employeeId;
        this.shiftId = shiftId;
        this.role = role;
    }

    public String getEmployeeId() { return employeeId; }
    public String getShiftId() { return shiftId; }
    public RoleDL getRole() { return role; }





}
