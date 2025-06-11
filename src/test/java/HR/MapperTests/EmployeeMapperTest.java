package HR.tests.MapperTests;

import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.WeeklyAvailability;
import HR.DTO.EmployeeDTO;
import HR.DTO.RoleDTO;
import HR.DTO.WeeklyAvailabilityDTO;
import HR.Mapper.EmployeeMapper;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeMapperTest {

    @Test
    void testToDTO() {
        Role role = new Role("HR");
        WeeklyAvailability avail = new WeeklyAvailability(DayOfWeek.MONDAY, Shift.ShiftTime.Morning);
        Date date = new Date();

        Employee employee = new Employee("123", List.of(role), "Alice", "pw", "123-456", 5000f, date);
        employee.getAvailabilityThisWeek().add(avail);
        employee.getAvailabilityNextWeek().add(avail);
        employee.getHolidays().add(date);

        EmployeeDTO dto = EmployeeMapper.toDTO(employee);

        assertEquals("123", dto.getId());
        assertEquals("Alice", dto.getName());
        assertEquals(1, dto.getRoles().size());
        assertEquals("HR", dto.getRoles().get(0).getName());
        assertEquals("123-456", dto.getBankAccount());
        assertEquals(5000f, dto.getSalary());
        assertEquals(date, dto.getEmploymentDate());
        assertEquals(1, dto.getAvailabilityThisWeek().size());
        assertEquals(1, dto.getAvailabilityNextWeek().size());
        assertEquals(1, dto.getHolidays().size());
    }

    @Test
    void testFromDTO() {
        RoleDTO roleDto = new RoleDTO("Driver");
        WeeklyAvailabilityDTO availDto = new WeeklyAvailabilityDTO(DayOfWeek.TUESDAY, Shift.ShiftTime.Evening);
        Date date = new Date();

        EmployeeDTO dto = new EmployeeDTO("321", "Bob", List.of(roleDto), "999-888", 6000f, date,
                List.of(availDto), List.of(availDto), List.of(date));

        Employee employee = EmployeeMapper.fromDTO(dto);

        assertEquals("321", employee.getId());
        assertEquals("Bob", employee.getName());
        assertEquals(1, employee.getRoles().size());
        assertEquals("Driver", employee.getRoles().get(0).getName());
        assertEquals("999-888", employee.getBankAccount());
        assertEquals(6000f, employee.getSalary());
        assertEquals(date, employee.getEmploymentDate());
        assertEquals(1, employee.getAvailabilityThisWeek().size());
        assertEquals(1, employee.getAvailabilityNextWeek().size());
        assertEquals(1, employee.getHolidays().size());
    }
}
