package tests;

import Domain.Employee;
import Domain.Role;
import Domain.Shift;
import Domain.ShiftAssignment;
import Domain.SwapRequest;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SwapRequestTest {

    @Test
    public void testSwapRequestGettersAndToString() {
        Role role = new Role("Cashier");
        // Create a simple employee.
        Employee employee = new Employee("1", List.of(role), "Dana", "pass", "Bank", 5000f, new Date());
        // Create a dummy shift.
        Date date = new Date();
        Map<Role, ArrayList<Employee>> requiredRoles = new HashMap<>();
        Map<Role, Integer> requiredCounts = new HashMap<>();
        requiredRoles.put(role, new ArrayList<>());
        requiredCounts.put(role, 1);
        Shift shift = new Shift("SHIFT1", date, Shift.ShiftTime.Morning, requiredRoles, requiredCounts);

        SwapRequest swapRequest = new SwapRequest(employee, shift, role);
        assertEquals(employee, swapRequest.getEmployee());
        assertEquals(shift, swapRequest.getShift());
        assertEquals(role, swapRequest.getRole());

        String str = swapRequest.toString();
        assertTrue(str.contains("Dana"));
        assertTrue(str.contains("SHIFT1"));
        assertTrue(str.contains("Cashier"));
    }
}
