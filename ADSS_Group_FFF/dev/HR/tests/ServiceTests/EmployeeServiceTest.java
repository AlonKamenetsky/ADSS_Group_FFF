
package HR.tests.ServiceTests;

import HR.DTO.DriverInfoDTO;
import HR.DTO.EmployeeDTO;
import HR.DTO.RoleDTO;
import HR.Domain.Role;
import HR.Service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeServiceTest {

    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        employeeService = EmployeeService.getInstance();
    }

    @Test
    public void testAddAndGetEmployee() {
        String id = "12345";
        List<RoleDTO> roles = List.of(new RoleDTO("Clerk"));
        EmployeeDTO employeeDTO = new EmployeeDTO(
                id, "Alice", roles, "IL000111", 4000f, new Date(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList()
        );
        employeeService.addEmployee(employeeDTO);

        EmployeeDTO dto = employeeService.getEmployeeById(id);
        assertNotNull(dto);
        assertEquals("Alice", dto.getName());
        assertEquals("IL000111", dto.getBankAccount());
    }

    @Test
    public void testGetAllEmployeesReturnsDTOs() {
        List<EmployeeDTO> employees = employeeService.getEmployees();
        assertNotNull(employees);
        assertTrue(employees.stream().allMatch(e -> e instanceof EmployeeDTO));
    }

    @Test
    public void testUpdateSalary() {
        String id = "54321";
        List<RoleDTO> roles = List.of(new RoleDTO("Tech"));
        EmployeeDTO employeeDTO = new EmployeeDTO(
                id, "Bob", roles, "IL222333", 6000f, new Date(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        employeeService.addEmployee(employeeDTO);
        employeeDTO.setSalary(7500f);
        employeeService.updateEmployee(employeeDTO);

        EmployeeDTO updated = employeeService.getEmployeeById(id);
        assertEquals(7500f, updated.getSalary());
    }

    @Test
    public void testUpdateBankAccount() {
        String id = "67890";
        List<RoleDTO> roles = List.of(new RoleDTO("Driver"));
        EmployeeDTO employeeDTO = new EmployeeDTO(
                id, "Dana", roles, "IL555666", 6000f, new Date(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        employeeService.addEmployee(employeeDTO);
        employeeDTO.setBankAccount("ILNEW123");
        employeeService.updateEmployee(employeeDTO);

        EmployeeDTO updated = employeeService.getEmployeeById(id);
        assertEquals("ILNEW123", updated.getBankAccount());
    }

    @Test
    public void testRemoveEmployee() {
        String id = "toRemove";
        List<RoleDTO> roles = List.of(new RoleDTO("Temp"));
        EmployeeDTO employeeDTO = new EmployeeDTO(
                id, "Tom", roles, "BANK1", 3000f, new Date(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        employeeService.addEmployee(employeeDTO);
        assertNotNull(employeeService.getEmployeeById(id));

        employeeService.removeEmployee(id);
        assertNull(employeeService.getEmployeeById(id));
    }


    @Test
    public void testGetNonExistentEmployeeReturnsNull() {
        assertNull(employeeService.getEmployeeById("nonexistent"));
    }

    @Test
    public void testAddEmployeeWithNullValuesDoesNotCrash() {
        assertDoesNotThrow(() -> {
            employeeService.addEmployee(new EmployeeDTO("nullID",null, new ArrayList<>(), "NullBank", null, null, null, null, null));
        });

        EmployeeDTO dto = employeeService.getEmployeeById("nullID");
        assertNotNull(dto);
        assertNull(dto.getName());
    }
}
