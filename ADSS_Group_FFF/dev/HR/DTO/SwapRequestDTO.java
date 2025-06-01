// File: SwapRequestDTO.java
package HR.DTO;

public class SwapRequestDTO {
    private int id;
    private String employeeId;
    private String shiftId;
    private String roleName;
    private boolean resolved;

    public SwapRequestDTO() { }

    public SwapRequestDTO(int id, String employeeId, String shiftId, String roleName, boolean resolved) {
        this.id = id;
        this.employeeId = employeeId;
        this.shiftId = shiftId;
        this.roleName = roleName;
        this.resolved = resolved;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }
}
