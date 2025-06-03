package HR.Service;

import HR.DTO.RoleDTO;
import HR.DTO.ShiftDTO;
import HR.DTO.ShiftTemplateDTO;
import HR.DTO.WeeklyShiftsScheduleDTO;
import HR.DataAccess.*;
import HR.Domain.*;
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

    public ShiftDTO getShiftById(String shiftId) {
        if (shiftId == null || shiftId.isEmpty()) {
            return null;
        }
        Shift s = shiftDAO.selectById(shiftId);
        return (s == null ? null : ShiftMapper.toDTO(s));
    }

    public void assignEmployeeToShift(
            String shiftId,
            String employeeId,
            String roleName
    ) {
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

        shift.assignEmployee(emp, role);
        shiftDAO.update(shift);
    }

    public List<ShiftDTO> getShiftsForDate(LocalDate date) {
        return shiftDAO
                .getShiftsByDate(date)           // List<Shift>
                .stream()
                .map(ShiftMapper::toDTO)         // → ShiftDTO
                .collect(Collectors.toList());
    }

    public List<ShiftDTO> getCurrentWeekShifts() {
        return shiftDAO
                .getCurrentWeekShifts()           // List<Shift>
                .stream()
                .map(ShiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ShiftDTO> getNextWeekShifts() {
        return shiftDAO
                .getNextWeekShifts()             // List<Shift>
                .stream()
                .map(ShiftMapper::toDTO)
                .collect(Collectors.toList());
    }


    public void configureShiftRoles(
            String shiftId,
            Map<String, Integer> requiredRoleCounts
    ) {
        Shift shift = shiftDAO.selectById(shiftId);
        if (shift == null) {
            throw new IllegalArgumentException("No shift with ID " + shiftId);
        }

        Role managerRole = roleDAO.findByName("Shift Manager");
        Role hrRole      = roleDAO.findByName("HR");
        if (managerRole == null || hrRole == null) {
            throw new IllegalStateException("Required roles not found in DB");
        }

        shift.getRequiredCounts().clear();
        shift.getRequiredRoles().clear();

        shift.getRequiredCounts().put(managerRole, 1);
        shift.getRequiredRoles().put(managerRole, new ArrayList<>());

        shift.getRequiredCounts().put(hrRole, 0);
        shift.getRequiredRoles().put(managerRole, new ArrayList<>());

        for (Map.Entry<String, Integer> entry : requiredRoleCounts.entrySet()) {
            String roleName = entry.getKey();
            int    count    = entry.getValue();
            if (roleName.equalsIgnoreCase("Shift Manager") || roleName.equalsIgnoreCase("HR")) {
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

    public List<ShiftDTO> getAssignedShifts(String employeeId) {
        List<Shift> all = shiftDAO.getCurrentWeekShifts();
        return all.stream()
                .filter(s -> s.getAssignedEmployees().stream()
                        .anyMatch(sa -> sa.getEmployeeId().equals(employeeId)))
                .map(ShiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    public RoleDTO getMyRoleForShift(
            String employeeId,
            String shiftId
    ) {
        Shift shift = shiftDAO.selectById(shiftId);
        if (shift == null) {
            throw new IllegalArgumentException("No shift with ID " + shiftId);
        }

        Optional<Role> maybeRole = shift.getAssignedEmployees().stream()
                .filter(sa -> sa.getEmployeeId().equals(employeeId))
                .map(sa -> sa.getRole())
                .findFirst();

        return maybeRole.map(RoleMapper::toDTO).orElse(null);
        // Convert domain Role → RoleDTO
    }

    public Optional<ShiftDTO> getCurrentShift() {
        Optional<Shift> current = shiftDAO.getCurrentShift();
        return current.map(ShiftMapper::toDTO);
    }


    public void addTemplate(ShiftTemplateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("ShiftTemplateDTO must not be null");
        }
        ShiftTemplate domainTpl = ShiftTemplateMapper.fromDTO(dto);
        templateDAO.insert(domainTpl);
    }

    public List<ShiftTemplateDTO> getTemplates() {
        return templateDAO.selectAll()
                .stream()
                .map(ShiftTemplateMapper::toDTO)
                .collect(Collectors.toList());
    }

    public WeeklyShiftsScheduleDTO getSchedule() {
        WeeklyShiftsSchedule schedule = shiftDAO.getSchedule();
        return WeeklyShiftsScheduleMapper.toDTO(schedule);
    }

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

    public String getShiftIdByDateAndTime(java.util.Date date, String time) {
        return shiftDAO.getShiftIdByDateAndTime(date, time);
    }

}

