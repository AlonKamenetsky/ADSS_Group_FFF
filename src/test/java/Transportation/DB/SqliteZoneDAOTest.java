package Transportation.Tests.DB;

import Transportation.DataAccess.SqliteZoneDAO;

import Transportation.DTO.ZoneDTO;
import Util.Database;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SqliteZoneDAOTest {

    private SqliteZoneDAO zoneDAO;

    @BeforeAll
    void setupDatabase() throws SQLException {
        Connection conn = Database.getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS zones");
            stmt.execute("CREATE TABLE zones (" +
                    "zone_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "zone_name TEXT NOT NULL UNIQUE)");
            stmt.execute("DROP TABLE IF EXISTS sites");
            stmt.execute("CREATE TABLE sites (" +
                    "site_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "address TEXT NOT NULL, " +
                    "contact_name TEXT NOT NULL, " +
                    "phone_number TEXT NOT NULL, " +
                    "zone_id INTEGER NOT NULL)");
        }
        zoneDAO = new SqliteZoneDAO();
    }

    @BeforeEach
    void clearZones() throws SQLException {
        try (Statement stmt = Database.getConnection().createStatement()) {
            stmt.execute("DELETE FROM sites");
            stmt.execute("DELETE FROM zones");
        }
    }

    @Test
    void insertZone_Success() throws SQLException {
        ZoneDTO zone = new ZoneDTO(0, "Center", new ArrayList<>());
        ZoneDTO inserted = zoneDAO.insert(zone);

        assertNotEquals(0, inserted.zoneId());
        assertEquals("Center", inserted.zoneName());
    }

    @Test
    void findById_ReturnsZone() throws SQLException {
        ZoneDTO inserted = zoneDAO.insert(new ZoneDTO(0, "North", new ArrayList<>()));
        Optional<ZoneDTO> found = zoneDAO.findById(inserted.zoneId());

        assertTrue(found.isPresent());
        assertEquals("North", found.get().zoneName());
    }

    @Test
    void findById_NotFound() throws SQLException {
        Optional<ZoneDTO> found = zoneDAO.findById(999);
        assertTrue(found.isEmpty());
    }

    @Test
    void findByName_ReturnsZone() throws SQLException {
        zoneDAO.insert(new ZoneDTO(0, "South", new ArrayList<>()));
        Optional<ZoneDTO> found = zoneDAO.findByName("South");

        assertTrue(found.isPresent());
        assertEquals("South", found.get().zoneName());
    }

    @Test
    void findByName_NotFound() throws SQLException {
        Optional<ZoneDTO> found = zoneDAO.findByName("Nowhere");
        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_ReturnsAllZones() throws SQLException {
        zoneDAO.insert(new ZoneDTO(0, "ZoneA", new ArrayList<>()));
        zoneDAO.insert(new ZoneDTO(0, "ZoneB", new ArrayList<>()));

        List<ZoneDTO> allZones = zoneDAO.findAll();
        assertEquals(2, allZones.size());
    }

    @Test
    void deleteZone_RemovesZone() throws SQLException {
        ZoneDTO inserted = zoneDAO.insert(new ZoneDTO(0, "ToRemove", new ArrayList<>()));
        zoneDAO.delete(inserted.zoneId());

        Optional<ZoneDTO> found = zoneDAO.findById(inserted.zoneId());
        assertTrue(found.isEmpty());
    }

    @Test
    void updateZone_ChangesName() throws SQLException {
        ZoneDTO original = zoneDAO.insert(new ZoneDTO(0, "Original", new ArrayList<>()));
        ZoneDTO updated = new ZoneDTO(original.zoneId(), "Updated", original.sitesRelated());

        ZoneDTO result = zoneDAO.update(updated);

        assertEquals("Updated", result.zoneName());

        Optional<ZoneDTO> reloaded = zoneDAO.findById(result.zoneId());
        assertTrue(reloaded.isPresent());
        assertEquals("Updated", reloaded.get().zoneName());
    }

    @AfterAll
    void cleanup() throws SQLException {
        try (Statement stmt = Database.getConnection().createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS sites");
            stmt.execute("DROP TABLE IF EXISTS zones");
        }
    }
}
