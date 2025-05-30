package HR.DataAccess;

import HR.Domain.DriverInfo;
import Util.Database;

import java.sql.*;

public class DriverInfoDAOImpl implements DriverInfoDAO {

    @Override
    public void insert(DriverInfo info) {
        String sql = "INSERT INTO driver_info (employee_id, license_type) VALUES (?, ?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, info.getEmployeeId());
            ps.setString(2, info.getLicenseType());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert DriverInfo: " + e.getMessage(), e);
        }
    }

    @Override
    public DriverInfo getByEmployeeId(String employeeId) {
        String sql = "SELECT * FROM driver_info WHERE employee_id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, employeeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new DriverInfo(
                        rs.getString("employee_id"),
                        rs.getString("license_type")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch DriverInfo: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(DriverInfo info) {
        String sql = "UPDATE driver_info SET license_type = ? WHERE employee_id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, info.getLicenseType());
            ps.setString(2, info.getEmployeeId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update DriverInfo: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String employeeId) {
        String sql = "DELETE FROM driver_info WHERE employee_id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, employeeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete DriverInfo: " + e.getMessage(), e);
        }
    }
}
