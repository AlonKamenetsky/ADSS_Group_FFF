package Transportation.Service;

import HR.DTO.EmployeeDTO;
import HR.DTO.ShiftDTO;
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
    public List<EmployeeDTO> findAvailableDrivers(String licenseType, Date date, String shiftTime) {
        return employeeService.findAvailableDrivers(licenseType, date, shiftTime);
    }

    @Override
    public boolean findAvailableWarehouseWorkers(Date date, Shift.ShiftTime shiftTime) {
        // find shiftId based on two params
        String shiftId = "";
        List<ShiftDTO> allShiftsDate = shiftService.getShiftsForDate(date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate());
        for (ShiftDTO s : allShiftsDate) {
            if(s.getType() == shiftTime) {
                 shiftId = s.getId();
            }
        }
        return shiftService.isWarehouseAssigned(shiftId);
    }

    public String getShiftIdByDateTime(Date date, String time) {
        return shiftService.getShiftIdByDateAndTime((java.sql.Date) date, time);
    }
}