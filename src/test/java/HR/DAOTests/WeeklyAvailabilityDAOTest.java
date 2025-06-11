
package HR.tests.DAOTests;

import HR.DataAccess.WeeklyAvailabilityDAO;
import HR.DataAccess.WeeklyAvailabilityDAOImpl;
import Util.Database;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WeeklyAvailabilityDAOTest {

    private WeeklyAvailabilityDAO availabilityDAO;
    private Connection conn;

    @BeforeAll
    public void setup() {
        conn = Database.getConnection();
        availabilityDAO = new WeeklyAvailabilityDAOImpl(conn);
    }

    @BeforeEach
    public void cleanUp() {
        try {
            conn.createStatement().execute("DELETE FROM WeeklyAvailability");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSetAndGetAvailability() {
        String employeeId = "e001";
        String date = "2025-06-01";
        String shiftTime = "Morning";

        availabilityDAO.setAvailability(employeeId, date, shiftTime);
        List<String> available = availabilityDAO.getAvailableEmployees(date, shiftTime);

        assertNotNull(available);
        assertTrue(available.contains(employeeId));
    }

    @Test
    public void testClearAvailability() {
        String employeeId = "e002";
        String date = "2025-06-02";
        String shiftTime = "Evening";

        availabilityDAO.setAvailability(employeeId, date, shiftTime);
        availabilityDAO.clearAvailability(employeeId);

        List<String> afterClear = availabilityDAO.getAvailableEmployees(date, shiftTime);
        assertFalse(afterClear.contains(employeeId));
    }
}
