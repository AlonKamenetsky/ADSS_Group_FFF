//package HR.Service;
//
//import HR.DataAccess.SwapDAO;
//import HR.DataAccess.SwapDAOImpl;
//import HR.Domain.*;
//import Util.Database;
//
//import java.sql.Connection;
//import java.util.List;
//
//public class SwapService {
//    private static SwapService instance;
//    private final SwapDAO swapDAO;
//
//    private SwapService() {
//        Connection conn = Database.getConnection();
//        this.swapDAO = new SwapDAOImpl(conn);
//    }
//
//    public static SwapService getInstance() {
//        if (instance == null) {
//            instance = new SwapService();
//        }
//        return instance;
//    }
//    public void SendSwapRequest(Employee employee, Shift shift, Role role) {
//        SwapRequest request = new SwapRequest(employee, shift, role);
//        swapDAO.insert(request);
//    }
//
//    public void CancelSwapRequest(SwapRequest request) {
//        swapDAO.delete(request.getId());
//    }
//
//    public void AcceptSwapRequests(SwapRequest req1,SwapRequest req2) {
//        Employee e1 = req1.getEmployee();
//        Employee e2 = req2.getEmployee();
//        Shift  s1 = req1.getShift();
//        Shift  s2 = req2.getShift();
//        Role   r  = req1.getRole();
//
//        // Update assignedEmployees lists:
//        s1.getAssignedEmployees().removeIf(sa -> sa.getEmployeeId().equals(e1.getId()) && sa.getRole().equals(r));
//        s2.getAssignedEmployees().removeIf(sa -> sa.getEmployeeId().equals(e2.getId()) && sa.getRole().equals(r));
//        s1.assignEmployee(e2, r);
//        s2.assignEmployee(e1, r);
//
//        System.out.printf("Swapped %s and %s for role %s between shifts %s and %s.%n",
//                e1.getName(), e2.getName(), r.getName(), s1.getID(), s2.getID());
//
//        // Remove processed requests
//        swapDAO.delete(req1.getId());
//        swapDAO.delete(req2.getId());
//
//    }
//
//
//    public List<SwapRequest> getSwapRequests(){
//        return swapDAO.selectAll();
//    }
//
//
//
//}
