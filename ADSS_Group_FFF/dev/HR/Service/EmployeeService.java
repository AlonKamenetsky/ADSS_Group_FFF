package HR.Service;

import HR.Domain.Employee;
import HR.DataAccess.EmployeesRepo;
import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.WeeklyAvailability;

import java.util.Date;
import java.util.List;

public class EmployeeService {

    private final EmployeesRepo repo;

    public EmployeeService() {
        repo = EmployeesRepo.getInstance();
    }
    public List<Employee> getEmployees() {
        return repo.getEmployees();
    }

    public Employee getEmployeeById(String id) {
        return repo.getEmployeeById(id);
    }

    public void addEmployee(String id, List<Role> rolesList, String name, String password, String bankAccount, Float salary, Date employmentDate) {
        Employee newEmployee = new Employee(id, rolesList, name, password, bankAccount, salary, employmentDate);
        repo.addEmployee(newEmployee);
    }

    public void removeEmployee(Employee employee) {
        repo.removeEmployee(employee);
    }

    public String getBankAccount(Employee e)       { return e.getBankAccount(); }
    public void   setBankAccount(Employee e,String b) { e.setBankAccount(b); }

    public Float  getSalary(Employee e)            { return e.getSalary(); }
    public void   setSalary(Employee e,Float s)     { e.setSalary(s); }

    public Date getEmploymentDate(Employee e)    { return e.getEmploymentDate(); }
    public void   setEmploymentDate(Employee e,Date d) { e.setEmploymentDate(d); }

    public List<WeeklyAvailability> getAvailabilityThisWeek(Employee e) { return e.getAvailabilityThisWeek(); }
    public List<WeeklyAvailability> getAvailabilityNextWeek(Employee e) { return e.getAvailabilityNextWeek(); }

    public List<Shift> getShifts(Employee e)       { return e.getShifts(); }
    public List<Date>  getHolidays(Employee e)     { return e.getHolidays(); }

    public void ShowInfo(Employee e){
        e.ShowInfo();
    }


}
