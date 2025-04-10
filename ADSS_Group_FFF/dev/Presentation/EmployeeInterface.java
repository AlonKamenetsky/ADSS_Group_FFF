package Presentation;

import Domain.EmployeeDL;
import Domain.ShiftAssignmentDL;

import java.util.List;

public class EmployeeInterface {
    private EmployeeDL employee;

    public EmployeeInterface(EmployeeDL employee) {
        this.employee = employee;
    }

    public void showAssignedShifts(List<ShiftAssignmentDL> shiftAssignments) {
        System.out.println("Your Assigned Shifts:");
        for (ShiftAssignmentDL sa : shiftAssignments) {
            if (sa.getEmployeeId().equals(employee.getId())) {
                System.out.println("Shift ID: " + sa.getShiftId() + ", Role: " + sa.getRole().getName());
            }
        }
    }
}
