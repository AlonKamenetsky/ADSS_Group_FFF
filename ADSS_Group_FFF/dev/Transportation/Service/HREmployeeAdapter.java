package Transportation.Service;

import HR.Domain.Employee;
import HR.Domain.Shift;
import HR.Service.EmployeeService;
import HR.Service.ShiftService;
import Transportation.Domain.EmployeeProvider;

import java.util.Date;
import java.util.List;

public class HREmployeeAdapter implements EmployeeProvider {
    private final EmployeeService employeeService = EmployeeService.getInstance();
    private final ShiftService shiftService = ShiftService.getInstance();

    @Override
    public List<Employee> findAvailableDrivers(String licenseType, Date date, Shift.ShiftTime shiftTime) {
        return employeeService.findAvailableDrivers(licenseType, date, shiftTime);
    }

//    @Override
//    public boolean findAvailableWarehouseWorkers(Date date, Shift.ShiftTime shiftTime) {
//        // find shiftId based on two params
//        return shiftService.isWarehouseAssigned("Warehouse Worker", date, shiftTime);
//    }
}
