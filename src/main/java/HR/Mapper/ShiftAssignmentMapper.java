package HR.Mapper;

import HR.Domain.Shift;
import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Domain.ShiftAssignment;
import HR.DTO.ShiftAssignmentDTO;

public class ShiftAssignmentMapper {
    public static ShiftAssignmentDTO toDTO(ShiftAssignment sa) {
        if (sa == null) return null;
        // Domain getters: getEmployeeId(), getShiftId(), getRole().getName()
        return new ShiftAssignmentDTO(
                sa.getEmployeeId(),
                sa.getShiftId(),
                sa.getRole().getName()
        );
    }

    public static ShiftAssignment fromDTO(ShiftAssignmentDTO dto) {
        if (dto == null) return null;
        // Domain constructor: public ShiftAssignment(String employeeId, String shiftId, Role role)
        Role role = new Role(dto.getRoleName());
        return new ShiftAssignment(dto.getEmployeeId(), dto.getShiftId(), role);
    }
}
