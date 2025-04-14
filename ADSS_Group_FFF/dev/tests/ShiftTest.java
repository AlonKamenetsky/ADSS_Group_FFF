package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Domain.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ShiftTest {

    private Shift shift;
    private Role hrRole;
    private Role cashierRole;
    private Employee employee1;
    private Employee employee2;
    private Date shiftDate;

    @BeforeEach
    void setUp() throws ParseException {
        hrRole = new Role("HR");
        cashierRole = new Role("Cashier");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        shiftDate = dateFormat.parse("01-05-2025");

        employee1 = new Employee("1", new LinkedList<>(List.of(hrRole)), "Dana", "123", "IL123BANK", 5000f, shiftDate);
        employee2 = new Employee("2", new LinkedList<>(List.of(cashierRole)), "John", "456", "IL456BANK", 4500f, shiftDate);

        Map<Role, ArrayList<Employee>> requiredRoles = new HashMap<>();
        requiredRoles.put(hrRole, new ArrayList<>());
        requiredRoles.put(cashierRole, new ArrayList<>());

        Map<Role, Integer> requiredCounts = new HashMap<>();
        requiredCounts.put(hrRole, 1);
        requiredCounts.put(cashierRole, 2);

        shift = new Shift("SHIFT1", shiftDate, Shift.ShiftTime.Morning, requiredRoles, requiredCounts);
    }

    @Test
    void testAssignEmployeeSuccess() {
        shift.assignEmployee(employee1, hrRole);
        assertEquals(1, shift.getRequiredRoles().get(hrRole).size());
        assertEquals(employee1, shift.getRequiredRoles().get(hrRole).get(0));
        assertEquals(1, shift.getAssignedEmployees().size());
    }

    @Test
    void testAssignEmployeeExceedsRequiredCount() {
        Employee extraEmployee = new Employee("3", new LinkedList<>(List.of(hrRole)), "Alex", "789", "IL789BANK", 4000f, shiftDate);
        shift.assignEmployee(employee1, hrRole);
        shift.assignEmployee(extraEmployee, hrRole); // Exceeds required count

        assertEquals(1, shift.getRequiredRoles().get(hrRole).size()); // Should not add
    }

    @Test
    void testAddRequiredRole() {
        Employee newEmployee = new Employee("4", new LinkedList<>(List.of(cashierRole)), "Ben", "101", "IL101BANK", 3500f, shiftDate);
        shift.addRequiredRole(cashierRole, newEmployee);

        assertTrue(shift.getRequiredRoles().get(cashierRole).contains(newEmployee));
    }

    @Test
    void testGetters() {
        assertEquals("SHIFT1", shift.getID());
        assertEquals(shiftDate, shift.getDate());
        assertEquals(Shift.ShiftTime.Morning, shift.getType());
    }
}
