package HR.tests.MapperTests;

import HR.Domain.*;
import HR.DTO.*;
import HR.Mapper.EmployeeMapper;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeMapperTest {

    @Test
    public void testToDTO_withValidEmployee_returnsCorrectDTO() {
        Role role = new Role("Clerk");
        Employee employee = new Employee("E1", List.of(role), "Alice", "pass",
                "5678", 6000f, java.sql.Date.valueOf("2022-01-01"));
        employee.getAvailabilityThisWeek().add(new WeeklyAvailability(DayOfWeek.MONDAY, Shift.ShiftTime.Morning));
        employee.getAvailabilityNextWeek().add(new WeeklyAvailability(DayOfWeek.TUESDAY, Shift.ShiftTime.Evening));
        employee.getHolidays().add(java.sql.Date.valueOf("2025-07-01"));

        EmployeeDTO dto = EmployeeMapper.toDTO(employee);

        assertEquals("E1", dto.getId());
        assertEquals("Alice", dto.getName());
        assertEquals("Clerk", dto.getRoles().getFirst().getName());
        assertEquals(LocalDate.of(2022, 1, 1), dto.getEmploymentDate());
        assertEquals(1, dto.getAvailabilityThisWeek().size());
        assertEquals(DayOfWeek.MONDAY, dto.getAvailabilityThisWeek().getFirst().getDay());
        assertEquals(1, dto.getAvailabilityNextWeek().size());
        assertEquals(DayOfWeek.TUESDAY, dto.getAvailabilityNextWeek().getFirst().getDay());
        assertEquals(LocalDate.of(2025, 7, 1), dto.getHolidays().getFirst());
    }

    @Test
    public void testFromDTO_withValidDTO_returnsCorrectEmployee() {
        RoleDTO roleDto = new RoleDTO("Driver");
        WeeklyAvailabilityDTO availThisWeek = new WeeklyAvailabilityDTO(DayOfWeek.WEDNESDAY, Shift.ShiftTime.Morning);
        WeeklyAvailabilityDTO availNextWeek = new WeeklyAvailabilityDTO(DayOfWeek.THURSDAY, Shift.ShiftTime.Evening);
        LocalDate holiday = LocalDate.of(2025, 12, 31);

        EmployeeDTO dto = new EmployeeDTO("E2", "Bob", List.of(roleDto), "9999",
                4500f, LocalDate.of(2020, 5, 5), List.of(availThisWeek), List.of(availNextWeek), List.of(holiday));

        Employee emp = EmployeeMapper.fromDTO(dto);

        assertEquals("E2", emp.getId());
        assertEquals("Bob", emp.getName());
        assertEquals("Driver", emp.getRoles().getFirst().getName());
        assertEquals(java.sql.Date.valueOf("2020-05-05"), emp.getEmploymentDate());
        assertEquals(DayOfWeek.WEDNESDAY, emp.getAvailabilityThisWeek().getFirst().getDay());
        assertEquals(DayOfWeek.THURSDAY, emp.getAvailabilityNextWeek().getFirst().getDay());
        assertEquals(java.sql.Date.valueOf("2025-12-31"), emp.getHolidays().getFirst());
    }

    @Test
    public void testToDTO_withNull_returnsNull() {
        assertNull(EmployeeMapper.toDTO(null));
    }

    @Test
    public void testFromDTO_withNull_returnsNull() {
        assertNull(EmployeeMapper.fromDTO(null));
    }
}
