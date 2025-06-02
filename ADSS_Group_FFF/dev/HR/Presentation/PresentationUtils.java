// src/HR.tests.Presentation/ConsoleUtils.java
package HR.Presentation;

import HR.DTO.EmployeeDTO;
import HR.DTO.RoleDTO;
import HR.DTO.ShiftAssignmentDTO;
import HR.DTO.ShiftDTO;
import HR.Domain.Shift;
import HR.Domain.ShiftAssignment;
import HR.Service.EmployeeService;

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



    public static void printEmployeeDTO(EmployeeDTO dto) {
        typewriterPrint("Employee ID: " + dto.getId(), 20);
        typewriterPrint("Name: " + dto.getName(), 20);
        typewriterPrint("Roles: ", 20);
        if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
            typewriterPrint("  None", 20);
        } else {
            for (RoleDTO role : dto.getRoles()) {
                typewriterPrint("  - " + role, 20);
            }
        }
        typewriterPrint("Bank Account: " + dto.getBankAccount(), 20);
        typewriterPrint("Salary: " + dto.getSalary(), 20);
        typewriterPrint("Employment Date: " + dto.getEmploymentDate(), 20);
    }

    public static void printShiftDTO(ShiftDTO shiftDTO) {
        typewriterPrint("Shift ID: " + shiftDTO.getId(),20);
        typewriterPrint("Date: " + shiftDTO.getDate(),20);
        typewriterPrint("Type: " + shiftDTO.getType(),20);
        typewriterPrint("Assigned employees:",20);
        for (ShiftAssignmentDTO sa : shiftDTO.getAssignedEmployees()) {
            String employeeName = EmployeeService.getInstance().getEmployeeById(sa.getEmployeeId()).getName();
            System.out.println("- " + employeeName + " as " + sa.getRoleName());
        }
    }
}
