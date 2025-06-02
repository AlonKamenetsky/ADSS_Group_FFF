package HR.Service;

import HR.DTO.RoleDTO;
import HR.DTO.ShiftDTO;
import HR.DTO.ShiftTemplateDTO;
import HR.DTO.WeeklyShiftsScheduleDTO;
import HR.DataAccess.EmployeeDAO;
import HR.DataAccess.EmployeeDAOImpl;
import HR.DataAccess.RoleDAO;
import HR.DataAccess.RoleDAOImpl;
import HR.DataAccess.ShiftDAO;
import HR.DataAccess.ShiftDAOImpl;
import HR.DataAccess.ShiftTemplateDAO;
import HR.DataAccess.ShiftTemplateDAOImpl;
import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.ShiftTemplate;
import HR.Domain.WeeklyShiftsSchedule;
import HR.Mapper.RoleMapper;
import HR.Mapper.ShiftMapper;
import HR.Mapper.ShiftTemplateMapper;
import HR.Mapper.WeeklyShiftsScheduleMapper;
import Util.Database;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ShiftService {

    private static ShiftService instance;

    // DAOs for domain access
    private final ShiftDAO shiftDAO;
    private final ShiftTemplateDAO templateDAO;
    private final EmployeeDAO employeeDAO;
    private final RoleDAO roleDAO;

    private ShiftService() {
        var conn = Database.getConnection();
        shiftDAO    = new ShiftDAOImpl(conn);
        templateDAO = new ShiftTemplateDAOImpl(conn);
        employeeDAO = new EmployeeDAOImpl(conn);
        roleDAO     = new RoleDAOImpl(conn);
    }

    public static synchronized ShiftService getInstance() {
        if (instance == null) {
            instance = new ShiftService();
        }
        return instance;
    }

    // ------------------------------------------------------------
    // 1. getShiftById(String) → ShiftDTO
    // ------------------------------------------------------------
    public ShiftDTO getShiftById(String shiftId) {
        if (shiftId == null || shiftId.isEmpty()) {
            return null;
        }
        Shift s = shiftDAO.selectById(shiftId);
        return (s == null ? null : ShiftMapper.toDTO(s));
    }

    // ------------------------------------------------------------
    // 2. assignEmployeeToShift
    //    (originally: AssignEmployeeToShift(Shift, Employee, Role))
    // ------------------------------------------------------------
    public void assignEmployeeToShift(
            String shiftId,
            String employeeId,
            String roleName
    ) {
        // 1) Fetch domain objects
        Shift    shift = shiftDAO.selectById(shiftId);
        Employee emp   = employeeDAO.selectById(employeeId);
        Role     role  = roleDAO.findByName(roleName);

        if (shift == null) {
            throw new IllegalArgumentException("No shift with ID " + shiftId);
        }
        if (emp == null) {
            throw new IllegalArgumentException("No employee with ID " + employeeId);
        }
        if (role == null) {
            throw new IllegalArgumentException("No role named \"" + roleName + "\"");
        }

        // 2) Assign and persist
        shift.assignEmployee(emp, role);
        shiftDAO.update(shift);
    }

    // ------------------------------------------------------------
    // 3. getShiftsForDate(LocalDate) → List<ShiftDTO>
    // ------------------------------------------------------------
    public List<ShiftDTO> getShiftsForDate(LocalDate date) {
        return shiftDAO
                .getShiftsByDate(date)           // List<Shift>
                .stream()
                .map(ShiftMapper::toDTO)         // → ShiftDTO
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------
    // 4. getCurrentWeekShifts() → List<ShiftDTO>
    // ------------------------------------------------------------
    public List<ShiftDTO> getCurrentWeekShifts() {
        return shiftDAO
                .getCurrentWeekShifts()           // List<Shift>
                .stream()
                .map(ShiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------
    // 5. getNextWeekShifts() → List<ShiftDTO>
    // ------------------------------------------------------------
    public List<ShiftDTO> getNextWeekShifts() {
        return shiftDAO
                .getNextWeekShifts()             // List<Shift>
                .stream()
                .map(ShiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------
    // 6. configureShiftRoles
    //    (originally: ConfigureShiftRoles(Shift, Map<Role, Integer>))
    // ------------------------------------------------------------
    public void configureShiftRoles(
            String shiftId,
            Map<String, Integer> requiredRoleCounts
    ) {
        // 1) Fetch domain Shift
        Shift shift = shiftDAO.selectById(shiftId);
        if (shift == null) {
            throw new IllegalArgumentException("No shift with ID " + shiftId);
        }

        // 2) Look up “Shift Manager” and “HR Manager” roles by name (domain Role)
        Role managerRole = roleDAO.findByName("Shift Manager");
        Role hrRole      = roleDAO.findByName("HR Manager");
        if (managerRole == null || hrRole == null) {
            throw new IllegalStateException("Required roles not found in DB");
        }

        // 3) First, clear any existing requiredCounts/requiredRoles
        shift.getRequiredCounts().clear();
        shift.getRequiredRoles().clear();

        // 4) Always put “Shift Manager” = 1
        shift.getRequiredCounts().put(managerRole, 1);
        shift.getRequiredRoles().put(managerRole, new ArrayList<>());

        // 5) Now iterate over provided map (roleName → count)
        for (Map.Entry<String, Integer> entry : requiredRoleCounts.entrySet()) {
            String roleName = entry.getKey();
            int    count    = entry.getValue();
            if (roleName.equalsIgnoreCase("Shift Manager") ||
                    roleName.equalsIgnoreCase("HR Manager")) {
                // skip, since we already handled Shift Manager,
                // and we do not configure “HR Manager” here
                continue;
            }

            Role r = roleDAO.findByName(roleName);
            if (r == null) {
                throw new IllegalArgumentException("Role \"" + roleName + "\" not found");
            }
            shift.getRequiredCounts().put(r, count);
            shift.getRequiredRoles().put(r, new ArrayList<>());
        }

        // 6) Persist
        shiftDAO.update(shift);
    }

    // ------------------------------------------------------------
    // 7. getAssignedShifts(employeeId) → List<ShiftDTO>
    //    (originally: getAssignedShifts(Employee))
    // ------------------------------------------------------------
    public List<ShiftDTO> getAssignedShifts(String employeeId) {
        // 1) Fetch all current‐week shifts
        List<Shift> all = shiftDAO.getCurrentWeekShifts();
        // 2) Filter to those where assignedEmployees contains this employeeId
        return all.stream()
                .filter(s -> s.getAssignedEmployees().stream()
                        .anyMatch(sa -> sa.getEmployeeId().equals(employeeId)))
                .map(ShiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------
    // 8. getMyRoleForShift(employeeId, shiftId) → RoleDTO
    //    (originally: getMyRoleForShift(Employee, Shift))
    // ------------------------------------------------------------
    public RoleDTO getMyRoleForShift(
            String employeeId,
            String shiftId
    ) {
        Shift shift = shiftDAO.selectById(shiftId);
        if (shift == null) {
            throw new IllegalArgumentException("No shift with ID " + shiftId);
        }

        // Find the first ShiftAssignment whose employeeId matches
        Optional<Role> maybeRole = shift.getAssignedEmployees().stream()
                .filter(sa -> sa.getEmployeeId().equals(employeeId))
                .map(sa -> sa.getRole())
                .findFirst();

        if (maybeRole.isEmpty()) {
            return null;
        }
        // Convert domain Role → RoleDTO
        return RoleMapper.toDTO(maybeRole.get());
    }

    // ------------------------------------------------------------
    // 9. getCurrentShift() → Optional<ShiftDTO>
    //    (originally: getCurrentShift() printed it)
    // ------------------------------------------------------------
    public Optional<ShiftDTO> getCurrentShift() {
        Optional<Shift> current = shiftDAO.getCurrentShift();
        return current.map(ShiftMapper::toDTO);
    }

    // ------------------------------------------------------------
    // 10. addTemplate(ShiftTemplateDTO)
    //     (originally: addTemplate(ShiftTemplate))
    // ------------------------------------------------------------
    public void addTemplate(ShiftTemplateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("ShiftTemplateDTO must not be null");
        }
        ShiftTemplate domainTpl = ShiftTemplateMapper.fromDTO(dto);
        templateDAO.insert(domainTpl);
    }

    // ------------------------------------------------------------
    // 11. getTemplates() → List<ShiftTemplateDTO>
    // ------------------------------------------------------------
    public List<ShiftTemplateDTO> getTemplates() {
        return templateDAO.selectAll()
                .stream()
                .map(ShiftTemplateMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------
    // 12. getSchedule() → WeeklyShiftsScheduleDTO
    // ------------------------------------------------------------
    public WeeklyShiftsScheduleDTO getSchedule() {
        WeeklyShiftsSchedule schedule = shiftDAO.getSchedule();
        return WeeklyShiftsScheduleMapper.toDTO(schedule);
    }

    // ------------------------------------------------------------
    // 13. isWarehouseAssigned(String shiftId) → boolean
    // ------------------------------------------------------------
    public boolean isWarehouseAssigned(String shiftId) {
        if (shiftId == null || shiftId.isEmpty()) {
            return false;
        }
        return shiftDAO.isWarehouseEmployeeAssigned(shiftId);
    }

    public List<ShiftDTO> getAllShifts() {
        return shiftDAO.selectAll()
                .stream()
                .map(ShiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    public String getShiftIdByDateAndTime(Date date, String time) {
        return shiftDAO.getShiftIdByDateAndTime(date, time);
    }

}

