package HR.Service;

import HR.DTO.SwapRequestDTO;
import HR.DataAccess.*;
import HR.Domain.*;
import HR.Mapper.SwapRequestMapper;

import java.util.List;
import java.util.stream.Collectors;

public class SwapService {

    private final SwapDAO swapDAO;
    private final EmployeeDAO employeeDAO;
    private final ShiftDAO shiftDAO;
    private final RoleDAO roleDAO;

    public SwapService(SwapDAO swapDAO, EmployeeDAO employeeDAO, ShiftDAO shiftDAO, RoleDAO roleDAO) {
        this.swapDAO = swapDAO;
        this.employeeDAO = employeeDAO;
        this.shiftDAO = shiftDAO;
        this.roleDAO = roleDAO;
    }

    public void sendSwapRequest(String employeeId, String shiftId, String roleName) {
        Employee emp = employeeDAO.selectById(employeeId);
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

        SwapRequest req = new SwapRequest(emp, sh, role);
        swapDAO.insert(req);
    }

    public void cancelSwapRequest(int requestId) {
        swapDAO.delete(requestId);
    }

    public void acceptSwapRequests(int req1Id, int req2Id) {
        SwapRequest r1 = swapDAO.selectById(req1Id);
        SwapRequest r2 = swapDAO.selectById(req2Id);

        if (r1 == null || r2 == null) {
            throw new IllegalArgumentException("One or both swap requests not found");
        }

        Employee e1 = r1.getEmployee();
        Employee e2 = r2.getEmployee();
        Shift s1 = r1.getShift();
        Shift s2 = r2.getShift();
        Role role = r1.getRole();

        s1.getAssignedEmployees().removeIf(sa ->
                sa.getEmployeeId().equals(e1.getId()) && sa.getRole().equals(role));
        s2.getAssignedEmployees().removeIf(sa ->
                sa.getEmployeeId().equals(e2.getId()) && sa.getRole().equals(role));

        s1.assignEmployee(e2, role);
        s2.assignEmployee(e1, role);

        shiftDAO.update(s1);
        shiftDAO.update(s2);

        swapDAO.delete(req1Id);
        swapDAO.delete(req2Id);
    }

    public List<SwapRequestDTO> getSwapRequests() {
        return swapDAO.selectAll().stream()
                .map(SwapRequestMapper::toDTO)
                .collect(Collectors.toList());
    }
}
