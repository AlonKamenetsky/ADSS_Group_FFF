package HR.Domain;

public class ShiftAssignment {
    private String employeeId;
    private String shiftId;
    private Role role;

    public ShiftAssignment(String employeeId, String shiftId, Role role) {
        this.employeeId = employeeId;
        this.shiftId = shiftId;
        this.role = role;
    }

    public String getEmployeeId() { return employeeId; }
    public String getShiftId() { return shiftId; }
    public Role getRole() { return role; }





}
