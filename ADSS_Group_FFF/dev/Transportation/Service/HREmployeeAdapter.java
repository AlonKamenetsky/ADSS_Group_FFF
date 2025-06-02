package Transportation.Service;

import HR.Domain.DriverInfo;
import HR.Domain.Employee;
import HR.Domain.Shift;
import HR.Service.EmployeeService;
import HR.Service.ShiftService;
import Transportation.Domain.EmployeeProvider;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class HREmployeeAdapter implements EmployeeProvider {
    private final EmployeeService employeeService = EmployeeService.getInstance();
    private final ShiftService shiftService = ShiftService.getInstance();

    @Override
    public List<Employee> findAvailableDrivers(String licenseType, Date date, Shift.ShiftTime shiftTime) {
        return employeeService.findAvailableDrivers(DriverInfo.LicenseType.valueOf(licenseType), date, shiftTime);
    }

    @Override
    public boolean findAvailableWarehouseWorkers(Date date, Shift.ShiftTime shiftTime) {
        // find shiftId based on two params
        String shiftId = "";
        List<Shift> allShiftsDate = shiftService.getShiftsForDate(date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate());
        for (Shift s : allShiftsDate) {
            if(s.getType() == shiftTime) {
                 shiftId = s.getID();
            }
        }
        return shiftService.isWarehouseAssigned(shiftId);
    }

    @Override
    public void setAvailabilityDriver(Employee emp, boolean status) {
        employeeService.setAvailability(emp, status);
    }
}