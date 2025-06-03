package HR.tests.ServiceTests;

import HR.DataAccess.EmployeeDAO;
import HR.Domain.Employee;
import HR.Domain.User;
import HR.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock private EmployeeDAO employeeDAO;
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(employeeDAO);
    }

    @Test
    void authenticate_withValidCredentials_returnsUser() {
        Employee emp = new Employee("E1", null, "Alice", "secret", null, 0f, null);
        when(employeeDAO.selectById("E1")).thenReturn(emp);

        User result = userService.authenticate("E1", "secret");
        assertNotNull(result);
        assertEquals("Alice", result.getName());
    }

    @Test
    void authenticate_withInvalidPassword_returnsNull() {
        Employee emp = new Employee("E2", null, "Bob", "hunter2", null, 0f, null);
        when(employeeDAO.selectById("E2")).thenReturn(emp);

        User result = userService.authenticate("E2", "wrongpass");
        assertNull(result);
    }

    @Test
    void authenticate_withUnknownId_returnsNull() {
        when(employeeDAO.selectById("X")).thenReturn(null);
        assertNull(userService.authenticate("X", "anything"));
    }

    @Test
    void authenticate_withNullOrBlankId_returnsNull() {
        assertNull(userService.authenticate(null, "pw"));
        assertNull(userService.authenticate("", "pw"));
    }

    @Test
    void authenticate_withNullPassword_returnsNull() {
        Employee emp = new Employee("E3", null, "Carol", "pw123", null, 0f, null);
        when(employeeDAO.selectById("E3")).thenReturn(emp);
        assertNull(userService.authenticate("E3", null));
    }
}
