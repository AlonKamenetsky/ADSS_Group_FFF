package tests;

import Domain.Employee;
import Domain.Role;
import Domain.Shift;
import Domain.ShiftsRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftsRepoTest {

    private ShiftsRepo repo;

    @BeforeEach
    public void setUp() {
        repo = ShiftsRepo.getInstance();
        repo.getShifts().clear();
    }

    @Test
    public void testAddAndGetShift() {
        Role role = new Role("Cashier");
        Date date = new Date();
        Map<Role, ArrayList<Employee>> requiredRoles = new HashMap<>();
        Map<Role, Integer> requiredCounts = new HashMap<>();
        requiredRoles.put(role, new ArrayList<>());
        requiredCounts.put(role, 1);
        Shift shift = new Shift("SHIFT1", date, Shift.ShiftTime.Morning, requiredRoles, requiredCounts);

        repo.addShift(shift);
        assertEquals(1, repo.getShifts().size());

        Shift retrieved = repo.getShiftById("SHIFT1");
        assertNotNull(retrieved);
        assertEquals("SHIFT1", retrieved.getID());
    }

    @Test
    public void testRemoveShift() {
        Role role = new Role("Cashier");
        Date date = new Date();
        Map<Role, ArrayList<Employee>> requiredRoles = new HashMap<>();
        Map<Role, Integer> requiredCounts = new HashMap<>();
        requiredRoles.put(role, new ArrayList<>());
        requiredCounts.put(role, 1);
        Shift shift = new Shift("SHIFT1", date, Shift.ShiftTime.Morning, requiredRoles, requiredCounts);

        repo.addShift(shift);
        assertEquals(1, repo.getShifts().size());

        repo.removeShift(shift);
        assertEquals(0, repo.getShifts().size());
    }
}
