package HR.Service;

import HR.DataAccess.ShiftDAOImpl;
import HR.Domain.*;

import java.util.List;

public class SwapService {
    private static SwapService instance;
    private final ShiftDAOImpl.SwapRequestsRepo repo;

    private SwapService() {
        repo = ShiftDAOImpl.SwapRequestsRepo.getInstance();
    }

    public static SwapService getInstance() {
        if (instance == null) {
            instance = new SwapService();
        }
        return instance;
    }
    public void SendSwapRequest(Employee employee, Shift shift, Role role) {
        SwapRequest request = new SwapRequest(employee, shift, role);
        repo.addSwapRequest(request);
    }

    public void CancelSwapRequest(SwapRequest request) {
        repo.removeSwapRequest(request);
    }

    public void AcceptSwapRequests(SwapRequest req1,SwapRequest req2) {
        Employee e1 = req1.getEmployee();
        Employee e2 = req2.getEmployee();
        Shift  s1 = req1.getShift();
        Shift  s2 = req2.getShift();
        Role   r  = req1.getRole();

        // Update assignedEmployees lists:
        s1.getAssignedEmployees().removeIf(sa -> sa.getEmployeeId().equals(e1.getId()) && sa.getRole().equals(r));
        s2.getAssignedEmployees().removeIf(sa -> sa.getEmployeeId().equals(e2.getId()) && sa.getRole().equals(r));
        s1.assignEmployee(e2, r);
        s2.assignEmployee(e1, r);

        System.out.printf("Swapped %s and %s for role %s between shifts %s and %s.%n",
                e1.getName(), e2.getName(), r.getName(), s1.getID(), s2.getID());

        // Remove processed requests
        repo.removeSwapRequest(req1);
        repo.removeSwapRequest(req2);

    }


    public List<SwapRequest> getSwapRequests(){
        return repo.getSwapRequests();
    }



}
