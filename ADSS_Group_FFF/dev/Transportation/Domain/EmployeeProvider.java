package Transportation.Domain;

import HR.DTO.EmployeeDTO;
import HR.Domain.Shift;

import java.util.Date;
import java.util.List;

public interface EmployeeProvider {
    List<EmployeeDTO> findAvailableDrivers(String licenseType, Date date, String shiftTime);
    boolean findAvailableWarehouseWorkers(Date date, Shift.ShiftTime shiftTime);
    String getShiftIdByDateTime(Date date, String time);
    }