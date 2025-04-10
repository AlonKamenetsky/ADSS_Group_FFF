package Domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class EmployeeDL extends UserDL{

    private String BankAccount;
    private Float Salary;
    private Date EmploymentDate;
    private List<AvailabilityDL> WeeklyAvailability;
    private List<ShiftDL> Shifts;

    public EmployeeDL(String ID, List<RoleDL> roles, String name, String Password, String BankAccount, Float Salary, Date EmploymentDate) {
        super(ID, name, Password, roles);
        this.BankAccount = BankAccount;
        this.Salary = Salary;
        this.EmploymentDate = EmploymentDate;
        this.WeeklyAvailability = new ArrayList<AvailabilityDL>();
        this.Shifts = new ArrayList<ShiftDL>();
    }

    public Float getSalary() {
        return Salary;
    }

    public Date getEmploymentDate() {
        return EmploymentDate;
    }

    public List<AvailabilityDL> getWeeklyAvailability() {
        return WeeklyAvailability;
    }

    public String getBankAccount() {
        return BankAccount;
    }

    public List<ShiftDL> getShifts() {
        return Shifts;
    }

    public void setBankAccount(String bankAccount) {
        BankAccount = bankAccount;
    }

    public void setEmploymentDate(Date employmentDate) {
        EmploymentDate = employmentDate;
    }

    public void setShifts(LinkedList<ShiftDL> shifts) {
        Shifts = shifts;
    }

    public void setWeeklyAvailability(LinkedList<AvailabilityDL> weeklyAvailability) {
        WeeklyAvailability = weeklyAvailability;
    }

    public void setSalary(Float salary) {
        Salary = salary;
    }
}
