package HR.tests.ServiceTests;

import HR.DataAccess.*;
import HR.Domain.*;
import HR.DTO.*;
import HR.Service.ShiftService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShiftServiceTest {

    private ShiftDAO shiftDAO;
    private ShiftTemplateDAO templateDAO;
    private EmployeeDAO employeeDAO;
    private RoleDAO roleDAO;
    private ShiftService service;

    @BeforeEach
    void setUp() {
        shiftDAO = mock(ShiftDAO.class);
        templateDAO = mock(ShiftTemplateDAO.class);
        employeeDAO = mock(EmployeeDAO.class);
        roleDAO = mock(RoleDAO.class);
        service = new ShiftService(shiftDAO, templateDAO, employeeDAO, roleDAO);
    }

    @Test
    void testGetShiftById_validId() {
        Shift mockShift = mock(Shift.class);
        when(shiftDAO.selectById("S1")).thenReturn(mockShift);
        when(mockShift.getID()).thenReturn("S1");
        when(mockShift.getDate()).thenReturn(Date.valueOf("2025-06-03"));
        when(mockShift.getType()).thenReturn(Shift.ShiftTime.Morning);
        when(mockShift.getRequiredCounts()).thenReturn(Map.of("Worker", 2).entrySet().stream().collect(
                Collectors.toMap(e -> new Role(e.getKey()), Map.Entry::getValue)));
        when(mockShift.getAssignedEmployees()).thenReturn(List.of());

        ShiftDTO dto = service.getShiftById("S1");

        assertNotNull(dto);
        assertEquals("S1", dto.getId());
        assertEquals(LocalDate.of(2025, 6, 3), dto.getDate());
    }

    @Test
    void testAssignEmployeeToShift_successful() {
        Shift shift = mock(Shift.class);
        Employee emp = mock(Employee.class);
        Role role = mock(Role.class);

        when(shiftDAO.selectById("S1")).thenReturn(shift);
        when(employeeDAO.selectById("E1")).thenReturn(emp);
        when(roleDAO.findByName("Worker")).thenReturn(role);

        service.assignEmployeeToShift("S1", "E1", "Worker");

        verify(shift).assignEmployee(emp, role);
        verify(shiftDAO).update(shift);
    }

    @Test
    void testGetShiftsForDate_returnsCorrectList() {
        Shift shift = mock(Shift.class);
        when(shift.getID()).thenReturn("S1");
        when(shift.getDate()).thenReturn(Date.valueOf("2025-06-03"));
        when(shift.getType()).thenReturn(Shift.ShiftTime.Morning);
        when(shift.getRequiredCounts()).thenReturn(Map.of("Worker", 1).entrySet().stream().collect(
                Collectors.toMap(e -> new Role(e.getKey()), Map.Entry::getValue)));
        when(shift.getAssignedEmployees()).thenReturn(List.of());

        when(shiftDAO.getShiftsByDate(LocalDate.of(2025, 6, 3))).thenReturn(List.of(shift));

        List<ShiftDTO> result = service.getShiftsForDate(LocalDate.of(2025, 6, 3));

        assertEquals(1, result.size());
        assertEquals("S1", result.get(0).getId());
    }

    // Add more tests here for: configureShiftRoles, getAssignedShifts, getMyRoleForShift, addTemplate, etc.
}
