package HR.DataAccess;

import HR.DataAccess.SwapDAO;
import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.SwapRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    private SwapRequest map(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        Employee employee = EmployeesRepo.getInstance().getEmployeeById(rs.getString("requester_id"));
        Shift shift = ShiftsRepo.getInstance().getShiftByID(rs.getString("shift_id"));
        Role role = RolesRepo.getInstance().getRoleByName(rs.getString("role_name"));

        return new SwapRequest(id, employee, shift, role);
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
}
