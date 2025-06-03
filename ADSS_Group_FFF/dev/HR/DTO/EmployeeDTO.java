package HR.DTO;

import java.time.LocalDate;
import java.util.List;

public class EmployeeDTO extends UserDTO {
    private String bankAccount;
    private Float salary;
    private LocalDate employmentDate;

    private List<WeeklyAvailabilityDTO> availabilityThisWeek;
    private List<WeeklyAvailabilityDTO> availabilityNextWeek;

    private List<LocalDate> holidays;

    public EmployeeDTO() { }

    public EmployeeDTO(
            String id,
            String name,
            List<RoleDTO> roles,
            String bankAccount,
            Float salary,
            LocalDate employmentDate,
            List<WeeklyAvailabilityDTO> availabilityThisWeek,
            List<WeeklyAvailabilityDTO> availabilityNextWeek,
            List<LocalDate> holidays
    ) {
        super(id, name, roles);
        this.bankAccount = bankAccount;
        this.salary = salary;
        this.employmentDate = employmentDate;
        this.availabilityThisWeek = availabilityThisWeek;
        this.availabilityNextWeek = availabilityNextWeek;
        this.holidays = holidays;
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
}
