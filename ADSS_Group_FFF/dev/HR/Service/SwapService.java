package HR.Service;

import HR.DTO.SwapRequestDTO;
import HR.DataAccess.EmployeeDAO;
import HR.DataAccess.EmployeeDAOImpl;
import HR.DataAccess.RoleDAO;
import HR.DataAccess.RoleDAOImpl;
import HR.DataAccess.ShiftDAO;
import HR.DataAccess.ShiftDAOImpl;
import HR.DataAccess.SwapDAO;
import HR.DataAccess.SwapDAOImpl;
import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.SwapRequest;
import HR.Mapper.SwapRequestMapper;
import Util.Database;

import java.util.List;
import java.util.stream.Collectors;

public class SwapService {

    private static SwapService instance;

    private final SwapDAO swapDAO;
    private final EmployeeDAO employeeDAO;
    private final ShiftDAO shiftDAO;
    private final RoleDAO roleDAO;

    private SwapService() {
        var conn = Database.getConnection();
        swapDAO     = new SwapDAOImpl(conn);
        employeeDAO = new EmployeeDAOImpl(conn);
        shiftDAO    = new ShiftDAOImpl(conn);
        roleDAO     = new RoleDAOImpl(conn);
    }

    public static synchronized SwapService getInstance() {
        if (instance == null) {
            instance = new SwapService();
        }
        return instance;
    }

    /**
     * 1. Send a new swap request.
     *    (Originally: SendSwapRequest(Employee, Shift, Role))
     */
    public void sendSwapRequest(
            String employeeId,
            String shiftId,
            String roleName
    ) {
        Employee emp  = employeeDAO.selectById(employeeId);
        if (emp == null) {
            throw new IllegalArgumentException("No employee with ID " + employeeId);
        }

        Shift sh = shiftDAO.selectById(shiftId);
        if (sh == null) {
            throw new IllegalArgumentException("No shift with ID " + shiftId);
        }

        Role role = roleDAO.findByName(roleName);
        if (role == null) {
            throw new IllegalArgumentException("No role named \"" + roleName + "\"");
        }

        // Create and persist the SwapRequest
        SwapRequest req = new SwapRequest(emp, sh, role);
        swapDAO.insert(req);
    }

    /**
     * 2. Cancel an existing swap request by its ID.
     *    (Originally: CancelSwapRequest(SwapRequest))
     */
    public void cancelSwapRequest(int requestId) {
        swapDAO.delete(requestId);
    }

    /**
     * 3. Accept two swap requests, swapping assignments accordingly.
     *    (Originally: AcceptSwapRequests(SwapRequest req1, SwapRequest req2))
     */
    public void acceptSwapRequests(int req1Id, int req2Id) {
        SwapRequest r1 = swapDAO.selectById(req1Id);
        SwapRequest r2 = swapDAO.selectById(req2Id);

        if (r1 == null || r2 == null) {
            throw new IllegalArgumentException("One or both swap requests not found");
        }

        Employee e1 = r1.getEmployee();
        Employee e2 = r2.getEmployee();
        Shift    s1 = r1.getShift();
        Shift    s2 = r2.getShift();
        Role     role = r1.getRole();

        // Remove existing assignments
        s1.getAssignedEmployees().removeIf(sa ->
                sa.getEmployeeId().equals(e1.getId()) && sa.getRole().equals(role));
        s2.getAssignedEmployees().removeIf(sa ->
                sa.getEmployeeId().equals(e2.getId()) && sa.getRole().equals(role));

        // Swap assignments
        s1.assignEmployee(e2, role);
        s2.assignEmployee(e1, role);

        // Persist updated shifts
        shiftDAO.update(s1);
        shiftDAO.update(s2);

        // Remove processed swap requests
        swapDAO.delete(req1Id);
        swapDAO.delete(req2Id);
    }

    /**
     * 4. Fetch all pending swap requests as DTOs.
     *    (Originally: getSwapRequests() returning List<SwapRequest>)
     */
    public List<SwapRequestDTO> getSwapRequests() {
        return swapDAO
                .selectAll()                       // List<SwapRequest>
                .stream()
                .map(SwapRequestMapper::toDTO)     // → SwapRequestDTO
                .collect(Collectors.toList());
    }
}
