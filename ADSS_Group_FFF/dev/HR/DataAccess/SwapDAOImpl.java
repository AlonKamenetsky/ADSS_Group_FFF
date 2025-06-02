
package HR.DataAccess;

import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.SwapRequest;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class SwapDAOImpl implements SwapDAO {

    private final Connection connection;

    public SwapDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(SwapRequest request) {
        String sql = "INSERT INTO swap_requests (requester_id, shift_id, role_name) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, request.getEmployee().getId());
            stmt.setString(2, request.getShift().getID());
            stmt.setString(3, request.getRole().getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Insert swap request failed", e);
        }
    }

    @Override
    public void delete(int requestId) {
        String sql = "DELETE FROM swap_requests WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Delete swap request failed", e);
        }
    }

    @Override
    public List<SwapRequest> selectAll() {
        List<SwapRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM swap_requests";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fetch swap requests failed", e);
        }
        return list;
    }

    @Override
    public SwapRequest selectById(int requestId) {
        String sql = "SELECT * FROM swap_requests WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Select swap request by ID failed", e);
        }
        return null;
    }

    private SwapRequest map(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String employeeId = rs.getString("requester_id");
        String shiftId = rs.getString("shift_id");
        String roleName = rs.getString("role_name");

        // Lightweight placeholders
        Employee employee = new Employee(employeeId, null, null, null, null, 0f, new Date());
        Shift shift = new Shift(shiftId, new Date(), Shift.ShiftTime.Morning, new HashMap<>(), new HashMap<>());
        Role role = new Role(roleName);

        return new SwapRequest(id, employee, shift, role);
    }
}
