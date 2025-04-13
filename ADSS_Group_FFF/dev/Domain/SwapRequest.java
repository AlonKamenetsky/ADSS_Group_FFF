package Domain;

public class SwapRequest {
    private Employee employee;
    private Shift shift;
    private Role role;

    public SwapRequest(Employee employee, Shift shift, Role role) {
        this.employee = employee;
        this.shift = shift;
        this.role = role;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Shift getShift() {
        return shift;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "Employee: " + employee.getName() +
                ", Shift: " + shift.getID() +
                ", Role: " + role.getName();
    }
}
