// File: HR/Mapper/SwapRequestMapper.java
package HR.Mapper;

import HR.Domain.SwapRequest;
import HR.Domain.Employee;
import HR.Domain.Shift;
import HR.Domain.Role;
import HR.DTO.SwapRequestDTO;

public class SwapRequestMapper {
    public static SwapRequestDTO toDTO(SwapRequest r) {
        if (r == null) return null;
        // Domain getters: getId(), getEmployee(), getShift(), getRole()
        return new SwapRequestDTO(
                r.getId(),
                r.getEmployee().getId(),
                r.getShift().getID(),
                r.getRole().getName(),
                false // domain has no getter for “resolved,” so default to false
        );
    }

    public static SwapRequest fromDTO(SwapRequestDTO dto) {
        if (dto == null) return null;
        // Domain constructors:
        //    public SwapRequest(int id, Employee employee, Shift shift, Role role)
        // We ignore “resolved” altogether (domain never sets it).
        Employee e = new Employee(
                dto.getEmployeeId(),
            /* Since Employee extends User, and its constructor is:
               Employee(String ID, List<Role> roles, String name, String password, String bankAccount, Float salary, Date employmentDate)
               we don’t have all those fields in the DTO. For a “shallow” placeholder, pass empty/default values. */
                java.util.Collections.emptyList(),
                "",
                "",
                "",
                0f,
                new java.util.Date()
        );
        Shift s = new Shift(
                dto.getShiftId(),
                new java.util.Date(),
                null,
                java.util.Map.of(),
                java.util.Map.of()
        );
        Role rRole = new Role(dto.getRoleName());
        return new SwapRequest(dto.getId(), e, s, rRole);
    }
}
