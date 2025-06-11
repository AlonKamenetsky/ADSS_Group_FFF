package HR.DataAccess;

import HR.Domain.DriverInfo;
import Util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverInfoDAOImpl implements DriverInfoDAO {

    private final Connection connection;

    public DriverInfoDAOImpl(Connection connection) {
        this.connection = connection;
    }

/*
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
    }*/

    @Override
    public void insert(DriverInfo info) {
        String sql = "INSERT INTO driver_info (employee_id, license_type) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (DriverInfo.LicenseType license : info.getLicenses()) {
                ps.setString(1, info.getEmployeeId());
                ps.setString(2, license.name());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert DriverInfo: " + e.getMessage(), e);
        }
    }

    @Override
    public DriverInfo getByEmployeeId(String employeeId) {
        String sql = "SELECT license_type FROM driver_info WHERE employee_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            ResultSet rs = ps.executeQuery();
            List<DriverInfo.LicenseType> licenses = new ArrayList<>();
            while (rs.next()) {
                licenses.add(DriverInfo.LicenseType.valueOf(rs.getString("license_type")));
            }
            if (licenses.isEmpty()) return null;
            return new DriverInfo(employeeId, licenses);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch DriverInfo: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(DriverInfo info) {
        // Remove all old licenses, then insert new ones
        delete(info.getEmployeeId());
        insert(info);
    }

    @Override
    public void delete(String employeeId) {
        String sql = "DELETE FROM driver_info WHERE employee_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete DriverInfo: " + e.getMessage(), e);
        }
    }
}
