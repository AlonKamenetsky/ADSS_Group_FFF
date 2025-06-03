package HR.DTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * A small DTO used specifically when creating a new employee (or resetting a password).
 * It carries exactly the same fields as EmployeeDTO, plus a rawPassword.
 */
public class CreateEmployeeDTO {
    private String id;
    private String name;
    private List<RoleDTO> roles;
    private String rawPassword;       // <-- The only place we carry the password
    private String bankAccount;
    private Float salary;
    private LocalDate employmentDate;
    private List<WeeklyAvailabilityDTO> availabilityThisWeek;
    private List<WeeklyAvailabilityDTO> availabilityNextWeek;
    private List<LocalDate> holidays;

    public CreateEmployeeDTO() { }

    public CreateEmployeeDTO(
            String id,
            String name,
            List<RoleDTO> roles,
            String rawPassword,
            String bankAccount,
            Float salary,
            LocalDate employmentDate,
            List<WeeklyAvailabilityDTO> availabilityThisWeek,
            List<WeeklyAvailabilityDTO> availabilityNextWeek,
            List<LocalDate> holidays
    ) {
        this.id                    = id;
        this.name                  = name;
        this.roles                 = roles;
        this.rawPassword           = rawPassword;
        this.bankAccount           = bankAccount;
        this.salary                = salary;
        this.employmentDate        = employmentDate;
        this.availabilityThisWeek  = availabilityThisWeek;
        this.availabilityNextWeek  = availabilityNextWeek;
        this.holidays              = holidays;
    }

    // — Getters and Setters for every field —
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }
    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }

    // This is the NEW piece:
    public String getRawPassword() {
        return rawPassword;
    }
    public void setRawPassword(String rawPassword) {
        this.rawPassword = rawPassword;
    }

    public String getBankAccount() {
        return bankAccount;
    }
    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Float getSalary() {
        return salary;
    }
    public void setSalary(Float salary) {
        this.salary = salary;
    }

    public LocalDate getEmploymentDate() {
        return employmentDate;
    }
    public void setEmploymentDate(LocalDate employmentDate) {
        this.employmentDate = employmentDate;
    }

    public List<WeeklyAvailabilityDTO> getAvailabilityThisWeek() {
        return availabilityThisWeek;
    }
    public void setAvailabilityThisWeek(List<WeeklyAvailabilityDTO> availabilityThisWeek) {
        this.availabilityThisWeek = availabilityThisWeek;
    }

    public List<WeeklyAvailabilityDTO> getAvailabilityNextWeek() {
        return availabilityNextWeek;
    }
    public void setAvailabilityNextWeek(List<WeeklyAvailabilityDTO> availabilityNextWeek) {
        this.availabilityNextWeek = availabilityNextWeek;
    }

    public List<LocalDate> getHolidays() {
        return holidays;
    }
    public void setHolidays(List<LocalDate> holidays) {
        this.holidays = holidays;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreateEmployeeDTO that = (CreateEmployeeDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(roles, that.roles) &&
                Objects.equals(rawPassword, that.rawPassword) &&
                Objects.equals(bankAccount, that.bankAccount) &&
                Objects.equals(salary, that.salary) &&
                Objects.equals(employmentDate, that.employmentDate) &&
                Objects.equals(availabilityThisWeek, that.availabilityThisWeek) &&
                Objects.equals(availabilityNextWeek, that.availabilityNextWeek) &&
                Objects.equals(holidays, that.holidays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id, name, roles, rawPassword, bankAccount,
                salary, employmentDate,
                availabilityThisWeek, availabilityNextWeek, holidays
        );
    }

    @Override
    public String toString() {
        return "CreateEmployeeDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", roles=" + roles +
                ", rawPassword='[PROTECTED]'" +
                ", bankAccount='" + bankAccount + '\'' +
                ", salary=" + salary +
                ", employmentDate=" + employmentDate +
                ", availabilityThisWeek=" + availabilityThisWeek +
                ", availabilityNextWeek=" + availabilityNextWeek +
                ", holidays=" + holidays +
                '}';
    }
}
