package Domain;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ManagerDL extends EmployeeDL{


    public ManagerDL(String ID, LinkedList<RoleDL> roles, String name, String password,
                     String bankAccount, Float salary, Date employmentDate) {
        super(ID, roles, name, password, bankAccount, salary, employmentDate);
    }


    private void AddRole(String Name){
        RolesRepo roles = RolesRepo.getInstance();
        roles.addRole(new RoleDL(Name));
    }


    private void RemoveRole(RoleDL role){
        RolesRepo roles = RolesRepo.getInstance();
        roles.getRoles().remove(role);
    }

    private void AddEmployee(String ID, List<RoleDL> roles, String name, String Password, String BankAccount, Float Salary, Date EmploymentDate){
        EmployeesRepo employees = EmployeesRepo.getInstance();
        employees.addEmployee(new EmployeeDL(ID,roles,name,Password,BankAccount,Salary,EmploymentDate));
    }



    private void RemoveEmployee(EmployeeDL employee){
        EmployeesRepo employees = EmployeesRepo.getInstance();
        employees.getEmployees().remove(employee);
    }

    private void swapShifts(EmployeeDL emp1, EmployeeDL emp2, ShiftDL shift1, ShiftDL shift2, RoleDL role) {
        // Retrieve the required roles dictionary from each shift.
        Map<RoleDL, List<EmployeeDL>> roles1 = shift1.getRequiredRoles();
        Map<RoleDL, List<EmployeeDL>> roles2 = shift2.getRequiredRoles();

        // From shift1's roles, remove emp1 and add emp2 in the key corresponding to the given role.
        List<EmployeeDL> employeesForRole1 = roles1.get(role);
        if (employeesForRole1 != null && employeesForRole1.contains(emp1)) {
            employeesForRole1.remove(emp1);
            employeesForRole1.add(emp2);
        } else {
            System.out.println("Employee " + emp1.getId() + " was not found for role " + role.getName() +
                    " in shift " + shift1.getID());
        }

        // From shift2's roles, remove emp2 and add emp1 in the key corresponding to the given role.
        List<EmployeeDL> employeesForRole2 = roles2.get(role);
        if (employeesForRole2 != null && employeesForRole2.contains(emp2)) {
            employeesForRole2.remove(emp2);
            employeesForRole2.add(emp1);
        } else {
            System.out.println("Employee " + emp2.getId() + " was not found for role " + role.getName() +
                    " in shift " + shift2.getID());
        }

        System.out.println("Swapped employees " + emp1.getName() + " and " + emp2.getName() + " for role " +
                role.getName() + " between shifts " + shift1.getID() + " and " + shift2.getID());
    }

}

