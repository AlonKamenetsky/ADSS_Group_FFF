package Domain;

import java.util.ArrayList;
import java.util.List;

public class EmployeesRepo {

    private static EmployeesRepo instance = null;

    private final List<Employee> employees;

    private EmployeesRepo() {
        employees = new ArrayList<>();
    }

    public static EmployeesRepo getInstance() {
        if (instance == null) {
            instance = new EmployeesRepo();
        }
        return instance;
    }

    // Returns the complete list of employees.
    public List<Employee> getEmployees() {
        return employees;
    }

    // Retrieves an employee by their ID.
    public Employee getEmployeeById(String id) {
        return employees.stream()
                .filter(employee -> employee.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Adds a new employee if one with the same ID doesn't already exist.
    public void addEmployee(Employee employee) {
        if (getEmployeeById(employee.getId()) == null) {
            employees.add(employee);
        } else {
            System.out.println("Employee already exists.");
        }
    }

    // Removes an employee from the repository.
    public void removeEmployee(Employee employee) {
        if (employees.contains(employee)) {
            employees.remove(employee);
        } else {
            System.out.println("Employee not found.");
        }
    }
}
