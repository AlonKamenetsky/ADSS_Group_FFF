// src/Presentation/ConsoleUtils.java
package Presentation;

import Domain.EmployeesRepo;
import Domain.Shift;
import Domain.ShiftAssignment;

public class PresentationUtils {
    /**
     * Prints text one character at a time, pausing `delayMs` milliseconds between each.
     */
    public static void typewriterPrint(String text, int delayMs) {
        for (char c : text.toCharArray()) {
            System.out.print(c);
            System.out.flush();
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println();
    }

    public static void printShift(Shift shift) {
        System.out.println("Shift ID: " + shift.getID());
        System.out.println("Date: " + shift.getDate());
        System.out.println("Type: " + shift.getType());
        System.out.println("Assigned employees:");
        for (ShiftAssignment sa : shift.getAssignedEmployees()) {
            String employeeName = EmployeesRepo.getInstance().getEmployeeById(sa.getEmployeeId()).getName();
            System.out.println("- " + employeeName + " as " + sa.getRole().getName());
        }
    }
}
