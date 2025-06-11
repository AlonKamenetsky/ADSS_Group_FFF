
package HR.tests.DAOTests;

import HR.DataAccess.ShiftAssignmentDAO;
import HR.DataAccess.ShiftAssignmentDAOImpl;
import Util.Database;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShiftAssignmentDAOTest {

    private ShiftAssignmentDAO assignmentDAO;
    private Connection conn;

    @BeforeAll
    public void setup() {
        conn = Database.getConnection();
        assignmentDAO = new ShiftAssignmentDAOImpl(conn);
    }

    @BeforeEach
    public void cleanUp() {
        try {
            conn.createStatement().execute("DELETE FROM ShiftAssignments");
        } catch (Exception e) {
            throw new RuntimeException("Failed to clean ShiftAssignments table", e);
        }
    }

    @Test
    public void testAssignAndRetrieve() {
        assignmentDAO.assignEmployeeToShift("SHIFT01", "emp1", "Driver");

        List<String> assigned = assignmentDAO.getEmployeesAssignedToShift("SHIFT01");
        assertNotNull(assigned);
        assertTrue(assigned.contains("emp1"));
    }

    @Test
    public void testRemoveAssignment() {
        assignmentDAO.assignEmployeeToShift("SHIFT02", "emp2", "Cook");
        assignmentDAO.removeAssignment("SHIFT02", "emp2", "Cook");

        List<String> assigned = assignmentDAO.getEmployeesAssignedToShift("SHIFT02");
        assertFalse(assigned.contains("emp2"));
    }
}
