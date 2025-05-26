package HR.DataAccess;

import HR.DataAccess.WeeklyAvailabilityDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WeeklyAvailabilityDAOImpl implements WeeklyAvailabilityDAO {

    private final Connection connection;

    public WeeklyAvailabilityDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void setAvailability(String employeeId, String date, String shiftTime) {
        String sql = "INSERT INTO weekly_availability (employee_id, available_date, shift_time) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            stmt.setString(2, date);
            stmt.setString(3, shiftTime);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Set availability failed", e);
        }
    }

    @Override
    public void clearAvailability(String employeeId) {
        String sql = "DELETE FROM weekly_availability WHERE employee_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Clear availability failed", e);
        }
    }

    @Override
    public List<String> getAvailableEmployees(String date, String shiftTime) {
        List<String> employees = new ArrayList<>();
        String sql = "SELECT employee_id FROM weekly_availability WHERE available_date = ? AND shift_time = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, date);
            stmt.setString(2, shiftTime);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                employees.add(rs.getString("employee_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Get available employees failed", e);
        }
        return employees;
    }
}
