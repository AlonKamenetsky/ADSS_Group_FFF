package HR.Service;

import HR.DTO.*;
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
import java.util.Objects;

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

    /*** 1. ---- READ operations ---- ***/

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

    /*** 2. ---- CREATE operations ---- ***/

    /**
     * Adds a non‐driver employee.  Use this when the new employee does not need any licenses.
     */
    public void addEmployee(CreateEmployeeDTO createDto) {
        if (createDto == null) {
            throw new IllegalArgumentException("CreateEmployeeDTO must not be null");
        }
        if (createDto.getRawPassword() == null || createDto.getRawPassword().isBlank()) {
            throw new IllegalArgumentException("New employee must have a non‐empty rawPassword");
        }

        // 1) Build domain Employee (password = null for now)
        Employee e = new Employee(
                createDto.getId(),
                createDto.getRoles().stream()
                        .map(rdto -> new HR.Domain.Role(rdto.getName()))
                        .toList(),
                createDto.getName(),
                null,
                createDto.getBankAccount(),
                createDto.getSalary(),
                createDto.getEmploymentDate()
        );

        // 2) Hash & set password
        String hashed = hashPassword(createDto.getRawPassword());
        e.setPassword(hashed);

        // 3) Populate availabilityThisWeek
        if (createDto.getAvailabilityThisWeek() != null) {
            for (var waDto : createDto.getAvailabilityThisWeek()) {
                e.getAvailabilityThisWeek().add(
                        new HR.Domain.WeeklyAvailability(waDto.getDay(), waDto.getTime())
                );
            }
        }

        // 4) Populate availabilityNextWeek
        if (createDto.getAvailabilityNextWeek() != null) {
            for (var waDto : createDto.getAvailabilityNextWeek()) {
                e.getAvailabilityNextWeek().add(
                        new HR.Domain.WeeklyAvailability(waDto.getDay(), waDto.getTime())
                );
            }
        }

        // 5) Populate holidays
        if (createDto.getHolidays() != null) {
            e.getHolidays().addAll(createDto.getHolidays());
        }

        // 6) Insert into employees table
        employeeDAO.insert(e);
    }

    /**
     * Adds a new employee who is also a driver.  The extra licenseTypes list
     * will be stored in DriverInfo after the Employee row is created.
     */
    public void addEmployee(CreateEmployeeDTO createDto, List<DriverInfo.LicenseType> licenseTypes) {
        if (createDto == null) {
            throw new IllegalArgumentException("CreateEmployeeDTO must not be null");
        }
        if (createDto.getRawPassword() == null || createDto.getRawPassword().isBlank()) {
            throw new IllegalArgumentException("New driver must have a non‐empty rawPassword");
        }
        if (licenseTypes == null || licenseTypes.isEmpty()) {
            throw new IllegalArgumentException("Driver must have at least one license type");
        }

        // 1) Build domain Employee (password = null for now)
        Employee e = new Employee(
                createDto.getId(),
                createDto.getRoles().stream()
                        .map(rdto -> new HR.Domain.Role(rdto.getName()))
                        .toList(),
                createDto.getName(),
                null,
                createDto.getBankAccount(),
                createDto.getSalary(),
                createDto.getEmploymentDate()
        );

        // 2) Hash & set password
        String hashed = hashPassword(createDto.getRawPassword());
        e.setPassword(hashed);

        // 3) Populate availabilityThisWeek
        if (createDto.getAvailabilityThisWeek() != null) {
            for (var waDto : createDto.getAvailabilityThisWeek()) {
                e.getAvailabilityThisWeek().add(
                        new HR.Domain.WeeklyAvailability(waDto.getDay(), waDto.getTime())
                );
            }
        }

        // 4) Populate availabilityNextWeek
        if (createDto.getAvailabilityNextWeek() != null) {
            for (var waDto : createDto.getAvailabilityNextWeek()) {
                e.getAvailabilityNextWeek().add(
                        new HR.Domain.WeeklyAvailability(waDto.getDay(), waDto.getTime())
                );
            }
        }

        // 5) Populate holidays
        if (createDto.getHolidays() != null) {
            e.getHolidays().addAll(createDto.getHolidays());
        }

        // 6) Insert into employees table
        employeeDAO.insert(e);

        // 7) Insert driver-specific licenses into driver_info table
        DriverInfo di = new DriverInfo(e.getId(), licenseTypes);
        driverInfoDAO.insert(di);
    }

    /*** 3. ---- UPDATE an existing employee (do not overwrite password) ---- ***/
    public void updateEmployee(EmployeeDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("EmployeeDTO must not be null");
        }

        // 1) Fetch existing Employee from DB (so we keep their password)
        Employee existing = employeeDAO.selectById(dto.getId());
        if (existing == null) {
            throw new IllegalArgumentException("No employee with ID " + dto.getId());
        }

        // 2) Overwrite only non‐password fields
        existing.setName(dto.getName());
        existing.setBankAccount(dto.getBankAccount());
        existing.setSalary(dto.getSalary());
        existing.setEmploymentDate(dto.getEmploymentDate());

        // Update roles
        existing.getRoles().clear();
        dto.getRoles().forEach(rdto ->
                existing.getRoles().add(new HR.Domain.Role(rdto.getName()))
        );

        // Update availabilityThisWeek
        existing.getAvailabilityThisWeek().clear();
        if (dto.getAvailabilityThisWeek() != null) {
            for (var waDto : dto.getAvailabilityThisWeek()) {
                existing.getAvailabilityThisWeek().add(
                        new HR.Domain.WeeklyAvailability(waDto.getDay(), waDto.getTime())
                );
            }
        }

        // Update availabilityNextWeek
        existing.getAvailabilityNextWeek().clear();
        if (dto.getAvailabilityNextWeek() != null) {
            for (var waDto : dto.getAvailabilityNextWeek()) {
                existing.getAvailabilityNextWeek().add(
                        new HR.Domain.WeeklyAvailability(waDto.getDay(), waDto.getTime())
                );
            }
        }

        // Update holidays
        existing.getHolidays().clear();
        if (dto.getHolidays() != null) {
            existing.getHolidays().addAll(dto.getHolidays());
        }

        // 3) Persist changes (password field remains unchanged)
        employeeDAO.update(existing);
    }

    /*** 4. ---- DELETE employee (and driver info if present) ---- ***/
    public void removeEmployee(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Employee ID must not be null/empty");
        }
        DriverInfo existingDriver = driverInfoDAO.getByEmployeeId(id);
        if (existingDriver != null) {
            driverInfoDAO.delete(id);
        }
        employeeDAO.delete(id);
    }

    /*** 5. ---- Password‐only update helper ---- ***/
    public void setPassword(String employeeId, String newRawPassword) {
        if (employeeId == null || employeeId.isEmpty()) {
            throw new IllegalArgumentException("Employee ID must not be null or empty");
        }
        if (newRawPassword == null || newRawPassword.isBlank()) {
            throw new IllegalArgumentException("New password must not be null or blank");
        }

        // Fetch domain Employee, set new password, update:
        Employee domainEmp = employeeDAO.selectById(employeeId);
        if (domainEmp == null) {
            throw new IllegalArgumentException("No employee with ID " + employeeId);
        }
        domainEmp.setPassword(hashPassword(newRawPassword));
        employeeDAO.update(domainEmp);
    }

    /*** 6. ---- Driver‐info management ---- ***/
    public void updateDriverInfo(DriverInfoDTO dto) {
        if (dto == null) throw new IllegalArgumentException("DriverInfoDTO must not be null");
        String empId = dto.getEmployeeId();
        if (empId == null || empId.isEmpty()) {
            throw new IllegalArgumentException("DriverInfoDTO must carry a valid employeeId");
        }

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
                DriverInfo di = DriverInfoMapper.fromDTO(dto);
                driverInfoDAO.insert(di);
            } else {
                existing.setLicenses(newLicenses);
                driverInfoDAO.update(existing);
            }
        } else {
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
                .anyMatch(r -> r.getName().equalsIgnoreCase("Driver"));
    }

    /*** 7. ---- Availability & Shifts ---- ***/
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

    public List<ShiftDTO> getEmployeeShifts(String employeeId) {
        List<Shift> shifts = shiftDAO.getShiftsByEmployeeId(employeeId);
        return shifts.stream().map(ShiftMapper::toDTO).toList();
    }

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

    public List<EmployeeDTO> findAvailableDrivers(
            String licenseTypeString,
            Date date,
            String shiftTimeString
    ) {
        DriverInfo.LicenseType licenseType = DriverInfo.LicenseType.valueOf(
                licenseTypeString.trim().toUpperCase()
        );
        Shift.ShiftTime shiftTime = Shift.ShiftTime.valueOf(
                shiftTimeString.trim().toUpperCase()
        );

        List<Employee> allEmps = employeeDAO.selectAll();
        return allEmps.stream()
                .filter(emp -> emp.getRoles().stream()
                        .anyMatch(r -> r.getName().equalsIgnoreCase("Driver")))
                .filter(emp -> {
                    DriverInfo di = driverInfoDAO.getByEmployeeId(emp.getId());
                    return di != null && di.getLicenses().contains(licenseType);
                })
                .filter(emp -> !emp.getHolidays().contains(date))
                .filter(emp -> emp.getAvailabilityThisWeek().isEmpty()
                        || emp.isAvailable(date, shiftTime))
                .map(EmployeeMapper::toDTO)
                .toList();
    }

    public DriverInfo.LicenseType parseLicenseType(Integer type) {
        return switch (type) {
            case 1 -> DriverInfo.LicenseType.B;
            case 2 -> DriverInfo.LicenseType.C;
            case 3 -> DriverInfo.LicenseType.C1;
            default -> throw new IllegalArgumentException("Invalid license type: " + type);
        };
    }

    /*** 8. ---- Simple placeholder for password hashing ---- ***/
    private String hashPassword(String raw) {
        // Replace this stub with a real hashing function (e.g. BCrypt).
        return Objects.requireNonNull(raw).trim();
    }

    public List<WeeklyAvailabilityDTO> getEmployeeAvailabilityThisWeek(String employeeId) {
        EmployeeDTO dto = getEmployeeById(employeeId);
        if (dto == null) {
            throw new IllegalArgumentException("No employee with ID " + employeeId);
        }
        return dto.getAvailabilityThisWeek();
    }
}
