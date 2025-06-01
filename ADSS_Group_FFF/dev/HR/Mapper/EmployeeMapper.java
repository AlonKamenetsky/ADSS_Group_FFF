// File: HR/Mapper/EmployeeMapper.java
package HR.Mapper;

import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Domain.WeeklyAvailability;
import HR.DTO.EmployeeDTO;
import HR.DTO.RoleDTO;
import HR.DTO.WeeklyAvailabilityDTO;

import java.util.List;
import java.util.stream.Collectors;

public class EmployeeMapper {

    /**
     * Convert a domain Employee into an EmployeeDTO.
     */
    public static EmployeeDTO toDTO(Employee e) {
        if (e == null) return null;

        // Map Role → RoleDTO
        List<RoleDTO> roleDtos = e.getRoles().stream()
                .map(r -> new RoleDTO(r.getName()))
                .collect(Collectors.toList());

        // Map WeeklyAvailability → WeeklyAvailabilityDTO for this week
        List<WeeklyAvailabilityDTO> availThisWeek = e.getAvailabilityThisWeek().stream()
                .map(wa -> new WeeklyAvailabilityDTO(wa.getDay(), wa.getTime()))
                .collect(Collectors.toList());

        // Map WeeklyAvailability → WeeklyAvailabilityDTO for next week
        List<WeeklyAvailabilityDTO> availNextWeek = e.getAvailabilityNextWeek().stream()
                .map(wa -> new WeeklyAvailabilityDTO(wa.getDay(), wa.getTime()))
                .collect(Collectors.toList());

        // Holidays (List<Date>) can be copied directly
        List<java.util.Date> holidays = e.getHolidays();

        // Build and return the DTO
        return new EmployeeDTO(
                e.getId(),
                e.getName(),
                roleDtos,
                e.getBankAccount(),
                e.getSalary(),
                e.getEmploymentDate(),
                availThisWeek,
                availNextWeek,
                holidays
        );
    }

    /**
     * Convert an EmployeeDTO into a domain Employee.
     */
    public static Employee fromDTO(EmployeeDTO dto) {
        if (dto == null) return null;

        // Map RoleDTO → Role
        List<Role> roles = dto.getRoles().stream()
                .map(rdto -> new Role(rdto.getName()))
                .collect(Collectors.toList());

        // Construct the Employee.
        // Domain constructor: Employee(String ID, List<Role> roles, String name, String password,
        //                       String bankAccount, Float salary, Date employmentDate)
        // We do not carry password in the DTO, so pass null.
        Employee e = new Employee(
                dto.getId(),
                roles,
                dto.getName(),
                null,                 // no password provided in DTO
                dto.getBankAccount(),
                dto.getSalary(),
                dto.getEmploymentDate()
        );

        // Populate availabilityThisWeek
        for (WeeklyAvailabilityDTO waDto : dto.getAvailabilityThisWeek()) {
            WeeklyAvailability wa = new WeeklyAvailability(waDto.getDay(), waDto.getTime());
            e.getAvailabilityThisWeek().add(wa);
        }

        // Populate availabilityNextWeek
        for (WeeklyAvailabilityDTO waDto : dto.getAvailabilityNextWeek()) {
            WeeklyAvailability wa = new WeeklyAvailability(waDto.getDay(), waDto.getTime());
            e.getAvailabilityNextWeek().add(wa);
        }

        // Populate holidays (List<Date>)
        if (dto.getHolidays() != null) {
            e.getHolidays().addAll(dto.getHolidays());
        }

        return e;
    }
}
