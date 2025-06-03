package HR.DTO;

import HR.Domain.Shift.ShiftTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ShiftDTO {
    private String id;
    private LocalDate date;
    private ShiftTime type;
    private Map<String, Integer> requiredCounts;
    private List<ShiftAssignmentDTO> assignedEmployees;

    public ShiftDTO() { }

    public ShiftDTO(
            String id,
            LocalDate date,
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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
