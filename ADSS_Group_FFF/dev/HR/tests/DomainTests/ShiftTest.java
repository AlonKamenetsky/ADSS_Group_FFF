package HR.tests.DomainTests;

import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.ShiftAssignment;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftTest {

    @Test
    public void constructor_and_getters() {
        String id = "shiftX";
        Date date = new Date();
        Shift.ShiftTime type = Shift.ShiftTime.Morning;

        Role r = new Role("Driver");
        Employee e = new Employee("emp1", List.of(r), "Name", "pw", "acc", 1000f, new Date());

        Map<Role, ArrayList<Employee>> reqRoles = new HashMap<>();
        reqRoles.put(r, new ArrayList<>());

        Map<Role, Integer> reqCounts = new HashMap<>();
        reqCounts.put(r, 1);

        Shift s = new Shift(id, date, type, reqRoles, reqCounts);
        assertEquals(id, s.getID());
        assertEquals(date, s.getDate());
        assertEquals(type, s.getType());
        assertEquals(reqRoles, s.getRequiredRoles());
        assertEquals(reqCounts, s.getRequiredCounts());
        assertTrue(s.getAssignedEmployees().isEmpty());
    }

    @Test
    public void fromTime_classifiesCorrectly() {
        LocalTime t1 = LocalTime.of(9, 0);
        assertEquals(Shift.ShiftTime.Morning, Shift.fromTime(t1));

        LocalTime t2 = LocalTime.of(18, 30);
        assertEquals(Shift.ShiftTime.Evening, Shift.fromTime(t2));

        LocalTime t3 = LocalTime.of(3, 0);
        assertNull(Shift.fromTime(t3));
    }

    @Test
    public void assignEmployee_respectsRequiredCount() {
        Role role = new Role("Cleaner");
        Employee e1 = new Employee("e1", List.of(role), "A", "pw", "b", 500f, new Date());
        Employee e2 = new Employee("e2", List.of(role), "B", "pw", "b", 600f, new Date());

        Map<Role, ArrayList<Employee>> reqRoles = new HashMap<>();
        reqRoles.put(role, new ArrayList<>());
        Map<Role, Integer> reqCounts = Map.of(role, 1);

        Shift s = new Shift("sh1", new Date(), Shift.ShiftTime.Evening, reqRoles, reqCounts);

        // 1st assignment should succeed
        s.assignEmployee(e1, role);
        assertEquals(1, s.getRequiredRoles().get(role).size());
        assertEquals(1, s.getAssignedEmployees().size());
        assertEquals("e1", s.getAssignedEmployees().get(0).getEmployeeId());

        // 2nd assignment exceeds requiredCount, so not added
        s.assignEmployee(e2, role);
        assertEquals(1, s.getRequiredRoles().get(role).size());
        assertEquals(1, s.getAssignedEmployees().size());
    }

    @Test
    public void setAssignedEmployees_replacesList() {
        Role r = new Role("Cashier");
        Shift s = new Shift("sh2", new Date(), Shift.ShiftTime.Morning, Map.of(r, new ArrayList<>()), Map.of(r, 1));

        ShiftAssignment sa1 = new ShiftAssignment("empA", "sh2", r);
        ShiftAssignment sa2 = new ShiftAssignment("empB", "sh2", r);
        s.setAssignedEmployees(List.of(sa1, sa2));

        List<ShiftAssignment> assigned = s.getAssignedEmployees();
        assertEquals(2, assigned.size());
        assertTrue(assigned.stream().anyMatch(a -> a.getEmployeeId().equals("empA")));
        assertTrue(assigned.stream().anyMatch(a -> a.getEmployeeId().equals("empB")));
    }
}
