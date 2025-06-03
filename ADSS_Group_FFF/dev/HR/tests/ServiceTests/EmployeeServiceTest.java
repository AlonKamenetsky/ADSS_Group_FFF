
package HR.tests.ServiceTests;

import HR.DataAccess.EmployeeDAO;
import HR.DataAccess.DriverInfoDAO;
import HR.DataAccess.ShiftDAO;
import HR.Domain.Employee;
import HR.DTO.EmployeeDTO;
import HR.Mapper.EmployeeMapper;

import HR.Service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmployeeServiceTest {

    @Mock private EmployeeDAO employeeDAO;
    @Mock private DriverInfoDAO driverInfoDAO;
    @Mock private ShiftDAO shiftDAO;

    @InjectMocks private EmployeeService employeeService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        employeeService = new EmployeeService(employeeDAO, driverInfoDAO, shiftDAO);
    }

    @Test
    public void testGetEmployeeHolidays_validId_returnsHolidays() {
        List<LocalDate> holidays = new ArrayList<>();
        holidays.add(LocalDate.of(2025, 6, 10));
        EmployeeDTO dto = mock(EmployeeDTO.class);
        when(dto.getHolidays()).thenReturn(holidays);

        Employee domainEmp = mock(Employee.class);
        when(employeeDAO.selectById("E1")).thenReturn(domainEmp);
        try (MockedStatic<EmployeeMapper> mockedMapper = mockStatic(EmployeeMapper.class)) {
            mockedMapper.when(() -> EmployeeMapper.toDTO(domainEmp)).thenReturn(dto);

            List<LocalDate> result = employeeService.getEmployeeHolidays("E1");

            assertEquals(1, result.size());
            assertTrue(result.contains(LocalDate.of(2025, 6, 10)));
        }
    }

    @Test
    public void testAddVacation_addsHolidayIfNotPresent() {
        List<LocalDate> holidays = new ArrayList<>();
        EmployeeDTO dto = new EmployeeDTO();
        dto.setHolidays(holidays);

        Employee domainEmp = mock(Employee.class);
        when(employeeDAO.selectById("E2")).thenReturn(domainEmp);
        try (MockedStatic<EmployeeMapper> mockedMapper = mockStatic(EmployeeMapper.class)) {
            mockedMapper.when(() -> EmployeeMapper.toDTO(domainEmp)).thenReturn(dto);
            mockedMapper.when(() -> EmployeeMapper.fromDTO(dto)).thenReturn(domainEmp);

            LocalDate newHoliday = LocalDate.of(2025, 12, 25);

            employeeService.addVacation("E2", newHoliday);

            assertEquals(1, dto.getHolidays().size());
            assertEquals(newHoliday, dto.getHolidays().get(0));
            verify(employeeDAO).update(domainEmp);
        }
    }

    @Test
    public void testAddVacation_throwsIfEmployeeNotFound() {
        when(employeeDAO.selectById("INVALID")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            employeeService.addVacation("INVALID", LocalDate.now());
        });
    }

    @Test
    public void testGetEmployeeHolidays_throwsIfEmployeeNotFound() {
        when(employeeDAO.selectById("INVALID")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            employeeService.getEmployeeHolidays("INVALID");
        });
    }
}
