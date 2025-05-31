package HR.Service;

import HR.DataAccess.*;
import HR.Domain.*;
import HR.Presentation.PresentationUtils;
import Util.Database;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EmployeeService {

    private static EmployeeService instance;
    private final EmployeeDAO employeeDAO;
    private final DriverInfoDAO driverInfoDAO;

    private EmployeeService() {
        Connection conn = Database.getConnection();
        this.employeeDAO = new EmployeeDAOImpl(conn);
        this.driverInfoDAO = new DriverInfoDAOImpl(conn);
    }

    public static EmployeeService getInstance() {
        if (instance == null) {
            instance = new EmployeeService();
        }
        return instance;
    }

    public List<Employee> getEmployees() {
        return employeeDAO.selectAll();
    }

    public Employee getEmployeeById(String id) {
        return employeeDAO.selectById(id);
    }

    public void addEmployee(String id, List<Role> rolesList, String name, String password, String bankAccount, Float salary, Date employmentDate) {
        Employee newEmployee = new Employee(id, rolesList, name, password, bankAccount, salary, employmentDate);
        employeeDAO.insert(newEmployee);
    }

    public void addEmployee(String id, List<Role> rolesList, String name, String password,
                            String bankAccount, Float salary, Date employmentDate,
                            String licenseTypeIfDriver) {
        Employee newEmployee = new Employee(id, rolesList, name, password, bankAccount, salary, employmentDate);

        boolean isDriver = hasDriverRole(newEmployee);

        if (isDriver && (licenseTypeIfDriver == null || licenseTypeIfDriver.isBlank())) {
            throw new IllegalArgumentException("Driver must have a license type.");
        }

        employeeDAO.insert(newEmployee);

        if (isDriver) {
            driverInfoDAO.insert(new DriverInfo(id, licenseTypeIfDriver));
        }
    }

    public void removeEmployee(Employee employee) {
        if (hasDriverRole(employee)) {
            driverInfoDAO.delete(employee.getId());
        }
        employeeDAO.delete(employee.getId());
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

    public void updateWeeklyAvailability(Employee employee, Set<WeeklyAvailability> newAvailabilities) {
        employee.getAvailabilityNextWeek().clear();
        employee.getAvailabilityNextWeek().addAll(newAvailabilities);
    }

    public void viewHolidays(Employee employee) {
        PresentationUtils.typewriterPrint("\nYour Vacations:", 20);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy‑MM‑dd");
        for (Date d : employee.getHolidays()) {
            PresentationUtils.typewriterPrint("• " + fmt.format(d), 20);
        }
        if (employee.getHolidays().isEmpty()) {
            PresentationUtils.typewriterPrint("No vacations scheduled.", 20);
        }
    }

    private boolean hasDriverRole(Employee e) {
        return e.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("Driver"));
    }

    public DriverInfo getDriverInfo(Employee e) {
        if (!hasDriverRole(e)) return null;
        return driverInfoDAO.getByEmployeeId(e.getId());
    }

    public void updateDriverLicense(String employeeId, String newLicenseType) {
        DriverInfo existing = driverInfoDAO.getByEmployeeId(employeeId);
        if (existing != null) {
            driverInfoDAO.update(new DriverInfo(employeeId, newLicenseType));
        } else {
            driverInfoDAO.insert(new DriverInfo(employeeId, newLicenseType));
        }
    }

    public List<Employee> findAvailableDrivers(String licenseType, Date date, Shift.ShiftTime shiftTime) {
        return employeeDAO.selectAll().stream()
                .filter(this::hasDriverRole)
                .filter(e -> {
                    DriverInfo info = driverInfoDAO.getByEmployeeId(e.getId());
                    return info != null && info.getLicenseType().equalsIgnoreCase(licenseType);
                })
                .filter(e -> !e.getHolidays().contains(date))
                .filter(e -> e.getAvailabilityThisWeek().isEmpty() || e.isAvailable(date, shiftTime))
                .collect(Collectors.toList());
    }
}
