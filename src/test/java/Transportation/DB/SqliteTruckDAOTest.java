package Transportation.Tests.DB;

import Transportation.DataAccess.SqliteTruckDAO;
import Transportation.DTO.TruckDTO;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SqliteTruckDAOTest {

    private SqliteTruckDAO truckDAO;

    @BeforeEach
    void setUp() throws SQLException {
        truckDAO = new SqliteTruckDAO();
        clearTrucksTable();
    }

    void clearTrucksTable() throws SQLException {
        try (Statement st = Util.Database.getConnection().createStatement()) {
            st.executeUpdate("DELETE FROM trucks");
        }
    }


    @Test
    void insert_newTruck_insertsAndReturnsWithId() throws SQLException {
        TruckDTO truck = new TruckDTO(null, "B", "123-ABC", "Volvo", 5000f, 8000f, true);
        TruckDTO inserted = truckDAO.insert(truck);

        assertNotNull(inserted.truckId());
        assertEquals("Volvo", inserted.model());
        assertEquals("123-ABC", inserted.licenseNumber());
    }

    @Test
    void insert_existingTruck_updatesSuccessfully() throws SQLException {
        TruckDTO original = truckDAO.insert(new TruckDTO(null, "B", "999-XYZ", "MAN", 4000f, 9000f, true));
        TruckDTO updated = new TruckDTO(original.truckId(), "C", "999-XYZ", "MAN-Updated", 4500f, 9500f, false);

        TruckDTO result = truckDAO.insert(updated);

        assertEquals(original.truckId(), result.truckId());
        assertEquals("MAN-Updated", result.model());
        assertFalse(result.isFree());
    }

    @Test
    void findByLicense_existingTruck_returnsTruck() throws SQLException {
        TruckDTO truck = truckDAO.insert(new TruckDTO(null, "A", "456-DEF", "Scania", 3000f, 7000f, true));
        Optional<TruckDTO> result = truckDAO.findByLicense("456-DEF");

        assertTrue(result.isPresent());
        assertEquals(truck.truckId(), result.get().truckId());
    }

    @Test
    void findByLicense_nonExistingTruck_returnsEmpty() throws SQLException {
        Optional<TruckDTO> result = truckDAO.findByLicense("non-existent");
        assertTrue(result.isEmpty());
    }

    @Test
    void setAvailability_changesStatusCorrectly() throws SQLException {
        TruckDTO truck = truckDAO.insert(new TruckDTO(null, "B", "777-GGG", "Iveco", 3200f, 6700f, true));
        truckDAO.setAvailability(truck.truckId(), false);

        Optional<TruckDTO> updated = truckDAO.findTruckById(truck.truckId());
        assertTrue(updated.isPresent());
        assertFalse(updated.get().isFree());
    }

    @Test
    void delete_removesTruck() throws SQLException {
        TruckDTO truck = truckDAO.insert(new TruckDTO(null, "C", "DEL-001", "DAF", 3300f, 7500f, true));
        truckDAO.delete(truck.truckId());

        Optional<TruckDTO> result = truckDAO.findTruckById(truck.truckId());
        assertTrue(result.isEmpty());
    }
}
