
package HR.tests.ServiceTests;

import HR.DataAccess.EmployeeDAO;
import HR.DataAccess.RoleDAO;
import HR.DataAccess.ShiftDAO;
import HR.DataAccess.SwapDAO;
import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.SwapRequest;
import HR.Domain.ShiftAssignment;
import HR.DTO.SwapRequestDTO;
import HR.Mapper.SwapRequestMapper;

import HR.Service.SwapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SwapServiceTest {

    @Mock private SwapDAO swapDAO;
    @Mock private EmployeeDAO employeeDAO;
    @Mock private ShiftDAO shiftDAO;
    @Mock private RoleDAO roleDAO;

    private SwapService swapService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        swapService = new SwapService(swapDAO, employeeDAO, shiftDAO, roleDAO);
    }

    @Test
    public void testSendSwapRequest_validInputs_insertsSwap() {
        String empId = "E1";
        String shiftId = "S1";
        String roleName = "Driver";

        Employee emp = new Employee(empId, null, "Alice", null, null, 0f, null);
        Shift shift = new Shift(shiftId, new java.util.Date(), null, Map.of(), Map.of());
        Role role = new Role(roleName);

        when(employeeDAO.selectById(empId)).thenReturn(emp);
        when(shiftDAO.selectById(shiftId)).thenReturn(shift);
        when(roleDAO.findByName(roleName)).thenReturn(role);

        swapService.sendSwapRequest(empId, shiftId, roleName);

        ArgumentCaptor<SwapRequest> captor = ArgumentCaptor.forClass(SwapRequest.class);
        verify(swapDAO).insert(captor.capture());
        SwapRequest inserted = captor.getValue();
        assertEquals(emp, inserted.getEmployee());
        assertEquals(shift, inserted.getShift());
        assertEquals(role, inserted.getRole());
    }

    @Test
    public void testSendSwapRequest_invalidEmployee_throwsException() {
        when(employeeDAO.selectById("E2")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            swapService.sendSwapRequest("E2", "S1", "Driver");
        });
    }

    @Test
    public void testCancelSwapRequest_invokesDelete() {
        int requestId = 5;
        swapService.cancelSwapRequest(requestId);
        verify(swapDAO).delete(requestId);
    }

    @Test
    public void testAcceptSwapRequests_validSwaps_updatesAndDeletes() {
        int req1Id = 1, req2Id = 2;
        Employee e1 = new Employee("E1", null, "Tom", null, null, 0f, null);
        Employee e2 = new Employee("E2", null, "Jerry", null, null, 0f, null);
        Role role = new Role("Loader");

        Shift s1 = mock(Shift.class);
        Shift s2 = mock(Shift.class);

        SwapRequest r1 = mock(SwapRequest.class);
        SwapRequest r2 = mock(SwapRequest.class);

        ShiftAssignment sa1 = mock(ShiftAssignment.class);
        ShiftAssignment sa2 = mock(ShiftAssignment.class);

        when(swapDAO.selectById(req1Id)).thenReturn(r1);
        when(swapDAO.selectById(req2Id)).thenReturn(r2);

        when(r1.getEmployee()).thenReturn(e1);
        when(r2.getEmployee()).thenReturn(e2);
        when(r1.getShift()).thenReturn(s1);
        when(r2.getShift()).thenReturn(s2);
        when(r1.getRole()).thenReturn(role);
        when(r2.getRole()).thenReturn(role);

        List<ShiftAssignment> list1 = new ArrayList<>();
        when(s1.getAssignedEmployees()).thenReturn(list1);
        list1.add(sa1);
        when(sa1.getEmployeeId()).thenReturn(e1.getId());
        when(sa1.getRole()).thenReturn(role);

        List<ShiftAssignment> list2 = new ArrayList<>();
        when(s2.getAssignedEmployees()).thenReturn(list2);
        list2.add(sa2);
        when(sa2.getEmployeeId()).thenReturn(e2.getId());
        when(sa2.getRole()).thenReturn(role);

        swapService.acceptSwapRequests(req1Id, req2Id);

        verify(s1).assignEmployee(e2, role);
        verify(s2).assignEmployee(e1, role);
        verify(shiftDAO).update(s1);
        verify(shiftDAO).update(s2);
        verify(swapDAO).delete(req1Id);
        verify(swapDAO).delete(req2Id);
    }

    @Test
    public void testAcceptSwapRequests_invalidRequests_throwsException() {
        when(swapDAO.selectById(3)).thenReturn(null);
        when(swapDAO.selectById(4)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            swapService.acceptSwapRequests(3, 4);
        });
    }

    @Test
    public void testGetSwapRequests_returnsDTOList() {
        SwapRequest r1 = mock(SwapRequest.class);
        SwapRequest r2 = mock(SwapRequest.class);
        SwapRequestDTO dto1 = new SwapRequestDTO();
        SwapRequestDTO dto2 = new SwapRequestDTO();

        when(swapDAO.selectAll()).thenReturn(List.of(r1, r2));

        try (MockedStatic<SwapRequestMapper> mocked = mockStatic(SwapRequestMapper.class)) {
            mocked.when(() -> SwapRequestMapper.toDTO(r1)).thenReturn(dto1);
            mocked.when(() -> SwapRequestMapper.toDTO(r2)).thenReturn(dto2);

            List<SwapRequestDTO> result = swapService.getSwapRequests();
            assertEquals(2, result.size());
            assertTrue(result.contains(dto1));
            assertTrue(result.contains(dto2));
        }
    }
}
