
package HR.tests.DAOTests;

import HR.DataAccess.DriverInfoDAO;
import HR.DataAccess.DriverInfoDAOImpl;
import HR.Domain.DriverInfo;
import HR.Domain.DriverInfo.LicenseType;
import Util.Database;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.util.List;

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
        try {
            conn.createStatement().execute("DELETE FROM DriverInfo"); // adjust if table name differs
        } catch (Exception e) {
            throw new RuntimeException("Failed to clean DriverInfo table", e);
        }
    }

    @Test
    public void testInsertAndGetByEmployeeId() {
        DriverInfo info = new DriverInfo("emp100", List.of(LicenseType.B, LicenseType.C));
        driverInfoDAO.insert(info);

        DriverInfo retrieved = driverInfoDAO.getByEmployeeId("emp100");
        assertNotNull(retrieved);
        assertEquals(2, retrieved.getLicenses().size());
        assertTrue(retrieved.getLicenses().contains(LicenseType.B));
    }

    @Test
    public void testUpdateLicenseTypes() {
        DriverInfo info = new DriverInfo("emp200", List.of(LicenseType.B));
        driverInfoDAO.insert(info);

        info.setLicenses(List.of(LicenseType.C, LicenseType.C1));
        driverInfoDAO.update(info);

        DriverInfo updated = driverInfoDAO.getByEmployeeId("emp200");
        assertEquals(2, updated.getLicenses().size());
        assertTrue(updated.getLicenses().contains(LicenseType.C1));
    }

    @Test
    public void testDeleteDriverInfo() {
        DriverInfo info = new DriverInfo("emp300", List.of(LicenseType.C));
        driverInfoDAO.insert(info);

        driverInfoDAO.delete("emp300");
        assertNull(driverInfoDAO.getByEmployeeId("emp300"));
    }
}
