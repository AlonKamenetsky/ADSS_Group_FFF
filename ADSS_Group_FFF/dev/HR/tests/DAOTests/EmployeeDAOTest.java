
package HR.tests.DAOTests;

import HR.DataAccess.EmployeeDAO;
import HR.DataAccess.EmployeeDAOImpl;
import HR.Domain.Employee;
import HR.Domain.Role;
import Util.Database;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmployeeDAOTest {

    private EmployeeDAO employeeDAO;
    private Connection conn;

    @BeforeAll
    public void setup() {
        conn = Database.getConnection();
        employeeDAO = new EmployeeDAOImpl(conn);
    }

    @BeforeEach
    public void cleanDB() throws Exception {
        conn.createStatement().execute("DELETE FROM Employees");  // adjust table name if needed
    }

    @Test
    public void testInsertAndSelectById() {
        Employee e = new Employee("e001", List.of(new Role("Clerk")), "Alice", "pw", "IL001", 5000f, new Date());
        employeeDAO.insert(e);

        Employee retrieved = employeeDAO.selectById("e001");
        assertNotNull(retrieved);
        assertEquals("Alice", retrieved.getName());
    }

    @Test
    public void testSelectAll() {
        employeeDAO.insert(new Employee("e002", List.of(new Role("Tech")), "Bob", "pw", "IL002", 6000f, new Date()));
        employeeDAO.insert(new Employee("e003", List.of(new Role("Mgr")), "Carol", "pw", "IL003", 7000f, new Date()));

        List<Employee> all = employeeDAO.selectAll();
        assertTrue(all.size() >= 2);
    }

    @Test
    public void testUpdateEmployee() {
        Employee e = new Employee("e004", List.of(new Role("Sales")), "Dana", "pw", "IL004", 5500f, new Date());
        employeeDAO.insert(e);

        e.setSalary(6200f);
        employeeDAO.update(e);

        Employee updated = employeeDAO.selectById("e004");
        assertEquals(6200f, updated.getSalary());
    }

    @Test
    public void testDeleteEmployee() {
        Employee e = new Employee("e005", List.of(new Role("Admin")), "Eli", "pw", "IL005", 4800f, new Date());
        employeeDAO.insert(e);
        employeeDAO.delete("e005");

        assertNull(employeeDAO.selectById("e005"));
    }
}
