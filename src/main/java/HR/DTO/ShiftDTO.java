// File: ShiftDTO.java
package HR.DTO;

import HR.Domain.Shift.ShiftTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ShiftDTO {
    private String id;
    private Date date;
    private ShiftTime type;
    private Map<String, Integer> requiredCounts;
    // (Map<roleName, count> instead of Map<Role, Integer>)
    private List<ShiftAssignmentDTO> assignedEmployees;

    public ShiftDTO() { }

    public ShiftDTO(
            String id,
            Date date,
            ShiftTime type,
            Map<String, Integer> requiredCounts,
            List<ShiftAssignmentDTO> assignedEmployees
    ) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.requiredCounts = requiredCounts;
        this.assignedEmployees = assignedEmployees;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ShiftTime getType() {
        return type;
    }

    public void setType(ShiftTime type) {
        this.type = type;
    }

    public Map<String, Integer> getRequiredCounts() {
        return requiredCounts;
    }

    public void setRequiredCounts(Map<String, Integer> requiredCounts) {
        this.requiredCounts = requiredCounts;
    }

    public List<ShiftAssignmentDTO> getAssignedEmployees() {
        return assignedEmployees;
    }

    public void setAssignedEmployees(List<ShiftAssignmentDTO> assignedEmployees) {
        this.assignedEmployees = assignedEmployees;
    }
}
