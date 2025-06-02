package Transportation.DataAccess;

import Transportation.DTO.DriverAvailabilityDTO;
import Transportation.DTO.TransportationTaskDTO;
import Util.Database;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class SqliteTransportationTaskDAO implements TransportationTaskDAO {
    private final SqliteSiteDAO siteDAO = new SqliteSiteDAO();

    @Override
    public TransportationTaskDTO insert(TransportationTaskDTO task) throws SQLException {
        // INSERT
        String sql = """
                    INSERT INTO transportation_tasks(task_date, departure_time, source_site_id, driver_id, truck_license_number, weight_before_leaving)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = Database.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, task.taskDate().toString());
            ps.setString(2, task.departureTime().toString());
            ps.setInt(3, getSiteIdByAddress(task.sourceSiteAddress()));
            ps.setString(4, "");
            ps.setString(5, "");
            ps.setFloat(6, -1);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new TransportationTaskDTO(keys.getInt(1), task.taskDate(), task.departureTime(), task.sourceSiteAddress(), new ArrayList<>(), "", "", -1);
            }
        }
    }

    @Override
    public void delete(int taskId) throws SQLException {
        String sql = "DELETE FROM transportation_tasks WHERE task_id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<TransportationTaskDTO> findById(int taskId) throws SQLException {
        String sql = """
                    SELECT t.task_id, t.task_date, t.departure_time, t.driver_id, t.truck_license_number,
                           t.weight_before_leaving, s.address AS source_address
                    FROM transportation_tasks t
                    JOIN sites s ON t.source_site_id = s.site_id
                    WHERE t.task_id = ?
                """;

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(buildDTOFromResultSet(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<TransportationTaskDTO> findByDateTimeAndSource(LocalDate taskDate, LocalTime departureTime, int sourceSiteId) throws SQLException {
        String sql = """
                    SELECT t.*, s.address AS source_address
                    FROM transportation_tasks t
                    JOIN sites s ON t.source_site_id = s.site_id
                    WHERE t.task_date = ? AND t.departure_time = ? AND t.source_site_id = ?
                """;

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, taskDate.toString());
            ps.setString(2, departureTime.toString());
            ps.setInt(3, sourceSiteId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(buildDTOFromResultSet(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<TransportationTaskDTO> findAll() throws SQLException {
        String sql = """
                    SELECT t.*, s.address AS source_address
                    FROM transportation_tasks t
                    JOIN sites s ON t.source_site_id = s.site_id
                    ORDER BY t.task_id
                """;

        List<TransportationTaskDTO> list = new ArrayList<>();
        try (Statement st = Database.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(buildDTOFromResultSet(rs));
            }
        }

        return list;
    }

    @Override
    public List<TransportationTaskDTO> findBySourceAddress(int sourceSiteId) throws SQLException {
        String sql = """
                    SELECT t.*, s.address AS source_address
                    FROM transportation_tasks t
                    JOIN sites s ON t.source_site_id = s.site_id
                    WHERE t.source_site_id = ?
                """;

        List<TransportationTaskDTO> list = new ArrayList<>();
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, sourceSiteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildDTOFromResultSet(rs));
                }
            }
        }

        return list;
    }

    @Override
    public List<TransportationTaskDTO> findByDriverId(String driverId) throws SQLException {
        String sql = """
                    SELECT t.*, s.address AS source_address
                    FROM transportation_tasks t
                    JOIN sites s ON t.source_site_id = s.site_id
                    WHERE driver_id = ?
                """;

        List<TransportationTaskDTO> list = new ArrayList<>();
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, driverId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildDTOFromResultSet(rs));
                }
            }
        }

        return list;
    }

    public boolean hasDestination(int taskId, int siteId) throws SQLException {
        String sql = """
                    SELECT 1 FROM transportation_task_destinations
                    WHERE task_id = ? AND site_id = ?
                    LIMIT 1
                """;

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ps.setInt(2, siteId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true if any row found
            }
        }
    }

    @Override
    public TransportationTaskDTO addDestination(int taskId, int destinationSiteId) throws SQLException {
        String sql = "INSERT INTO transportation_task_destinations(task_id, site_id) VALUES (?, ?)";

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ps.setInt(2, destinationSiteId);
            ps.executeUpdate();
        }

        return findById(taskId)
                .orElseThrow(() -> new SQLException("Failed to retrieve updated task with id: " + taskId));
    }

    @Override
    public TransportationTaskDTO updateWeight(int taskId, float weight) throws SQLException {
        String sql = "UPDATE transportation_tasks SET weight_before_leaving = ? WHERE task_id = ?";

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setFloat(1, weight);
            ps.setInt(2, taskId);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("No task found with ID: " + taskId);
            }
        }

        // Return the updated task
        return findById(taskId)
                .orElseThrow(() -> new SQLException("Task not found after updating weight"));
    }

    public TransportationTaskDTO assignTruck(int taskId, String truckLicenseNumber) throws SQLException {
        String sql = "UPDATE transportation_tasks SET truck_license_number = ? WHERE task_id = ?";

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, truckLicenseNumber);
            ps.setInt(2, taskId);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("No task found with ID: " + taskId);
            }
        }

        // Return the updated task
        return findById(taskId)
                .orElseThrow(() -> new SQLException("Task not found"));
    }

    @Override
    public TransportationTaskDTO assignDriver(int taskId, String driverId) throws SQLException {
        String sql = "UPDATE transportation_tasks SET driver_id = ? WHERE task_id = ?";

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, driverId);
            ps.setInt(2, taskId);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("No task found with ID: " + taskId);
            }
        }
        // Return the updated task
        return findById(taskId)
                .orElseThrow(() -> new SQLException("Task not found"));
    }

    public DriverAvailabilityDTO addOccupiedDriver(String shiftId, String driverId) throws SQLException {
        String sql = "INSERT INTO drivers_in_tasks(shift_id, driver_id) VALUES (?, ?)";

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, shiftId);
            ps.setString(2, driverId);
            ps.executeUpdate();
        }

        return new DriverAvailabilityDTO(shiftId, driverId);
    }

    public void removeOccupiedDriver(String shiftId, String driverId) throws SQLException {
        String sql = "DELETE FROM drivers_in_tasks WHERE shift_id = ? AND driver_id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, shiftId);
            ps.setString(2, driverId);
            ps.executeUpdate();
        }
    }

    @Override
    public boolean hasOccupiedDriver(String shiftId, String driverId) throws SQLException {
        String sql = "SELECT 1 FROM drivers_in_tasks WHERE shift_id = ? AND driver_id = ?";
        try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
            stmt.setString(1, shiftId);
            stmt.setString(2, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // returns true if a row exists
            }
        }
    }


// ----------- Helper Methods -----------

    private void insertDestinations(int taskId, List<String> destinations) throws SQLException {
        String sql = "INSERT INTO transportation_task_destinations(task_id, site_id) VALUES (?, ?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            for (String address : destinations) {
                int siteId = getSiteIdByAddress(address);
                ps.setInt(1, taskId);
                ps.setInt(2, siteId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteDestinations(int taskId) throws SQLException {
        String sql = "DELETE FROM transportation_task_destinations WHERE task_id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ps.executeUpdate();
        }
    }

    private List<String> getDestinationAddresses(int taskId) throws SQLException {
        String sql = """
                    SELECT s.address
                    FROM transportation_task_destinations d
                    JOIN sites s ON d.site_id = s.site_id
                    WHERE d.task_id = ?
                """;

        List<String> list = new ArrayList<>();
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("address"));
                }
            }
        }
        return list;
    }

    private TransportationTaskDTO buildDTOFromResultSet(ResultSet rs) throws SQLException {
        int taskId = rs.getInt("task_id");
        return new TransportationTaskDTO(
                taskId,
                LocalDate.parse(rs.getString("task_date")),
                LocalTime.parse(rs.getString("departure_time")),
                rs.getString("source_site_id"),
                getDestinationAddresses(taskId),
                rs.getString("driver_id"),
                rs.getString("truck_license_number"),
                rs.getFloat("weight_before_leaving")
        );
    }

    private int getSiteIdByAddress(String address) throws SQLException {
        return siteDAO.findByAddress(address)
                .orElseThrow(() -> new SQLException("Site address not found: " + address))
                .siteId();
    }
}