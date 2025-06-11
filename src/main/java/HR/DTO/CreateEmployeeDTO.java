// File: HR/DTO/CreateEmployeeDTO.java
package HR.DTO;

import java.util.Date;
import java.util.List;

/**
 * A small DTO used specifically when creating a new employee (or resetting a password).
 * It carries exactly the same fields as EmployeeDTO, plus a rawPassword.
 */
public class CreateEmployeeDTO {
    private String id;
    private String name;
    private List<RoleDTO> roles;
    private String rawPassword;       // <-- NEW: the only place we carry the password
    private String bankAccount;
    private Float salary;
    private Date employmentDate;
    private List<WeeklyAvailabilityDTO> availabilityThisWeek;
    private List<WeeklyAvailabilityDTO> availabilityNextWeek;
    private List<Date> holidays;

    public CreateEmployeeDTO() { }

    public CreateEmployeeDTO(
            String id,
            String name,
            List<RoleDTO> roles,
            String rawPassword,
            String bankAccount,
            Float salary,
            Date employmentDate,
            List<WeeklyAvailabilityDTO> availabilityThisWeek,
            List<WeeklyAvailabilityDTO> availabilityNextWeek,
            List<Date> holidays
    ) {
        this.id                 = id;
        this.name               = name;
        this.roles              = roles;
        this.rawPassword        = rawPassword;
        this.bankAccount        = bankAccount;
        this.salary             = salary;
        this.employmentDate     = employmentDate;
        this.availabilityThisWeek  = availabilityThisWeek;
        this.availabilityNextWeek  = availabilityNextWeek;
        this.holidays           = holidays;
    }

    // — Getters and Setters for every field —
    public String getId()                      { return id; }
    public void setId(String id)                { this.id = id; }
    public String getName()                    { return name; }
    public void setName(String name)            { this.name = name; }
    public List<RoleDTO> getRoles()            { return roles; }
    public void setRoles(List<RoleDTO> roles)   { this.roles = roles; }

    // This is the NEW piece:
    public String getRawPassword()             { return rawPassword; }
    public void setRawPassword(String rawPassword) { this.rawPassword = rawPassword; }

    public String getBankAccount()              { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }
    public Float getSalary()                   { return salary; }
    public void setSalary(Float salary)         { this.salary = salary; }
    public Date getEmploymentDate()             { return employmentDate; }
    public void setEmploymentDate(Date employmentDate) { this.employmentDate = employmentDate; }
    public List<WeeklyAvailabilityDTO> getAvailabilityThisWeek() { return availabilityThisWeek; }
    public void setAvailabilityThisWeek(List<WeeklyAvailabilityDTO> availabilityThisWeek) {
        this.availabilityThisWeek = availabilityThisWeek;
    }
    public List<WeeklyAvailabilityDTO> getAvailabilityNextWeek()   { return availabilityNextWeek; }
    public void setAvailabilityNextWeek(List<WeeklyAvailabilityDTO> availabilityNextWeek) {
        this.availabilityNextWeek = availabilityNextWeek;
    }
    public List<Date> getHolidays()             { return holidays; }
    public void setHolidays(List<Date> holidays)  { this.holidays = holidays; }
}
