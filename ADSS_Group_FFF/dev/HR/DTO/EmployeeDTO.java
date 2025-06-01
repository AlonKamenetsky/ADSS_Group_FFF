// File: EmployeeDTO.java
package HR.DTO;

import java.util.Date;
import java.util.List;

public class EmployeeDTO extends UserDTO {
    private String bankAccount;
    private Float salary;
    private Date employmentDate;

    // We represent availability lists as DTOs:
    private List<WeeklyAvailabilityDTO> availabilityThisWeek;
    private List<WeeklyAvailabilityDTO> availabilityNextWeek;

    // List of holiday dates (if you want to expose them)
    private List<Date> holidays;

    public EmployeeDTO() { }

    public EmployeeDTO(
            String id,
            String name,
            List<RoleDTO> roles,
            String bankAccount,
            Float salary,
            Date employmentDate,
            List<WeeklyAvailabilityDTO> availabilityThisWeek,
            List<WeeklyAvailabilityDTO> availabilityNextWeek,
            List<Date> holidays
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

    public Date getEmploymentDate() {
        return employmentDate;
    }

    public void setEmploymentDate(Date employmentDate) {
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

    public List<Date> getHolidays() {
        return holidays;
    }

    public void setHolidays(List<Date> holidays) {
        this.holidays = holidays;
    }
}
