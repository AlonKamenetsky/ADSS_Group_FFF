package HR.DataAccess;

import HR.Domain.Shift;
import HR.Domain.Employee;
import HR.Domain.WeeklyShiftsSchedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShiftDAO {
    void insert(Shift shift);
    void update(Shift shift);
    void delete(String shiftId);
    Shift selectById(String shiftId);
    List<Shift> selectAll();

    List<Shift> getShiftsByDate(LocalDate date);
    List<Shift> getCurrentWeekShifts();
    List<Shift> getNextWeekShifts(); // <-- Implement this
    Optional<Shift> getCurrentShift(); // <-- Implement this
    WeeklyShiftsSchedule getSchedule(); // <-- Optional based on your architecture
    List<Employee> findAssignedEmployees(String shiftId);
    boolean isWarehouseEmployeeAssigned(String shiftId);
    List<Shift> getShiftsByEmployeeId(String employeeId);
}

