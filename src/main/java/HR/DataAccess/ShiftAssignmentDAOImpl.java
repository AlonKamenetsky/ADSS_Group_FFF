package HR.DataAccess;

import HR.DataAccess.ShiftAssignmentDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShiftAssignmentDAOImpl implements ShiftAssignmentDAO {

    private final Connection connection;

    public ShiftAssignmentDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void assignEmployeeToShift(String shiftId, String employeeId, String roleName) {
        String sql = "INSERT INTO shift_assignments (shift_id, employee_id, role_name) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, shiftId);
            stmt.setString(2, employeeId);
            stmt.setString(3, roleName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Shift assignment failed", e);
        }
    }

    @Override
    public void removeAssignment(String shiftId, String employeeId, String roleName) {
        String sql = "DELETE FROM shift_assignments WHERE shift_id = ? AND employee_id = ? AND role_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, shiftId);
            stmt.setString(2, employeeId);
            stmt.setString(3, roleName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Remove assignment failed", e);
        }
    }

    @Override
    public List<String> getEmployeesAssignedToShift(String shiftId) {
        List<String> result = new ArrayList<>();
        String sql = "SELECT employee_id FROM shift_assignments WHERE shift_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, shiftId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("employee_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fetch assigned employees failed", e);
        }
        return result;
    }
}
