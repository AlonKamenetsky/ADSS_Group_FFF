// File: HR/Mapper/ShiftMapper.java
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

        // Domain getters:
        //    String getID(), Date getDate(), ShiftTime getType()
        //    Map<Role,List<Employee>> getRequiredRoles()
        //    Map<Role,Integer> getRequiredCounts()
        //    List<ShiftAssignment> getAssignedEmployees()
        //
        // Our DTO only contains: id, date, type, Map<String,Integer> requiredCounts, List<ShiftAssignmentDTO>
        // (We’re omitting requiredRoles in the DTO, since DTO earlier did not include it.)

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

        // Domain constructor is:
        //   public Shift(String ID,
        //                Date date,
        //                ShiftTime type,
        //                Map<Role, List<Employee>> requiredRoles,
        //                Map<Role, Integer> requiredCounts)
        //
        // Our DTO only has requiredCounts (Map<String,Integer>). We’ll pass an empty Map<Role,List<Employee>>
        // for requiredRoles, and build requiredCounts properly.

        Map<Role, Integer> requiredCounts = dto.getRequiredCounts().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> new Role(entry.getKey()),
                        Map.Entry::getValue
                ));

        // For “requiredRoles,” we don’t have that in the DTO. Just pass an empty map:
        Map<Role, ArrayList<Employee>> emptyReqRoles = Map.of();

        // Build the base Shift entity
        Shift s = new Shift(
                dto.getId(),
                dto.getDate(),
                dto.getType(),
                emptyReqRoles,
                requiredCounts
        );

        // Now set “assignedEmployees”: convert each ShiftAssignmentDTO → ShiftAssignment,
        // then call assignment.setShift(s) (ShiftAssignmentMapper does not set Shift, so do it here).
        List<ShiftAssignment> assignments = dto.getAssignedEmployees().stream()
                .map(ShiftAssignmentMapper::fromDTO)
                .peek(assign -> assign.setShift(s))
                .collect(Collectors.toList());
        s.setAssignedEmployees(assignments);

        return s;
    }
}
