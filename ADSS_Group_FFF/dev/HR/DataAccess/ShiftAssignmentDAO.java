package HR.DataAccess;

import java.util.List;

public interface ShiftAssignmentDAO {
    void assignEmployeeToShift(String shiftId, String employeeId, String roleName);
    void removeAssignment(String shiftId, String employeeId, String roleName);
    List<String> getEmployeesAssignedToShift(String shiftId);
}
