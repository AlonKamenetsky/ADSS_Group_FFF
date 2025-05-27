package HR.DataAccess;

import HR.Domain.Employee;
import HR.Domain.Shift;
import HR.Domain.SwapRequest;
import HR.Presentation.PresentationUtils;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class ShiftDAOImpl implements ShiftDAO {

    private final Connection connection;

    public ShiftDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Shift shift) {
        String sql = "INSERT INTO shifts (id, date, time, ...) VALUES (?, ?, ?, ...)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, shift.getID());
            stmt.setDate(2, Date.valueOf(shift.getDateString()));
            // set other fields...
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Insert failed", e);
        }
    }

    @Override
    public void update(Shift shift) {
        String sql = "UPDATE shifts SET date = ?, time = ?, ... WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(2, Date.valueOf(shift.getDateString()));
            // other fields...
            stmt.setString(1, shift.getID()); // replace n with correct index
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Update failed", e);
        }
    }

    @Override
    public void delete(String shiftId) {
        String sql = "DELETE FROM shifts WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, shiftId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Delete failed", e);
        }
    }

    @Override
    public Shift selectById(String shiftId) {
        String sql = "SELECT * FROM shifts WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, shiftId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapToShift(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Select failed", e);
        }
        return null;
    }

    @Override
    public List<Shift> selectAll() {
        List<Shift> shifts = new ArrayList<>();
        String sql = "SELECT * FROM shifts";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                shifts.add(mapToShift(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Select all failed", e);
        }
        return shifts;
    }

    @Override
    public List<Shift> getShiftsByDate(LocalDate date) {
        List<Shift> shifts = new ArrayList<>();
        String sql = "SELECT * FROM shifts WHERE date = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                shifts.add(mapToShift(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Query failed", e);
        }
        return shifts;
    }

    @Override
    public List<Shift> getCurrentWeekShifts() {
        List<Shift> shifts = new ArrayList<>();
        String sql = "SELECT * FROM shifts WHERE YEARWEEK(date, 1) = YEARWEEK(CURDATE(), 1)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                shifts.add(mapToShift(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Query failed", e);
        }
        return shifts;
    }

    @Override
    public List<Employee> findAssignedEmployees(String shiftId) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.* FROM employees e " +
                "JOIN shift_assignments sa ON e.id = sa.employee_id " +
                "WHERE sa.shift_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, shiftId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                employees.add(mapToEmployee(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Query failed", e);
        }
        return employees;
    }

    // Utility methods to map ResultSet ➝ domain objects
    private Shift mapToShift(ResultSet rs) throws SQLException {
        // return new Shift(...); // extract fields from rs
        return null; // implement later
    }

    private Employee mapToEmployee(ResultSet rs) throws SQLException {
        // return new Employee(...); // extract fields from rs
        return null; // implement later
    }


}
