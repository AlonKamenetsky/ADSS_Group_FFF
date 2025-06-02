package HR.Service;

import HR.DTO.DriverInfoDTO;
import HR.DTO.EmployeeDTO;
import HR.DTO.WeeklyAvailabilityDTO;
import HR.DTO.ShiftDTO;
import HR.DataAccess.DriverInfoDAO;
import HR.DataAccess.DriverInfoDAOImpl;
import HR.DataAccess.EmployeeDAO;
import HR.DataAccess.EmployeeDAOImpl;
import HR.DataAccess.ShiftDAO;
import HR.DataAccess.ShiftDAOImpl;
import HR.Domain.DriverInfo;
import HR.Domain.Employee;
import HR.Domain.Shift;
import HR.Mapper.DriverInfoMapper;
import HR.Mapper.EmployeeMapper;
import HR.Mapper.ShiftMapper;
import Util.Database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmployeeService {

    private static EmployeeService instance;
    private final EmployeeDAO employeeDAO;
    private final DriverInfoDAO driverInfoDAO;
    private final ShiftDAO shiftDAO;

    private EmployeeService() {
        var conn = Database.getConnection();
        employeeDAO   = new EmployeeDAOImpl(conn);
        driverInfoDAO = new DriverInfoDAOImpl(conn);
        shiftDAO      = new ShiftDAOImpl(conn);
    }

    public static synchronized EmployeeService getInstance() {
        if (instance == null) {
            instance = new EmployeeService();
        }
        return instance;
    }

    /*** 1. Basic Employee CRUD (DTO-based) ***/
    public List<EmployeeDTO> getEmployees() {
        return employeeDAO.selectAll()
                .stream()
                .map(EmployeeMapper::toDTO)
                .toList();
    }

    public EmployeeDTO getEmployeeById(String id) {
        if (id == null || id.isEmpty()) return null;
        Employee e = employeeDAO.selectById(id);
        return (e == null ? null : EmployeeMapper.toDTO(e));
    }

    public void addEmployee(EmployeeDTO dto) {
        if (dto == null) throw new IllegalArgumentException("EmployeeDTO must not be null");
        Employee e = EmployeeMapper.fromDTO(dto);
        employeeDAO.insert(e);
    }

    public void addEmployee(EmployeeDTO dto, List<DriverInfo.LicenseType> licenseTypes) {
        if (dto == null) throw new IllegalArgumentException("EmployeeDTO must not be null");
        if (licenseTypes == null || licenseTypes.isEmpty()) {
            throw new IllegalArgumentException("Driver must have at least one LicenseType");
        }
        Employee e = EmployeeMapper.fromDTO(dto);
        employeeDAO.insert(e);
        DriverInfo di = new DriverInfo(e.getId(), licenseTypes);
        driverInfoDAO.insert(di);
    }

    public void updateEmployee(EmployeeDTO dto) {
        if (dto == null) throw new IllegalArgumentException("EmployeeDTO must not be null");
        Employee updated = EmployeeMapper.fromDTO(dto);
        employeeDAO.update(updated);
    }

    public void removeEmployee(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Employee ID must not be null/empty");
        }
        DriverInfo existing = driverInfoDAO.getByEmployeeId(id);
        if (existing != null) {
            driverInfoDAO.delete(id);
        }
        employeeDAO.delete(id);
    }

    /*** 2. Driver-info management (DTO-based) ***/
    public void updateDriverInfo(DriverInfoDTO dto) {
        if (dto == null) throw new IllegalArgumentException("DriverInfoDTO must not be null");
        String empId = dto.getEmployeeId();
        if (empId == null || empId.isEmpty()) {
            throw new IllegalArgumentException("DriverInfoDTO must carry a valid employeeId");
        }

        // Convert List<String> → List<LicenseType>
        List<DriverInfo.LicenseType> newLicenses = dto.getLicenseType().stream()
                .map(s -> {
                    try {
                        return DriverInfo.LicenseType.valueOf(s.trim().toUpperCase());
                    } catch (IllegalArgumentException ex) {
                        throw new IllegalArgumentException(
                                "Invalid license‐type \"" + s + "\" for employee " + empId);
                    }
                })
                .toList();

        DriverInfo existing = driverInfoDAO.getByEmployeeId(empId);

        if (!newLicenses.isEmpty()) {
            if (existing == null) {
                // Insert new row
                DriverInfo di = DriverInfoMapper.fromDTO(dto);
                driverInfoDAO.insert(di);
            } else {
                // Update existing row
                existing.setLicenses(newLicenses);
                driverInfoDAO.update(existing);
            }
        } else {
            // newLicenses empty ⇒ delete any existing row
            if (existing != null) {
                driverInfoDAO.delete(empId);
            }
        }
    }

    public List<DriverInfo.LicenseType> getDriverLicenses(String employeeId) {
        if (employeeId == null || employeeId.isEmpty()) {
            throw new IllegalArgumentException("Employee ID must not be null/empty");
        }
        DriverInfo di = driverInfoDAO.getByEmployeeId(employeeId);
        if (di == null) {
            throw new IllegalArgumentException("No driver info for employee " + employeeId);
        }
        return di.getLicenses();
    }

    public DriverInfoDTO getDriverInfo(String employeeId) {
        DriverInfo di = driverInfoDAO.getByEmployeeId(employeeId);
        return (di == null ? null : DriverInfoMapper.toDTO(di));
    }

    public boolean isDriver(String employeeId) {
        EmployeeDTO dto = getEmployeeById(employeeId);
        if (dto == null) return false;
        return dto.getRoles().stream()
                .anyMatch(r -> r.equals("Driver"));
    }

    /*** 3. Availability ***/
    public List<WeeklyAvailabilityDTO> getEmployeeAvailabilityNextWeek(String employeeId) {
        EmployeeDTO dto = getEmployeeById(employeeId);
        if (dto == null) return List.of();
        return dto.getAvailabilityNextWeek();
    }

    public void updateEmployeeAvailabilityNextWeek(
            String employeeId,
            List<WeeklyAvailabilityDTO> newAvailabilities
    ) {
        EmployeeDTO dto = getEmployeeById(employeeId);
        if (dto == null) {
            throw new IllegalArgumentException("No employee with ID " + employeeId);
        }
        dto.setAvailabilityNextWeek(newAvailabilities);
        updateEmployee(dto);
    }

    /*** 4. Shifts ***/
    public List<ShiftDTO> getEmployeeShifts(String employeeId) {
        List<Shift> shifts = shiftDAO.getShiftsByEmployeeId(employeeId);
        return shifts.stream().map(ShiftMapper::toDTO).toList();
    }

    /*** 5. Holidays/Vacations ***/
    public List<Date> getEmployeeHolidays(String employeeId) {
        EmployeeDTO dto = getEmployeeById(employeeId);
        if (dto == null) {
            throw new IllegalArgumentException("No employee with ID " + employeeId);
        }
        return dto.getHolidays();
    }

    public void addVacation(String employeeId, Date newHoliday) {
        EmployeeDTO dto = getEmployeeById(employeeId);
        if (dto == null) {
            throw new IllegalArgumentException("No employee with ID " + employeeId);
        }
        var holidays = dto.getHolidays();
        if (holidays == null) {
            holidays = new ArrayList<>();
        }
        if (!holidays.contains(newHoliday)) {
            holidays.add(newHoliday);
        }
        dto.setHolidays(holidays);
        updateEmployee(dto);
    }

    /*** 6. Finding available drivers ***/
    public List<EmployeeDTO> findAvailableDrivers(
            String licenseTypeString,
            Date date,
            String shiftTimeString
    ) {
        DriverInfo.LicenseType licenseType = DriverInfo.LicenseType.valueOf(licenseTypeString.trim().toUpperCase());
        Shift.ShiftTime shiftTime         = Shift.ShiftTime.valueOf(shiftTimeString.trim().toUpperCase());

        List<Employee> allEmps = employeeDAO.selectAll();
        return allEmps.stream()
                // only employees whose roles include “Driver”
                .filter(emp -> emp.getRoles()
                        .stream()
                        .anyMatch(r -> r.getName().equalsIgnoreCase("Driver")))
                // license match
                .filter(emp -> {
                    DriverInfo di = driverInfoDAO.getByEmployeeId(emp.getId());
                    return di != null && di.getLicenses().contains(licenseType);
                })
                // not on holiday
                .filter(emp -> !emp.getHolidays().contains(date))
                // is available on that date+shiftTime
                .filter(emp -> emp.getAvailabilityThisWeek().isEmpty()
                        || emp.isAvailable(date, shiftTime))
                // map to DTO
                .map(EmployeeMapper::toDTO)
                .toList();
    }

    /*** (Optional) 7. Helper for mapping an integer → LicenseType ***/
    public DriverInfo.LicenseType parseLicenseType(Integer type) {
        return switch (type) {
            case 1 -> DriverInfo.LicenseType.B;
            case 2 -> DriverInfo.LicenseType.C;
            case 3 -> DriverInfo.LicenseType.C1;
            default -> throw new IllegalArgumentException("Invalid license type: " + type);
        };
    }

    public void setPassword(String employeeId, String newPassword) {
        if (employeeId == null || employeeId.isEmpty()) {
            throw new IllegalArgumentException("Employee ID must not be null/empty");
        }
        Employee domainEmp = employeeDAO.selectById(employeeId);
        if (domainEmp == null) {
            throw new IllegalArgumentException("No employee with ID " + employeeId);
        }
        domainEmp.setPassword(newPassword);
        employeeDAO.update(domainEmp);
    }

    public List<WeeklyAvailabilityDTO> getEmployeeAvailabilityThisWeek(String employeeId) {
    EmployeeDTO dto = getEmployeeById(employeeId);
        if (dto == null) {
            throw new IllegalArgumentException("No employee with ID " + employeeId);
        }
        return dto.getAvailabilityThisWeek();
    }

    public void setAvailability(Employee driver, boolean status) {
        DriverInfo driverInfo = getDriverInfo(driver);
        if (driverInfo != null) {
            driverInfo.setAvaiable(status);
            driverInfoDAO.update(driverInfo);
        } else {
            throw new IllegalArgumentException("Employee is not a driver or has no driver info.");
        }
    }
}