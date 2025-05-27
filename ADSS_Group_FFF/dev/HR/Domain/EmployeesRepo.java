package HR.Domain;


import java.util.List;

public interface EmployeesRepo {
    List<Employee> getEmployees();
    Employee getEmployeeById(String id);
    void addEmployee(Employee employee);
    void removeEmployee(Employee employee);
}
