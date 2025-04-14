package Domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Employee extends User {

    private String BankAccount;
    private Float Salary;
    private Date EmploymentDate;
    private List<Availability> WeeklyAvailability;
    private List<Shift> Shifts;
    private List<Date> Holidays;

    public Employee(String ID, List<Role> roles, String name, String Password, String BankAccount, Float Salary, Date EmploymentDate) {
        super(ID, name, Password, roles);
        this.BankAccount = BankAccount;
        this.Salary = Salary;
        this.EmploymentDate = EmploymentDate;
        this.WeeklyAvailability = new ArrayList<>();
        this.Shifts = ShiftsRepo.getInstance().getShifts();
        this.Holidays = new ArrayList<>();
    }

    public Float getSalary() {
        return Salary;
    }

    public Date getEmploymentDate() {
        return EmploymentDate;
    }

    public List<Availability> getWeeklyAvailability() {
        return WeeklyAvailability;
    }

    public String getBankAccount() {
        return BankAccount;
    }

    public List<Shift> getShifts() {
        return Shifts;
    }

    public List<Date> getHolidays() {
        return Holidays;
    }

    public void setBankAccount(String bankAccount) {
        BankAccount = bankAccount;
    }

    public void setEmploymentDate(Date employmentDate) {
        EmploymentDate = employmentDate;
    }

    public void setShifts(LinkedList<Shift> shifts) {
        Shifts = shifts;
    }

    public void setWeeklyAvailability(LinkedList<Availability> weeklyAvailability) {
        WeeklyAvailability = weeklyAvailability;
    }

    public void setSalary(Float salary) {
        Salary = salary;
    }

    public void addAvailability(Date date, Shift.ShiftTime type) {
        WeeklyAvailability.add(new Availability(date, type));
    }

    public boolean isAvailable(Date date, Shift.ShiftTime type) {
        for (Availability availability : WeeklyAvailability) {
            if (availability.getDate().equals(date) && availability.getType() == type) {
                return true;
            }
        }
        return false;
    }
}
