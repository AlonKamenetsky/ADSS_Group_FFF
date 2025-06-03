package HR.Service;

import HR.DTO.*;
import HR.DataAccess.*;
import HR.Domain.*;
import HR.Mapper.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ShiftService {

    private final ShiftDAO shiftDAO;
    private final ShiftTemplateDAO templateDAO;
    private final EmployeeDAO employeeDAO;
    private final RoleDAO roleDAO;

    public ShiftService(ShiftDAO shiftDAO, ShiftTemplateDAO templateDAO, EmployeeDAO employeeDAO, RoleDAO roleDAO) {
        this.shiftDAO = shiftDAO;
        this.templateDAO = templateDAO;
        this.employeeDAO = employeeDAO;
        this.roleDAO = roleDAO;
    }

    public ShiftDTO getShiftById(String shiftId) {
        if (shiftId == null || shiftId.isEmpty()) return null;
        Shift s = shiftDAO.selectById(shiftId);
        return (s == null ? null : ShiftMapper.toDTO(s));
    }

    public void assignEmployeeToShift(String shiftId, String employeeId, String roleName) {
        Shift shift = shiftDAO.selectById(shiftId);
        Employee emp = employeeDAO.selectById(employeeId);
        Role role = roleDAO.findByName(roleName);

        if (shift == null) throw new IllegalArgumentException("No shift with ID " + shiftId);
        if (emp == null) throw new IllegalArgumentException("No employee with ID " + employeeId);
        if (role == null) throw new IllegalArgumentException("No role named \"" + roleName + "\"");

        shift.assignEmployee(emp, role);
        shiftDAO.update(shift);
    }

    public List<ShiftDTO> getShiftsForDate(LocalDate date) {
        return shiftDAO.getShiftsByDate(date).stream()
                .map(ShiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ShiftDTO> getCurrentWeekShifts() {
        return shiftDAO.getCurrentWeekShifts().stream()
                .map(ShiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ShiftDTO> getNextWeekShifts() {
        return shiftDAO.getNextWeekShifts().stream()
                .map(ShiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void configureShiftRoles(String shiftId, Map<String, Integer> requiredRoleCounts) {
        Shift shift = shiftDAO.selectById(shiftId);
        if (shift == null) throw new IllegalArgumentException("No shift with ID " + shiftId);

        Role managerRole = roleDAO.findByName("Shift Manager");
        Role hrRole = roleDAO.findByName("HR Manager");
        if (managerRole == null || hrRole == null) {
            throw new IllegalStateException("Required roles not found in DB");
        }

        shift.getRequiredCounts().clear();
        shift.getRequiredRoles().clear();
        shift.getRequiredCounts().put(managerRole, 1);
        shift.getRequiredRoles().put(managerRole, new ArrayList<>());

        for (Map.Entry<String, Integer> entry : requiredRoleCounts.entrySet()) {
            String roleName = entry.getKey();
            int count = entry.getValue();
            if (roleName.equalsIgnoreCase("Shift Manager") || roleName.equalsIgnoreCase("HR Manager")) {
                continue;
            }
            Role r = roleDAO.findByName(roleName);
            if (r == null) throw new IllegalArgumentException("Role \"" + roleName + "\" not found");
            shift.getRequiredCounts().put(r, count);
            shift.getRequiredRoles().put(r, new ArrayList<>());
        }

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

    public RoleDTO getMyRoleForShift(String employeeId, String shiftId) {
        Shift shift = shiftDAO.selectById(shiftId);
        if (shift == null) throw new IllegalArgumentException("No shift with ID " + shiftId);

        return shift.getAssignedEmployees().stream()
                .filter(sa -> sa.getEmployeeId().equals(employeeId))
                .map(ShiftAssignment::getRole)
                .map(RoleMapper::toDTO)
                .findFirst()
                .orElse(null);
    }

    public Optional<ShiftDTO> getCurrentShift() {
        return shiftDAO.getCurrentShift().map(ShiftMapper::toDTO);
    }

    public void addTemplate(ShiftTemplateDTO dto) {
        if (dto == null) throw new IllegalArgumentException("ShiftTemplateDTO must not be null");
        templateDAO.insert(ShiftTemplateMapper.fromDTO(dto));
    }

    public List<ShiftTemplateDTO> getTemplates() {
        return templateDAO.selectAll().stream()
                .map(ShiftTemplateMapper::toDTO)
                .collect(Collectors.toList());
    }

    public WeeklyShiftsScheduleDTO getSchedule() {
        return WeeklyShiftsScheduleMapper.toDTO(shiftDAO.getSchedule());
    }

    public boolean isWarehouseAssigned(String shiftId) {
        return shiftId != null && !shiftId.isEmpty() && shiftDAO.isWarehouseEmployeeAssigned(shiftId);
    }

    public List<ShiftDTO> getAllShifts() {
        return shiftDAO.selectAll().stream()
                .map(ShiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    public String getShiftIdByDateAndTime(Date date, String time) {
        return shiftDAO.getShiftIdByDateAndTime(date, time);
    }
}
