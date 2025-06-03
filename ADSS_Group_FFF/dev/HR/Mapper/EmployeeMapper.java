package HR.Mapper;

import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Domain.WeeklyAvailability;
import HR.DTO.EmployeeDTO;
import HR.DTO.RoleDTO;
import HR.DTO.WeeklyAvailabilityDTO;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeMapper {

    private static LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static Date toUtilDate(LocalDate localDate) {
        return localDate == null ? null : Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Convert a domain Employee → EmployeeDTO (no password ever goes into EmployeeDTO).
     */
    public static EmployeeDTO toDTO(Employee e) {
        if (e == null) return null;

        List<RoleDTO> roleDtos = e.getRoles().stream()
                .map(r -> new RoleDTO(r.getName()))
                .collect(Collectors.toList());

        List<WeeklyAvailabilityDTO> availThisWeek = e.getAvailabilityThisWeek().stream()
                .map(wa -> new WeeklyAvailabilityDTO(wa.getDay(), wa.getTime()))
                .collect(Collectors.toList());

        List<WeeklyAvailabilityDTO> availNextWeek = e.getAvailabilityNextWeek().stream()
                .map(wa -> new WeeklyAvailabilityDTO(wa.getDay(), wa.getTime()))
                .collect(Collectors.toList());

        List<LocalDate> holidays = e.getHolidays().stream()
                .map(EmployeeMapper::toLocalDate)
                .collect(Collectors.toList());

        return new EmployeeDTO(
                e.getId(),
                e.getName(),
                roleDtos,
                e.getBankAccount(),
                e.getSalary(),
                toLocalDate(e.getEmploymentDate()),
                availThisWeek,
                availNextWeek,
                holidays
        );
    }

    /**
     * Convert an EmployeeDTO → domain Employee.  We intentionally pass `password = null` here,
     * because EmployeeDTO has no password.  If you need to set a password, do that in the Service layer.
     */
    public static Employee fromDTO(EmployeeDTO dto) {
        if (dto == null) return null;

        List<Role> roles = dto.getRoles().stream()
                .map(rdto -> new Role(rdto.getName()))
                .collect(Collectors.toList());

        Employee e = new Employee(
                dto.getId(),
                roles,
                dto.getName(),
                null,
                dto.getBankAccount(),
                dto.getSalary(),
                toUtilDate(dto.getEmploymentDate())
        );

        if (dto.getAvailabilityThisWeek() != null) {
            for (WeeklyAvailabilityDTO waDto : dto.getAvailabilityThisWeek()) {
                WeeklyAvailability wa = new WeeklyAvailability(waDto.getDay(), waDto.getTime());
                e.getAvailabilityThisWeek().add(wa);
            }
        }
        if (dto.getAvailabilityNextWeek() != null) {
            for (WeeklyAvailabilityDTO waDto : dto.getAvailabilityNextWeek()) {
                WeeklyAvailability wa = new WeeklyAvailability(waDto.getDay(), waDto.getTime());
                e.getAvailabilityNextWeek().add(wa);
            }
        }
        if (dto.getHolidays() != null) {
            e.getHolidays().addAll(dto.getHolidays().stream()
                    .map(EmployeeMapper::toUtilDate)
                    .collect(Collectors.toList()));
        }

        return e;
    }
}
