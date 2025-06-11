package Transportation.Tests.DB;

import Transportation.DataAccess.SqliteTransportationTaskDAO;
import Transportation.DataAccess.SqliteSiteDAO;


import Transportation.DTO.TransportationTaskDTO;
import Transportation.DTO.SiteDTO;
import Util.Database;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SqliteTaskDAOTest {

    private SqliteTransportationTaskDAO taskDAO;
    private SqliteSiteDAO siteDAO;

    @BeforeAll
    void init() throws SQLException {
        Connection conn = Database.getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS transportation_task_destinations");
            stmt.execute("DROP TABLE IF EXISTS transportation_tasks");
            stmt.execute("DROP TABLE IF EXISTS sites");
            stmt.execute("CREATE TABLE sites (" +
                    "site_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "address TEXT NOT NULL, " +
                    "contact_name TEXT NOT NULL, " +
                    "phone_number TEXT NOT NULL, " +
                    "zone_id INTEGER NOT NULL)");
            stmt.execute("CREATE TABLE transportation_tasks (" +
                    "task_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "task_date TEXT NOT NULL, " +
                    "departure_time TEXT NOT NULL, " +
                    "source_site_address TEXT NOT NULL, " +
                    "driver_id TEXT, " +
                    "truck_license_number TEXT, " +
                    "weight_before_leaving REAL, " +
                    "FOREIGN KEY(source_site_address) REFERENCES sites(address))");
            stmt.execute("CREATE TABLE transportation_task_destinations (" +
                    "task_id INTEGER NOT NULL, " +
                    "site_id INTEGER NOT NULL)");
        }
        siteDAO = new SqliteSiteDAO();
        taskDAO = new SqliteTransportationTaskDAO();
    }

    @BeforeEach
    void resetTables() throws SQLException {
        try (Statement stmt = Database.getConnection().createStatement()) {
            stmt.execute("DELETE FROM transportation_task_destinations");
            stmt.execute("DELETE FROM transportation_tasks");
            stmt.execute("DELETE FROM sites");
        }
    }

    @Test
    void insertAndFindTask() throws SQLException {
        SiteDTO site = siteDAO.insert(new SiteDTO(0, "Warehouse A", "John", "050-0000000", 1));
        TransportationTaskDTO newTask = new TransportationTaskDTO(0, LocalDate.now(), LocalTime.of(10, 0), site.siteAddress(), List.of(), "", "", -1);
        TransportationTaskDTO inserted = taskDAO.insert(newTask);

        Optional<TransportationTaskDTO> retrieved = taskDAO.findById(inserted.taskId());
        assertTrue(retrieved.isPresent());
        assertEquals(inserted.taskId(), retrieved.get().taskId());
    }

    @Test
    void findAllTasks_ReturnsCorrectAmount() throws SQLException {
        SiteDTO site = siteDAO.insert(new SiteDTO(0, "Depot", "Anna", "050-9999999", 2));
        taskDAO.insert(new TransportationTaskDTO(0, LocalDate.now(), LocalTime.of(9, 0), site.siteAddress(), List.of(), "", "", -1));
        taskDAO.insert(new TransportationTaskDTO(0, LocalDate.now(), LocalTime.of(11, 0), site.siteAddress(), List.of(), "", "", -1));

        List<TransportationTaskDTO> allTasks = taskDAO.findAll();
        assertEquals(2, allTasks.size());
    }

    @Test
    void deleteTask_RemovesTask() throws SQLException {
        SiteDTO site = siteDAO.insert(new SiteDTO(0, "Temp Site", "Eve", "050-8888888", 3));
        TransportationTaskDTO task = taskDAO.insert(new TransportationTaskDTO(0, LocalDate.now(), LocalTime.of(12, 0), site.siteAddress(), List.of(), "", "", -1));
        taskDAO.delete(task.taskId());

        Optional<TransportationTaskDTO> result = taskDAO.findById(task.taskId());
        assertTrue(result.isEmpty());
    }

    @AfterAll
    void cleanup() throws SQLException {
        try (Statement stmt = Database.getConnection().createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS transportation_task_destinations");
            stmt.execute("DROP TABLE IF EXISTS transportation_tasks");
            stmt.execute("DROP TABLE IF EXISTS sites");
        }
    }
}