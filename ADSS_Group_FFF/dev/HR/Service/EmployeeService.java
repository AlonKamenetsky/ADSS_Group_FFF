package HR.Service;

import HR.DataAccess.WeeklyAvailabilityDAO;
import HR.Domain.Employee;
import HR.DataAccess.EmployeesRepo;
import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.WeeklyAvailability;
import HR.Presentation.PresentationUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class EmployeeService {

    private static EmployeeService instance;
    private final EmployeesRepo repo;

    private EmployeeService() {
        repo = EmployeesRepo.getInstance();
    }

    public static EmployeeService getInstance() {
        if (instance == null) {
            instance = new EmployeeService();
        }
        return instance;
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


    public String getEmployeeId(Employee e) { return e.getId(); }
    public String getEmployeeName(Employee e) { return e.getName(); }

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

    public void AddVacation(Employee employee, Date date) {
        employee.addHoliday(date);
        PresentationUtils.typewriterPrint("Vacation added: " + date.toString(), 20);
    }


// In file: HR/Service/EmployeeService.java

    public void updateWeeklyAvailability(Employee employee, Set<WeeklyAvailability> newAvailabilities) {
        // Update the employee's next week availability
        employee.getAvailabilityNextWeek().clear();
        employee.getAvailabilityNextWeek().addAll(newAvailabilities);
        // Persist the change if using a DAO or repository
/*      WeeklyAvailabilityDAO dao = new WeeklyAvailabilityDAO();
        dao.updateEmployeeNextWeekAvailability(employee.getId(), newAvailabilities);
    */
    }

    public void viewHolidays(Employee employee) {
        PresentationUtils.typewriterPrint("\nYour Vacations:", 20
        );
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy‑MM‑dd");
        for (Date d : employee.getHolidays()) {
            PresentationUtils.typewriterPrint("• " + fmt.format(d), 20
            );
        }
        if (employee.getHolidays().isEmpty()) {
            PresentationUtils.typewriterPrint("No vacations scheduled.", 20
            );
    }
    }
}
