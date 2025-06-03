package HR.DataAccess;

import HR.Domain.Shift;
import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Domain.ShiftAssignment;
import HR.Domain.WeeklyShiftsSchedule;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
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
        // Persist assignments + required‐roles immediately
        update(shift);
    }

    @Override
    public void update(Shift shift) {
        // 1) Persist actual “employee assignments”
        deleteAssignments(shift.getID());
        for (ShiftAssignment assignment : shift.getAssignedEmployees()) {
            insertAssignment(assignment);
        }

        // 2) Persist “required role counts” into shift_role_requirements
        deleteRoleRequirements(shift.getID());
        for (Map.Entry<Role, Integer> entry : shift.getRequiredCounts().entrySet()) {
            insertRoleRequirement(shift.getID(),
                    entry.getKey().getName(),
                    entry.getValue());
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

    private void deleteRoleRequirements(String shiftId) {
        String sql = "DELETE FROM shift_role_requirements WHERE shift_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, shiftId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Delete role requirements failed", e);
        }
    }

    private void insertRoleRequirement(String shiftId, String roleName, int requiredCount) {
        String sql = """
            INSERT INTO shift_role_requirements (shift_id, role_name, required_count)
            VALUES (?, ?, ?)
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, shiftId);
            stmt.setString(2, roleName);
            stmt.setInt(3, requiredCount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Insert role requirement failed", e);
        }
    }

    @Override
    public void delete(String shiftId) {
        deleteAssignments(shiftId);
        deleteRoleRequirements(shiftId);
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
        return selectAll();
    }

    @Override
    public List<Shift> getNextWeekShifts() {
        return List.of();
    }

    @Override
    public Optional<Shift> getCurrentShift() {
        LocalTime now = LocalTime.now();
        Shift.ShiftTime bucket = Shift.fromTime(now);
        if (bucket == null) {
            return Optional.empty();
        }
        LocalDate today = LocalDate.now();
        String shiftId = today.toString() + "-" + bucket.name();
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
        String sql = """
            SELECT e.* 
              FROM employees e
              JOIN shift_assignments sa ON e.id = sa.employee_id
             WHERE sa.shift_id = ?
        """;
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

    @Override
    public boolean isWarehouseEmployeeAssigned(String shiftId) {
        String sql = """
            SELECT 1 
              FROM shift_assignments 
             WHERE shift_id = ? 
               AND role_name = 'Warehouse' 
             LIMIT 1
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, shiftId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Check warehouse role failed", e);
        }
    }

    private Shift mapToShift(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        Date   date = rs.getDate("date");
        Shift.ShiftTime time = Shift.ShiftTime.valueOf(rs.getString("time"));

        // 1) Load “required role counts” from shift_role_requirements:
        Map<Role, Integer> requiredCounts = new HashMap<>();
        Map<Role, ArrayList<Employee>> requiredRoles = new HashMap<>();

        String sqlReq = """
            SELECT role_name, required_count 
              FROM shift_role_requirements 
             WHERE shift_id = ?
        """;
        try (PreparedStatement psReq = connection.prepareStatement(sqlReq)) {
            psReq.setString(1, id);
            ResultSet rsReq = psReq.executeQuery();
            while (rsReq.next()) {
                String roleName = rsReq.getString("role_name");
                int    count    = rsReq.getInt("required_count");
                Role   r        = new Role(roleName);
                requiredCounts.put(r, count);
                // start with empty list—will fill in actual assigned employees next
                requiredRoles.put(r, new ArrayList<>());
            }
        }

        // 2) Load “actual assignments,” then populate requiredRoles
        List<ShiftAssignment> assignments = loadAssignments(id);
        for (ShiftAssignment sa : assignments) {
            Role role = sa.getRole();
            Employee emp = mapToEmployeeFromId(sa.getEmployeeId());
            // If that role wasn't in the requirements table (edge‐case), still put it:
            requiredRoles.computeIfAbsent(role, r -> new ArrayList<>()).add(emp);
            // (We do NOT override requiredCounts here; requiredCounts are from the requirement table.)
        }

        // 3) Build the Shift domain object
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
        String sql = """
            SELECT s.*
              FROM shifts s
              JOIN shift_assignments sa ON s.id = sa.shift_id
             WHERE sa.employee_id = ?
        """;
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
        String sql = "SELECT id FROM shifts WHERE date = ? AND time = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(date.getTime()));
            stmt.setString(2, time);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("id");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve shift ID", e);
        }
        return null;
    }
}
