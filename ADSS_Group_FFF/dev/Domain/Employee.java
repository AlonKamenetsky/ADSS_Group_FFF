package Domain;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Employee extends User {
    private String bankAccount;
    private Float salary;
    private Date employmentDate;

    // Two separate lists for current‑week and next‑week
    private final List<WeeklyAvailability> availabilityThisWeek = new ArrayList<>();
    private final List<WeeklyAvailability> availabilityNextWeek = new ArrayList<>();

    // List of all assigned shifts (backed by ShiftsRepo)
    private final List<Shift> shifts = ShiftsRepo.getInstance().getCurrentWeekShifts();

    // Holidays
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

    public void ShowInfo(){
        System.out.println("Employee Name: " + getName());
        System.out.println("Employee ID: " + getId());
        System.out.println("Employee Roles: " + getRoles());
        System.out.println("Employee Bank Account: " + getBankAccount());
        System.out.println("Employee Salary: " + getSalary());
        System.out.println("Employee Employment Date: " + getEmploymentDate());
    }

    // --- Availability Management ---------------------------------------

    /** Called Saturday evening to roll nextWeek → thisWeek. */
    public void swapAvailability() {
        availabilityThisWeek.clear();
        availabilityThisWeek.addAll(availabilityNextWeek);
        availabilityNextWeek.clear();
    }

    /**
     * Employee declares availability for a day-of-week + shift-time slot
     * for next week.
     */
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


    /**
     * Checks if the employee is available THIS WEEK for the date+time slot.
     */
    public boolean isAvailable(Date date, Shift.ShiftTime time) {
        DayOfWeek dow = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .getDayOfWeek();
        return availabilityThisWeek.stream()
                .anyMatch(w -> w.getDay() == dow && w.getTime() == time);
    }

    // --- Vacation Management -------------------------------------------

    /** Add a new holiday (vacation) date. */
    public void addHoliday(Date date) {
        if (!holidays.contains(date)) {
            holidays.add(date);
        }
    }
}
