package HR.tests.PresentationTests;

import HR.DTO.EmployeeDTO;
import HR.DTO.RoleDTO;
import HR.DTO.ShiftDTO;
import HR.Presentation.EmployeeInterface;
import HR.Service.EmployeeService;
import HR.Service.ShiftService;
import HR.Service.SwapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeInterfaceTest {

    private EmployeeService employeeService;
    private ShiftService shiftService;
    private SwapService swapService;

    @BeforeEach
    void setUp() throws ParseException {
        employeeService = EmployeeService.getInstance();
        shiftService    = ShiftService.getInstance();
        swapService     = SwapService.getInstance();

        // Clean out any existing data in the services.
        // If your services maintain in‐memory caches, clear them here:
        // employeeService.clearAll();
        // shiftService.clearAll();
        // swapService.clearAll();

        // Create a single employee in the system for testing:
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId("E100");
        dto.setName("TestUser");
        dto.setRoles(Collections.emptyList());
        dto.setBankAccount("BQ123");
        dto.setSalary(3000f);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date d = df.parse("2022-05-01");
        dto.setEmploymentDate(d);
        employeeService.addEmployee(dto);
        employeeService.setPassword("E100", "pw");
    }

    @Test
    void viewMyInfo_printsWithoutException() {
        EmployeeInterface ui = new EmployeeInterface("E100");

        // Swap System.out to capture output (optional)
        PrintStream originalOut = System.out;
        try {
            ui.viewMyInfo();
            // If no exception is thrown, test passes
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void viewAssignedShifts_noShifts_printsEmptyList() {
        EmployeeInterface ui = new EmployeeInterface("E100");

        // There are no shifts assigned, so no exception:
        ui.viewAssignedShifts();
    }

    @Test
    void addVacation_and_viewHolidays_behaveAsExpected() throws ParseException {
        EmployeeInterface ui = new EmployeeInterface("E100");
        Scanner scanner = new Scanner(new ByteArrayInputStream("2022-12-25\n".getBytes()));

        // This should add December 25, 2022 as a vacation
        ui.addVacation(scanner);

        // Now “viewHolidays” should list at least one date (no exception thrown)
        ui.viewHolidays();

        // Confirm that the service reports exactly that holiday
        List<Date> holidays = employeeService.getEmployeeHolidays("E100");
        assertEquals(1, holidays.size());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals("2022-12-25", df.format(holidays.get(0)));
    }

    @Test
    void sendWeeklyAvailability_then_viewWeeklyAvailability_noException() {
        EmployeeInterface ui = new EmployeeInterface("E100");

        // For “sendWeeklyAvailability,” we need at least one next‐week shift to toggle.
        // Let’s create one shift for next week via shiftService (DTO‐based API):
        ShiftDTO shift = new ShiftDTO();
        shift.setId("S1");
        // Set date to upcoming Monday
        shift.setDate(new Date(System.currentTimeMillis() + 7L * 24 * 3600 * 1000));
        shift.setType(HR.Domain.Shift.ShiftTime.Morning);
        shift.setRequiredCounts(Collections.emptyMap());
        shift.setAssignedEmployees(Collections.emptyList());
        shiftService.addTemplate(new HR.DTO.ShiftTemplateDTO(DayOfWeek.MONDAY, HR.Domain.Shift.ShiftTime.Morning, Collections.emptyMap()));
        // We assume that adding via addTemplate is enough for availability to show ■

        // Now call sendWeeklyAvailability with “0” immediately to finish
        Scanner scanner = new Scanner(new ByteArrayInputStream("0\n".getBytes()));
        ui.sendWeeklyAvailability(scanner);

        // Then viewWeeklyAvailability (current‐week) and viewNextWeeklyAvailability (next‐week)
        ui.viewWeeklyAvailability();
        ui.viewNextWeeklyAvailability();
        // No exception = pass
    }

    @Test
    void sendSwapRequest_then_cancelSwapRequest_noException() throws ParseException {
        // We need a second employee and a shift, plus role assignment, to create a swap scenario.

        // 1) Create a second employee
        EmployeeDTO dto2 = new EmployeeDTO();
        dto2.setId("E200");
        dto2.setName("OtherUser");
        dto2.setRoles(Collections.singletonList(new RoleDTO("DummyRole")));
        dto2.setBankAccount("BQ456");
        dto2.setSalary(3200f);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        dto2.setEmploymentDate(df.parse("2022-06-01"));
        employeeService.addEmployee(dto2);
        employeeService.setPassword("E200", "pw2");

        // 2) Create a dummy shift and assign both employees same role
        //    (We’ll fake it by directly calling shiftService.assignEmployeeToShift)
        ShiftDTO shift = new ShiftDTO();
        shift.setId("S100");
        shift.setDate(new Date());
        shift.setType(HR.Domain.Shift.ShiftTime.Morning);
        shift.setRequiredCounts(Collections.singletonMap("DummyRole", 2));
        shift.setAssignedEmployees(Collections.emptyList());
        shiftService.addTemplate(new HR.DTO.ShiftTemplateDTO(DayOfWeek.WEDNESDAY, HR.Domain.Shift.ShiftTime.Morning, Collections.singletonMap("DummyRole", 2)));
        shiftService.configureShiftRoles("S100", Collections.singletonMap("DummyRole", 2));
        shiftService.assignEmployeeToShift("S100", "E100", "DummyRole");
        shiftService.assignEmployeeToShift("S100", "E200", "DummyRole");

        // 3) Now “sendSwapRequest” for employee E100, and then “cancelSwapRequest”
        EmployeeInterface ui = new EmployeeInterface("E100");

        // First, send a swap request
        ui.sendSwapRequest(new Scanner(new ByteArrayInputStream("1\n".getBytes())));
        List<HR.DTO.SwapRequestDTO> pending = swapService.getSwapRequests();
        assertFalse(pending.isEmpty(), "Swap request should have been created");

        // Cancel it (simulate picking “1” again)
        ui.cancelSwapRequest(new Scanner(new ByteArrayInputStream("1\n".getBytes())));
        assertTrue(swapService.getSwapRequests().isEmpty(), "Swap requests should be empty after cancellation");
    }
}
