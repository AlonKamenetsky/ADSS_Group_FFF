package HR.DataAccess;

import HR.Domain.Shift;
import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Domain.ShiftAssignment;
import HR.Domain.WeeklyShiftsSchedule;

import java.sql.*;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class ShiftDAOImpl implements ShiftDAO {

    private final Connection connection;

    public ShiftDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Shift shift) {
        String sql = "INSERT INTO shifts (id, date, time) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, shift.getID());
            stmt.setDate(2, new java.sql.Date(shift.getDate().getTime()));
            stmt.setString(3, shift.getType().name());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Insert failed", e);
        }
        update(shift); // Persist assignments immediately
    }

    @Override
    public void update(Shift shift) {
        deleteAssignments(shift.getID());
        for (ShiftAssignment assignment : shift.getAssignedEmployees()) {
            insertAssignment(assignment);
        }
    }

    private void insertAssignment(ShiftAssignment assignment) {
        String sql = "INSERT INTO shift_assignments (shift_id, employee_id, role_name) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, assignment.getShiftId());
            stmt.setString(2, assignment.getEmployeeId());
            stmt.setString(3, assignment.getRole().getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Insert assignment failed", e);
        }
    }

    private void deleteAssignments(String shiftId) {
        String sql = "DELETE FROM shift_assignments WHERE shift_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, shiftId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Delete assignments failed", e);
        }
    }

    @Override
    public void delete(String shiftId) {
        deleteAssignments(shiftId);
        String sql = "DELETE FROM shifts WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, shiftId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Delete shift failed", e);
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
            stmt.setDate(1, java.sql.Date.valueOf(date));
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
        // Compute Monday and Sunday of the current week
        LocalDate today = LocalDate.now();
        // find the Monday of this week:
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        // find the Sunday of this week:
        LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        return getShiftsInRange(monday, sunday);
    }

    @Override
    public List<Shift> getNextWeekShifts() {
        // Compute Monday and Sunday of next week
        LocalDate today = LocalDate.now();
        LocalDate nextWeekMonday = today
                .with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        LocalDate nextWeekSunday = nextWeekMonday
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        return getShiftsInRange(nextWeekMonday, nextWeekSunday);
    }

    /** Helper: return all shifts whose date is between start (inclusive) and end (inclusive). */
    private List<Shift> getShiftsInRange(LocalDate start, LocalDate end) {
        List<Shift> shifts = new ArrayList<>();
        String sql = "SELECT * FROM shifts WHERE date BETWEEN ? AND ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(start));
            stmt.setDate(2, java.sql.Date.valueOf(end));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                shifts.add(mapToShift(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Query shifts in date range failed", e);
        }
        return shifts;
    }

    @Override
    public Optional<Shift> getCurrentShift() {
        // 1) Figure out the "current" ShiftTime bucket (Morning, Evening, or null)
        LocalTime now = LocalTime.now();
        Shift.ShiftTime bucket = Shift.fromTime(now);
        if (bucket == null) {
            // before 08:00 → no active shift
            return Optional.empty();
        }

        // 2) Construct today’s date‐string + bucket name to match the shift‐ID format
        LocalDate today = LocalDate.now();
        String shiftId = today.toString() + "-" + bucket.name();

        // 3) Load that exact shift row (if it exists)
        Shift s = selectById(shiftId);
        return Optional.ofNullable(s);
    }

    @Override
    public WeeklyShiftsSchedule getSchedule() {
        WeeklyShiftsSchedule schedule = new WeeklyShiftsSchedule();
        schedule.getCurrentWeek().addAll(getCurrentWeekShifts());
        return schedule;
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
            throw new RuntimeException("Query assigned employees failed", e);
        }
        return employees;
    }

    public boolean isWarehouseEmployeeAssigned(String shiftId) {
        String sql = """
        SELECT 1
          FROM shift_assignments
         WHERE shift_id = ?
           AND lower(trim(role_name)) = lower(trim(?))
         LIMIT 1
    """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, shiftId);
            stmt.setString(2, "Warehouse");
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Check warehouse role failed", e);
        }
    }

    private Shift mapToShift(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        Date date = rs.getDate("date");
        Shift.ShiftTime time = Shift.ShiftTime.valueOf(rs.getString("time"));

        Map<Role, ArrayList<Employee>> requiredRoles = new HashMap<>();
        Map<Role, Integer> requiredCounts = new HashMap<>();
        List<ShiftAssignment> assignments = loadAssignments(id);

        for (ShiftAssignment sa : assignments) {
            Role role = sa.getRole();
            Employee emp = mapToEmployeeFromId(sa.getEmployeeId());
            requiredRoles.computeIfAbsent(role, r -> new ArrayList<>()).add(emp);
            requiredCounts.put(role, requiredCounts.getOrDefault(role, 0) + 1);
        }

        Shift shift = new Shift(id, date, time, requiredRoles, requiredCounts);
        shift.getAssignedEmployees().addAll(assignments);
        return shift;
    }

    private List<ShiftAssignment> loadAssignments(String shiftId) throws SQLException {
        List<ShiftAssignment> list = new ArrayList<>();
        String sql = "SELECT * FROM shift_assignments WHERE shift_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, shiftId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String empId = rs.getString("employee_id");
                String roleName = rs.getString("role_name");
                Role role = new Role(roleName);
                list.add(new ShiftAssignment(empId, shiftId, role));
            }
        }
        return list;
    }

    private Employee mapToEmployeeFromId(String employeeId) throws SQLException {
        String sql = "SELECT * FROM employees WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapToEmployee(rs);
            }
        }
        return null;
    }

    private Employee mapToEmployee(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String password = rs.getString("password");
        String bankAccount = rs.getString("bank_account");
        Float salary = rs.getFloat("salary");
        Date employmentDate = rs.getDate("employment_date");
        return new Employee(id, new ArrayList<>(), name, password, bankAccount, salary, employmentDate);
    }

    @Override
    public List<Shift> getShiftsByEmployeeId(String employeeId) {
        List<Shift> shifts = new ArrayList<>();
        String sql = "SELECT s.* FROM shifts s " +
                "JOIN shift_assignments sa ON s.id = sa.shift_id " +
                "WHERE sa.employee_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                shifts.add(mapToShift(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Query shifts by employee failed", e);
        }
        return shifts;
    }

    @Override
    public String getShiftIdByDateAndTime(Date date, String time) {
        if (date == null || time == null || time.trim().isEmpty()) {
            return null;
        }

        // 1) Convert incoming java.util.Date (which may include a time-of-day) into a pure LocalDate
        java.time.LocalDate localDate = date.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();

        // 2) Produce a java.sql.Date at midnight for that LocalDate
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);

        // 3) Normalize the 'time' string: trim whitespace, and we’ll do a case-insensitive comparison in SQL
        String normalizedTime = time.trim();

        // 4) Use a case-insensitive match on the "time" column (e.g. “Morning” vs. “morning”)
        String sql = """
        SELECT id
          FROM shifts
         WHERE date = ?
           AND LOWER(time) = LOWER(?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, sqlDate);
            stmt.setString(2, normalizedTime);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve shift ID", e);
        }

        return null;

}
}
