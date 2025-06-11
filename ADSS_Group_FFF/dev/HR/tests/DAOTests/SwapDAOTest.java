
package HR.tests.DAOTests;

import HR.DataAccess.SwapDAO;
import HR.DataAccess.SwapDAOImpl;
import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.SwapRequest;
import Util.Database;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SwapDAOTest {

    private SwapDAO swapRequestDAO;
    private Connection conn;

    @BeforeAll
    public void setup() {
        conn = Database.getConnection();
        swapRequestDAO = new SwapDAOImpl(conn);
    }

    @BeforeEach
    public void cleanUp() {
        try {
            conn.createStatement().execute("DELETE FROM SwapRequests");
        } catch (Exception e) {
            throw new RuntimeException("Failed to clean SwapRequests table", e);
        }
    }

    private SwapRequest buildRequest(String id, String empName, String shiftId, String roleName) {
        Employee emp = new Employee(id, List.of(new Role(roleName)), empName, "pw", "ILXXX", 5000f, new Date());
        Role role = new Role(roleName);
        Map<Role, ArrayList<Employee>> roles = new HashMap<>();
        Map<Role, Integer> counts = new HashMap<>();
        roles.put(role, new ArrayList<>());
        counts.put(role, 1);
        Shift shift = new Shift(shiftId, new Date(), Shift.ShiftTime.Morning, roles, counts);
        return new SwapRequest(emp, shift, role);
    }

    @Test
    public void testInsertAndRetrieveSwapRequest() {
        SwapRequest request = buildRequest("e001", "Alice", "S001", "Cook");
        swapRequestDAO.insert(request);

        SwapRequest retrieved = swapRequestDAO.selectById(request.getId());
        assertNotNull(retrieved);
        assertEquals("Alice", retrieved.getEmployee().getName());
        assertEquals("Cook", retrieved.getRole().getName());
    }

    @Test
    public void testSelectAll() {
        swapRequestDAO.insert(buildRequest("e002", "Bob", "S002", "Driver"));
        swapRequestDAO.insert(buildRequest("e003", "Carol", "S003", "Tech"));

        List<SwapRequest> all = swapRequestDAO.selectAll();
        assertTrue(all.size() >= 2);
    }

    @Test
    public void testDeleteRequest() {
        SwapRequest request = buildRequest("e004", "David", "S004", "Warehouse");
        swapRequestDAO.insert(request);

        swapRequestDAO.delete(request.getId());
        assertNull(swapRequestDAO.selectById(request.getId()));
    }
}
