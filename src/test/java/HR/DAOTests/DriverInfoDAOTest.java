package HR.tests.DAOTests;

import HR.DataAccess.DriverInfoDAO;
import HR.DataAccess.DriverInfoDAOImpl;
import HR.Domain.DriverInfo;
import HR.Domain.DriverInfo.LicenseType;
import Util.Database;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DriverInfoDAOTest {

    private DriverInfoDAO driverInfoDAO;
    private Connection conn;

    @BeforeAll
    public void setup() {
        conn = Database.getConnection();
        driverInfoDAO = new DriverInfoDAOImpl(conn);
    }

    @BeforeEach
    public void cleanUp() {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM DriverInfo"); // adjust table name if different
        } catch (Exception e) {
            fail("Failed to clean DriverInfo table: " + e.getMessage());
        }
    }

    @Test
    public void testInsertAndGetDriverInfo() {
        String empId = "EMP123";
        List<LicenseType> licenses = List.of(LicenseType.B, LicenseType.C);
        DriverInfo info = new DriverInfo(empId, licenses);

        driverInfoDAO.insert(info);
        DriverInfo loaded = driverInfoDAO.getByEmployeeId(empId);

        assertNotNull(loaded);
        assertEquals(empId, loaded.getEmployeeId());
        assertEquals(licenses.size(), loaded.getLicenses().size());
        assertTrue(loaded.getLicenses().containsAll(licenses));
    }

    @Test
    public void testUpdateDriverInfo() {
        String empId = "EMP124";
        driverInfoDAO.insert(new DriverInfo(empId, List.of(LicenseType.B)));

        DriverInfo updated = new DriverInfo(empId, List.of(LicenseType.C1));
        driverInfoDAO.update(updated);

        DriverInfo result = driverInfoDAO.getByEmployeeId(empId);
        assertNotNull(result);
        assertEquals(1, result.getLicenses().size());
        assertTrue(result.getLicenses().contains(LicenseType.C1));
    }

    @Test
    public void testDeleteDriverInfo() {
        String empId = "EMP125";
        driverInfoDAO.insert(new DriverInfo(empId, List.of(LicenseType.C)));
        driverInfoDAO.delete(empId);

        DriverInfo deleted = driverInfoDAO.getByEmployeeId(empId);
        assertNull(deleted);
    }
}
