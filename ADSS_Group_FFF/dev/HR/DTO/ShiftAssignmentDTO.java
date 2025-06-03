package HR.DTO;

import java.util.Objects;

/**
 * DTO representing an assignment of an employee to a shift with a specific role.
 */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShiftAssignmentDTO that = (ShiftAssignmentDTO) o;
        return Objects.equals(employeeId, that.employeeId) &&
                Objects.equals(shiftId, that.shiftId) &&
                Objects.equals(roleName, that.roleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, shiftId, roleName);
    }

    @Override
    public String toString() {
        return "ShiftAssignmentDTO{" +
                "employeeId='" + employeeId + '\'' +
                ", shiftId='" + shiftId + '\'' +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
