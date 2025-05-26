package HR.tests.DomainTests;

import HR.DataAccess.ShiftDAOImpl;
import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.SwapRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SwapRequestsRepoTest {

    private ShiftDAOImpl.SwapRequestsRepo repo;

    @BeforeEach
    public void setUp() {
        repo = ShiftDAOImpl.SwapRequestsRepo.getInstance();
        // Clear any existing swap requests.
        repo.getSwapRequests().clear();
    }

    @Test
    public void testAddAndRemoveSwapRequest() {
        Role role = new Role("Cashier");
        Employee employee = new Employee("1", List.of(role), "Dana", "pass", "Bank", 5000f, new Date());
        Map<Role, ArrayList<Employee>> requiredRoles = new HashMap<>();
        Map<Role, Integer> requiredCounts = new HashMap<>();
        requiredRoles.put(role, new ArrayList<>());
        requiredCounts.put(role, 1);
        Shift shift = new Shift("SHIFT1", new Date(), Shift.ShiftTime.Morning, requiredRoles, requiredCounts);
        SwapRequest request = new SwapRequest(employee, shift, role);

        repo.addSwapRequest(request);
        List<SwapRequest> swapList = repo.getSwapRequests();
        assertEquals(1, swapList.size());
        assertTrue(swapList.contains(request));

        repo.removeSwapRequest(request);
        assertEquals(0, repo.getSwapRequests().size());
    }
}
