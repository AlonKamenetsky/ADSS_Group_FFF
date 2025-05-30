package Transportation.Tests.DB;

import Transportation.DataAccess.SqliteSiteDAO;
import Transportation.DTO.SiteDTO;
import Util.Database;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

////import static java.lang.foreign.MemorySegment.NULL;
//import static java.sql.JDBCType.INTEGER;
//import static SSjdk.incubator.vector.VectorOperators.NOT;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SqliteSiteDAOTest {

    private SqliteSiteDAO siteDAO;

    @BeforeAll
    void init() throws SQLException {
        Connection conn = Database.getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS sites");
            stmt.execute("stmt.execute(\n" +
                    "    \"CREATE TABLE sites (\" +\n" +
                    "    \"site_id INTEGER PRIMARY KEY AUTOINCREMENT, \" +\n" +
                    "    \"address TEXT NOT NULL, \" +\n" +
                    "    \"contact_name TEXT NOT NULL, \" +\n" +
                    "    \"phone_number TEXT NOT NULL, \" +\n" +
                    "    \"zone_id INTEGER NOT NULL)\"\n" +
                    ");");
        }
        siteDAO = new SqliteSiteDAO();
    }

    @BeforeEach
    void resetTable() throws SQLException {
        try (Statement stmt = Database.getConnection().createStatement()) {
            stmt.execute("DELETE FROM sites");
        }
    }

    @Test
    void insertAndFindById() throws SQLException {
        SiteDTO inserted = siteDAO.insert(new SiteDTO(0, "123 Main", "Alice", "050-1111111", 1));
        Optional<SiteDTO> found = siteDAO.findById(inserted.siteId());

        assertTrue(found.isPresent());
        assertEquals("123 Main", found.get().siteAddress());
    }

    @Test
    void findAll_ReturnsAllSites() throws SQLException {
        siteDAO.insert(new SiteDTO(0, "Site A", "Bob", "050-2222222", 2));
        siteDAO.insert(new SiteDTO(0, "Site B", "Carol", "050-3333333", 2));

        List<SiteDTO> sites = siteDAO.findAll();
        assertEquals(2, sites.size());
    }

    @Test
    void findByAddress_ReturnsCorrectSite() throws SQLException {
        siteDAO.insert(new SiteDTO(0, "Unique Address", "Dave", "050-4444444", 3));
        Optional<SiteDTO> site = siteDAO.findByAddress("Unique Address");

        assertTrue(site.isPresent());
        assertEquals("Dave", site.get().contactName());
    }

    @Test
    void findAllByZoneId_ReturnsCorrectSites() throws SQLException {
        siteDAO.insert(new SiteDTO(0, "Z1", "Eve", "050-5555555", 4));
        siteDAO.insert(new SiteDTO(0, "Z2", "Frank", "050-6666666", 4));
        siteDAO.insert(new SiteDTO(0, "Z3", "Grace", "050-7777777", 5));

        List<SiteDTO> sites = siteDAO.findAllByZoneId(4);
        assertEquals(2, sites.size());
    }

    @Test
    void delete_RemovesSite() throws SQLException {
        SiteDTO inserted = siteDAO.insert(new SiteDTO(0, "ToDelete", "Hank", "050-8888888", 6));
        siteDAO.delete(inserted.siteId());
        Optional<SiteDTO> result = siteDAO.findById(inserted.siteId());
        assertTrue(result.isEmpty());
    }

    @AfterAll
    void cleanup() throws SQLException {
        try (Statement stmt = Database.getConnection().createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS sites");
        }
    }
}

