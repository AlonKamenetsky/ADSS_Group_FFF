
package HR.tests.DAOTests;

import HR.DataAccess.RoleDAO;
import HR.DataAccess.RoleDAOImpl;
import HR.Domain.Role;
import Util.Database;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RoleDAOTest {

    private RoleDAO roleDAO;
    private Connection conn;

    @BeforeAll
    public void setupDB() {
        conn = Database.getConnection();  // assumes in-memory or test DB
        roleDAO = new RoleDAOImpl(conn);
    }

    @BeforeEach
    public void cleanUp() {
        try {
            conn.createStatement().execute("DELETE FROM Roles");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean Roles table", e);
        }
    }


    @Test
    public void testInsertAndFindByName() {
        Role role = new Role("Engineer");
        roleDAO.insert(role);

        Role retrieved = roleDAO.findByName("Engineer");
        assertNotNull(retrieved);
        assertEquals("Engineer", retrieved.getName());
    }

    @Test
    public void testFindAll() throws Exception {
        roleDAO.insert(new Role("Analyst"));
        roleDAO.insert(new Role("Manager"));

        List<Role> roles = roleDAO.findAll();
        assertTrue(roles.size() >= 2);
    }

    @Test
    public void testDelete() throws Exception {
        roleDAO.insert(new Role("TempRole"));
        roleDAO.delete("TempRole");

        Role shouldBeNull = roleDAO.findByName("TempRole");
        assertNull(shouldBeNull);
    }
}
