package Transportation.Domain;

import HR.Domain.Employee;
import HR.Domain.Shift;

import java.util.Date;
import java.util.List;

public interface EmployeeProvider {
    List<Employee> findAvailableDrivers(String licenseType, Date date, Shift.ShiftTime shiftTime);
    boolean findAvailableWarehouseWorkers(Date date, Shift.ShiftTime shiftTime);
    void setAvailabilityDriver(Employee emp, boolean status);
}