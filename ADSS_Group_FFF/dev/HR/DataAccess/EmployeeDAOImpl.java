package HR.DataAccess;

import HR.DataAccess.EmployeeDAO;
import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Presentation.PresentationUtils;

import java.sql.*;
import java.sql.Date;
import java.util.*;


public class EmployeeDAOImpl implements EmployeeDAO {

    private final Connection connection;

    public EmployeeDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Employee employee) {
        String sql = "INSERT INTO employees (id, name, password, bank_account, salary, employment_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        String roleSql = "INSERT INTO employee_roles (employee_id, role_name) VALUES (?, ?)";

        try (
                PreparedStatement empStmt = connection.prepareStatement(sql);
                PreparedStatement roleStmt = connection.prepareStatement(roleSql)
        ) {
            setEmployeeFields(empStmt, employee, false);
            empStmt.executeUpdate();

            insertEmployeeRoles(roleStmt, employee);
            PresentationUtils.typewriterPrint("Added employee successfully",20);

        } catch (SQLException e) {
            throw new RuntimeException("Insert employee failed", e);
        }
    }

    @Override
    public void update(Employee employee) {
        String sql = "UPDATE employees SET name = ?, password = ?, bank_account = ?, salary = ?, employment_date = ? WHERE id = ?";
        String deleteRolesSql = "DELETE FROM employee_roles WHERE employee_id = ?";
        String insertRoleSql = "INSERT INTO employee_roles (employee_id, role_name) VALUES (?, ?)";

        try (
                PreparedStatement empStmt = connection.prepareStatement(sql);
                PreparedStatement deleteStmt = connection.prepareStatement(deleteRolesSql);
                PreparedStatement insertStmt = connection.prepareStatement(insertRoleSql)
        ) {
            setEmployeeFields(empStmt, employee, true);
            empStmt.executeUpdate();

            deleteStmt.setString(1, employee.getId());
            deleteStmt.executeUpdate();

            insertEmployeeRoles(insertStmt, employee);
            PresentationUtils.typewriterPrint("Updated employee successfully",20);


        } catch (SQLException e) {
            throw new RuntimeException("Update employee failed", e);
        }
    }

    @Override
    public void delete(String employeeId) {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            stmt.executeUpdate();
            PresentationUtils.typewriterPrint("Removed employee successfully",20);

        } catch (SQLException e) {
            throw new RuntimeException("Delete employee failed", e);
        }
    }

    @Override
    public Employee selectById(String employeeId) {
        String sql = "SELECT * FROM employees WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapToEmployee(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Select by ID failed", e);
        }
        return null;
    }

    @Override
    public List<Employee> selectAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                employees.add(mapToEmployee(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Select all failed", e);
        }
        return employees;
    }

    // 🔄 Utility Methods

    private void setEmployeeFields(PreparedStatement stmt, Employee e, boolean isUpdate) throws SQLException {
        stmt.setString(1, e.getName());
        stmt.setString(2, e.getPassword());
        stmt.setString(3, e.getBankAccount());
        stmt.setFloat(4, e.getSalary());
        stmt.setDate(5, new java.sql.Date(e.getEmploymentDate().getTime()));
        if (isUpdate) {
            stmt.setString(6, e.getId());
        } else {
            stmt.setString(1, e.getId()); // shift everything right by 1 for insert
            stmt.setString(2, e.getName());
            stmt.setString(3, e.getPassword());
            stmt.setString(4, e.getBankAccount());
            stmt.setFloat(5, e.getSalary());
            stmt.setDate(6, new java.sql.Date(e.getEmploymentDate().getTime()));
        }
    }

    private void insertEmployeeRoles(PreparedStatement stmt, Employee employee) throws SQLException {
        for (Role role : employee.getRoles()) {
            stmt.setString(1, employee.getId());
            stmt.setString(2, role.getName());
            stmt.addBatch();
        }
        stmt.executeBatch();
    }

    private Employee mapToEmployee(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String password = rs.getString("password");
        String bankAccount = rs.getString("bank_account");
        Float salary = rs.getFloat("salary");
        Date employmentDate = rs.getDate("employment_date");

        List<Role> roles = getRolesByEmployeeId(id);

        Employee employee = new Employee(id, roles, name, password, bankAccount, salary, employmentDate);
        return employee;
    }

    private List<Role> getRolesByEmployeeId(String employeeId) {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT role_name FROM employee_roles WHERE employee_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                roles.add(new Role(rs.getString("role_name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get roles for employee " + employeeId, e);
        }
        return roles;
    }

    @Override
    public List<Employee> selectByRole(String roleName) {
        List<Employee> employees = new ArrayList<>();
        String sql = """
        SELECT e.* FROM employees e
        JOIN employee_roles er ON e.id = er.employee_id
        WHERE er.role_name = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, roleName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                employees.add(mapToEmployee(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Select by role failed", e);
        }
        return employees;
    }
    @Override
    public List<Employee> selectAvailableOnDate(String date) {
        List<Employee> employees = new ArrayList<>();
        String sql = """
        SELECT DISTINCT e.* FROM employees e
        JOIN weekly_availability wa ON e.id = wa.employee_id
        WHERE wa.available_date = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date)); // assuming date is "yyyy-mm-dd"
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                employees.add(mapToEmployee(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Select available employees failed", e);
        }

        return employees;
    }


}
