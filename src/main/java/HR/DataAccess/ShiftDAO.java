package HR.DataAccess;

import HR.Domain.Shift;
import HR.Domain.Employee;
import HR.Domain.WeeklyShiftsSchedule;

import java.sql.Date;
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
    List<Shift> getNextWeekShifts();
    Optional<Shift> getCurrentShift();
    WeeklyShiftsSchedule getSchedule();
    List<Employee> findAssignedEmployees(String shiftId);
    boolean isWarehouseEmployeeAssigned(String shiftId);
    List<Shift> getShiftsByEmployeeId(String employeeId);
    String getShiftIdByDateAndTime(java.sql.Date date, String time);

}

