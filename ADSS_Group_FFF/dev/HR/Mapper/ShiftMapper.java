package HR.Mapper;

import HR.Domain.Shift;
import HR.Domain.Role;
import HR.Domain.Employee;
import HR.Domain.ShiftAssignment;
import HR.DTO.ShiftDTO;
import HR.DTO.ShiftAssignmentDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShiftMapper {
    public static ShiftDTO toDTO(Shift s) {
        if (s == null) return null;

        Map<String, Integer> requiredCountsDto = s.getRequiredCounts().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getName(),
                        Map.Entry::getValue
                ));

        List<ShiftAssignmentDTO> assignmentDtos = s.getAssignedEmployees().stream()
                .map(ShiftAssignmentMapper::toDTO)
                .collect(Collectors.toList());

        LocalDate localDate = new java.sql.Date(s.getDate().getTime()).toLocalDate();

        return new ShiftDTO(
                s.getID(),
                localDate,
                s.getType(),
                requiredCountsDto,
                assignmentDtos
        );
    }

    public static Shift fromDTO(ShiftDTO dto) {
        if (dto == null) return null;

        Map<Role, Integer> requiredCounts = dto.getRequiredCounts().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> new Role(entry.getKey()),
                        Map.Entry::getValue
                ));

        Map<Role, ArrayList<Employee>> emptyReqRoles = Map.of();

        Date utilDate = java.sql.Date.valueOf(dto.getDate());

        Shift s = new Shift(
                dto.getId(),
                utilDate,
                dto.getType(),
                emptyReqRoles,
                requiredCounts
        );

        List<ShiftAssignment> assignments = dto.getAssignedEmployees().stream()
                .map(ShiftAssignmentMapper::fromDTO)
                .peek(assign -> assign.setShift(s))
                .collect(Collectors.toList());
        s.setAssignedEmployees(assignments);

        return s;
    }
}
