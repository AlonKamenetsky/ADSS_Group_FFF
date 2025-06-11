package HR.Domain;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Employee extends User {
    private String bankAccount;
    private Float salary;
    private Date employmentDate;

    private final List<WeeklyAvailability> availabilityThisWeek = new ArrayList<>();
    private final List<WeeklyAvailability> availabilityNextWeek = new ArrayList<>();

    private final List<Shift> shifts = new ArrayList<>();
    private final List<Date> holidays = new ArrayList<>();

    public Employee(
            String ID,
            List<Role> roles,
            String name,
            String password,
            String bankAccount,
            Float salary,
            Date employmentDate
    ) {
        super(ID, name, password, roles);
        this.bankAccount    = bankAccount;
        this.salary         = salary;
        this.employmentDate = employmentDate;
    }

    // --- Getters & Setters --------------------------------------------

    public String getBankAccount()       { return bankAccount; }
    public void   setBankAccount(String b) { bankAccount = b; }

    public Float  getSalary()            { return salary; }
    public void   setSalary(Float s)     { salary = s; }

    public Date   getEmploymentDate()    { return employmentDate; }
    public void   setEmploymentDate(Date d) { employmentDate = d; }

    public List<WeeklyAvailability> getAvailabilityThisWeek() { return availabilityThisWeek; }
    public List<WeeklyAvailability> getAvailabilityNextWeek() { return availabilityNextWeek; }

    public List<Shift> getShifts()       { return shifts; }
    public List<Date>  getHolidays()     { return holidays; }

    public void addShift(Shift shift) {
        if (!shifts.contains(shift)) {
            shifts.add(shift);
        }
    }

    public void ShowInfo(){
        System.out.println("Employee Name: " + getName());
        System.out.println("Employee ID: " + getId());
        System.out.println("Employee Roles: " + getRoles());
        System.out.println("Employee Bank Account: " + getBankAccount());
        System.out.println("Employee Salary: " + getSalary());
        System.out.println("Employee Employment Date: " + getEmploymentDate());
    }

    // --- Availability Management ---------------------------------------

    public void swapAvailability() {
        availabilityThisWeek.clear();
        availabilityThisWeek.addAll(availabilityNextWeek);
        availabilityNextWeek.clear();
    }

    public void addAvailability(DayOfWeek day, Shift.ShiftTime time) {
        WeeklyAvailability slot = new WeeklyAvailability(day, time);
        if (!availabilityNextWeek.contains(slot)) {
            availabilityNextWeek.add(slot);
        }
    }

    public void removeAvailability(DayOfWeek day, Shift.ShiftTime time) {
        availabilityNextWeek.removeIf(av ->
                av.getDay() == day && av.getTime() == time
        );
    }

    public boolean isAvailable(Date date, Shift.ShiftTime time) {
        DayOfWeek dow = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .getDayOfWeek();
        return availabilityThisWeek.stream()
                .anyMatch(w -> w.getDay() == dow && w.getTime() == time);
    }

    public void addHoliday(Date date) {
        if (!holidays.contains(date)) {
            holidays.add(date);
        }
    }
}
