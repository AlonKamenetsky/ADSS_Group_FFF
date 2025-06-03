//package IntegrationTests;

//import HR.DTO.EmployeeDTO;
//import HR.Domain.Shift;
//import Transportation.Domain.*;
//import Transportation.DTO.TransportationTaskDTO;
//import Transportation.Domain.LicenseMapper;
//import Transportation.Domain.TruckType;
//import Transportation.Service.*;
//import Transportation.DTO.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.sql.Date;
//import java.text.ParseException;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class TransportationToEmployeeHRIntegrationTest {
//
//    private TaskManager taskManager;
//    private EmployeeProvider mockEmployeeProvider;
//    private TaskService taskService;
//
//    @BeforeEach
//    void setUp() {
//        mockEmployeeProvider = mock(EmployeeProvider.class);
//        taskManager = new TaskManager(mockEmployeeProvider);
//    }
//
//    @Test
//    void testAssignDriverAndTruckToTask_successfulFlow() throws Exception {
//        // GIVEN
//        String taskDate = "15/06/2025";
//        String taskDeparture = "10:30";
//        String taskSourceSite = "bareket 20 shoham";
//
//
//        taskManager.addTask(
//                LocalDate.of(2025, 6, 15),
//                LocalTime.of(10, 30),
//                taskSourceSite
//        );
//
//        String requiredLicense = LicenseMapper.getRequiredLicense(TruckType.Small).toString();
//        Date shiftDate = Date.valueOf(LocalDate.of(2025, 6, 15));
//        String shiftTimeStr = "10:30";
//
//        List<EmployeeDTO> mockDrivers = List.of(
//                new EmployeeDTO("D123", "Driver Test", requiredLicense)
//        );
//
//        when(mockEmployeeProvider.findAvailableDrivers(requiredLicense, shiftDate, shiftTimeStr))
//                .thenReturn(mockDrivers);
//
//        when(mockEmployeeProvider.findAvailableWarehouseWorkers(eq(shiftDate), eq(Shift.ShiftTime.MORNING)))
//                .thenReturn(true);
//
//        when(mockEmployeeProvider.getShiftIdByDateTime(eq(shiftDate), eq(shiftTimeStr)))
//                .thenReturn("SHIFT123");
//
//        // THEN
//        boolean assigned = taskManager.assignDriverAndTruckToTask(
//                LocalDate.of(2025, 6, 15),
//                LocalTime.of(10, 30),
//                taskSourceSite
//        );
//
//        assertTrue(assigned, "Driver and truck should be assigned successfully");
//        verify(mockEmployeeProvider).findAvailableDrivers(requiredLicense, shiftDate, shiftTimeStr);
//        verify(mockEmployeeProvider).findAvailableWarehouseWorkers(shiftDate, Shift.ShiftTime.MORNING);
//        verify(mockEmployeeProvider).getShiftIdByDateTime(shiftDate, shiftTimeStr);
//    }
//
//
//    }
//
//
//
//
