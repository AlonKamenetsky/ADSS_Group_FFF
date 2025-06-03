package HR.tests.ServiceTests;

import HR.DataAccess.EmployeeDAO;
import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Service.EmployeeService;
import HR.DTO.EmployeeDTO;
import HR.DTO.RoleDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmployeeServiceTest {

    private EmployeeDAO mockEmployeeDAO;
    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        mockEmployeeDAO = mock(EmployeeDAO.class);
        employeeService = new EmployeeService(mockEmployeeDAO); // assuming constructor injection is possible
    }

    @Test
    public void testAddEmployeeCallsDAO() {
        RoleDTO roleDto = new RoleDTO("HR");
        List<RoleDTO> roles = List.of(roleDto);

        EmployeeDTO dto = new EmployeeDTO(
                "E001", "Alice", roles,
                "IL123456", 5000f,
                new Date(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
        );

        employeeService.addEmployee(dto);

        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(mockEmployeeDAO).insert(captor.capture());
        assertEquals("Alice", captor.getValue().getName());
    }

    @Test
    public void testGetEmployeeByIdReturnsCorrectData() {
        Role hrRole = new Role("HR");
        Employee mockEmployee = new Employee("E001", List.of(hrRole), "Alice", "pass", "IL123456", 5000f, new Date());

        when(mockEmployeeDAO.selectById("E001")).thenReturn(mockEmployee);

        EmployeeDTO dto = employeeService.getEmployeeById("E001");
        assertNotNull(dto);
        assertEquals("Alice", dto.getName());
        assertEquals("IL123456", dto.getBankAccount());
    }

    @Test
    public void testRemoveEmployeeDelegatesToDAO() {
        employeeService.removeEmployee("E001");
        verify(mockEmployeeDAO).delete("E001");
    }

    @Test
    public void testUpdateEmployeeDelegatesToDAO() {
        RoleDTO roleDto = new RoleDTO("Driver");
        EmployeeDTO dto = new EmployeeDTO(
                "E002", "Bob", List.of(roleDto),
                "BANK2", 6000f,
                new Date(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
        );

        employeeService.updateEmployee(dto);
        verify(mockEmployeeDAO).update(any(Employee.class));
    }

    @Test
    public void testGetAllEmployeesReturnsMappedDTOs() {
        List<Employee> employees = List.of(
                new Employee("E003", List.of(new Role("Warehouse")), "Tom", "1234", "BANK3", 3500f, new Date())
        );

        when(mockEmployeeDAO.selectAll()).thenReturn(employees);

        List<EmployeeDTO> result = employeeService.getEmployees();
        assertEquals(1, result.size());
        assertEquals("Tom", result.get(0).getName());
    }
}
