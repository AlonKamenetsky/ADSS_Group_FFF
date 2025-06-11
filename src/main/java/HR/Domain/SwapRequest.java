package HR.Domain;

public class SwapRequest {
    private int id;
    private Employee employee;
    private Shift shift;
    private Role role;
    private boolean resolved;

    public SwapRequest(int id, Employee employee, Shift shift, Role role) {
        this.id = id;
        this.employee = employee;
        this.shift = shift;
        this.role = role;
    }

    public SwapRequest(Employee employee, Shift shift, Role role) {
        this(-1, employee, shift, role); // default = unresolved
    }

    public int getId() { return id; }

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
