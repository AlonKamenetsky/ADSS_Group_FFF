package HR.DataAccess;

import HR.Domain.Shift;
import HR.Domain.Employee;

import java.time.LocalDate;
import java.util.List;

public interface ShiftDAO {
    void insert(Shift shift);
    void update(Shift shift);
    void delete(String shiftId);
    Shift selectById(String shiftId);
    List<Shift> selectAll();
    List<Shift> getShiftsByDate(LocalDate date);
    List<Shift> getCurrentWeekShifts();
    List<Employee> findAssignedEmployees(String shiftId);
}
