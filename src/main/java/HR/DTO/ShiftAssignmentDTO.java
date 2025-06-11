// File: ShiftAssignmentDTO.java
package HR.DTO;

public class ShiftAssignmentDTO {
    private String employeeId;
    private String shiftId;
    private String roleName;  // store role.getName()

    public ShiftAssignmentDTO() { }

    public ShiftAssignmentDTO(String employeeId, String shiftId, String roleName) {
        this.employeeId = employeeId;
        this.shiftId = shiftId;
        this.roleName = roleName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
