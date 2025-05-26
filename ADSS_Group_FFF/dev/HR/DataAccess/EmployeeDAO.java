package HR.DataAccess;

import HR.Domain.Employee;

import java.util.List;

public interface EmployeeDAO {
    void insert(Employee employee);
    void update(Employee employee);
    void delete(String employeeId);
    Employee selectById(String employeeId);
    List<Employee> selectAll();
    List<Employee> selectByRole(String roleName);
    List<Employee> selectAvailableOnDate(String date); // Optional: based on availability system
}
