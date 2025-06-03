package HR.Mapper;

import HR.Domain.Shift;
import HR.Domain.Role;
import HR.Domain.Employee;
import HR.Domain.ShiftAssignment;
import HR.DTO.ShiftDTO;
import HR.DTO.ShiftAssignmentDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShiftMapper {
    public static ShiftDTO toDTO(Shift s) {
        if (s == null) return null;

        // Convert requiredCounts: Map<Role,Integer> → Map<String,Integer>
        Map<String, Integer> requiredCountsDto = s.getRequiredCounts().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getName(),
                        Map.Entry::getValue
                ));

        // Convert assignments
        List<ShiftAssignmentDTO> assignmentDtos = s.getAssignedEmployees().stream()
                .map(ShiftAssignmentMapper::toDTO)
                .collect(Collectors.toList());

        return new ShiftDTO(
                s.getID(),
                s.getDate(),
                s.getType(),
                requiredCountsDto,
                assignmentDtos
        );
    }

    public static Shift fromDTO(ShiftDTO dto) {
        if (dto == null) return null;

        // Convert Map<String,Integer> → Map<Role,Integer>
        Map<Role, Integer> requiredCounts = dto.getRequiredCounts().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> new Role(entry.getKey()),
                        Map.Entry::getValue
                ));

        // We don’t know “requiredRoles” yet—DAO will fill that in when reading from the DB
        Map<Role, ArrayList<Employee>> emptyReqRoles = Map.of();

        Shift s = new Shift(
                dto.getId(),
                dto.getDate(),
                dto.getType(),
                emptyReqRoles,
                requiredCounts
        );

        // Now fill assignedEmployees from DTO
        List<ShiftAssignment> assignments = dto.getAssignedEmployees().stream()
                .map(ShiftAssignmentMapper::fromDTO)
                .peek(assign -> assign.setShift(s))
                .collect(Collectors.toList());
        s.setAssignedEmployees(assignments);

        return s;
    }
}
