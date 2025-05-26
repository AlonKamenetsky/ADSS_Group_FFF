package HR.Service;

import HR.DataAccess.WeeklyAvailabilityDAO;
import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Domain.Shift;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShiftService {

    private final WeeklyAvailabilityDAO.ShiftsRepo repo = WeeklyAvailabilityDAO.ShiftsRepo.getInstance();

    public void AssignEmployeeToShift(Shift shift, Employee employee, Role role) {
        shift.assignEmployee(employee, role);
        System.out.println("Employee " + employee.getName() + " assigned to role " +
                role.getName() + " in shift " + shift.getID());
    }



    public Shift getShiftsForDate(Shift.ShiftTime shiftTime, Date date) {
        repo.ensureUpToDate();
        List<Shift> shifts = new ArrayList<>();
        shifts.addAll(repo.getCurrentWeekShifts());
        shifts.addAll(repo.getNextWeekShifts());
        for (Shift shift : shifts) {
            if (repo.isSameDay(shift.getDate(), date)&&shift.getType()==shiftTime) {
                return shift;
            }
        }
        return null;
    }


    public void GenerateWeeklyShifts() {
        repo.ensureUpToDate();
    }



}
