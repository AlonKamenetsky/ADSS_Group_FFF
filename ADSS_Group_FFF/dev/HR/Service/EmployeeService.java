package HR.Service;

import HR.DataAccess.DriverInfoDAO;
import HR.DataAccess.EmployeeDAO;
import HR.DataAccess.ShiftDAO;
import HR.DTO.EmployeeDTO;

import java.time.LocalDate;
import java.util.List;

public class EmployeeService {

    private final EmployeeDAO employeeDAO;
    private final DriverInfoDAO driverInfoDAO;
    private final ShiftDAO shiftDAO;

    public EmployeeService(EmployeeDAO employeeDAO, DriverInfoDAO driverInfoDAO, ShiftDAO shiftDAO) {
        this.employeeDAO = employeeDAO;
        this.driverInfoDAO = driverInfoDAO;
        this.shiftDAO = shiftDAO;
    }

    public List<LocalDate> getEmployeeHolidays(String employeeId) {
        EmployeeDTO dto = getEmployeeById(employeeId);
        if (dto == null) {
            throw new IllegalArgumentException("No employee with ID " + employeeId);
        }
        return dto.getHolidays();
    }

    public void addVacation(String employeeId, LocalDate newHoliday) {
        EmployeeDTO dto = getEmployeeById(employeeId);
        if (dto == null) {
            throw new IllegalArgumentException("No employee with ID " + employeeId);
        }
        List<LocalDate> holidays = dto.getHolidays();
        if (!holidays.contains(newHoliday)) {
            holidays.add(newHoliday);
        }
        dto.setHolidays(holidays);
        updateEmployee(dto);
    }

    private EmployeeDTO getEmployeeById(String id) {
        return employeeDAO.selectById(id) != null ? HR.Mapper.EmployeeMapper.toDTO(employeeDAO.selectById(id)) : null;
    }

    private void updateEmployee(EmployeeDTO dto) {
        employeeDAO.update(HR.Mapper.EmployeeMapper.fromDTO(dto));
    }
}
