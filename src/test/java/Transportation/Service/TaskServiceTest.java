package Transportation.Tests.Service;

import HR.Service.EmployeeService;
import Transportation.Service.TaskService;

import Transportation.DTO.TransportationTaskDTO;
import Transportation.Domain.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    TaskManager taskManager;

    @Mock
    EmployeeService employeeService;

    TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskManager, employeeService);
    }

    @Test
    void addTask_ValidInput_ReturnsTask() throws Exception {
        String dateStr = "01/06/2025";
        String timeStr = "14:00";
        String address = "test address";
        LocalDate date = LocalDate.of(2025, 6, 1);
        LocalTime time = LocalTime.of(14, 0);
        TransportationTaskDTO dto = new TransportationTaskDTO(1, date, time, address, null, "","",0);

        when(taskManager.addTask(date, time, address)).thenReturn(dto);

        TransportationTaskDTO result = taskService.addTask(dateStr, timeStr, address);
        assertEquals(dto, result);
    }

    @Test
    void addTask_NullInput_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> taskService.addTask(null, "12:00", "site"));
        assertThrows(NullPointerException.class, () -> taskService.addTask("01/01/2024", null, "site"));
        assertThrows(NullPointerException.class, () -> taskService.addTask("01/01/2024", "12:00", null));
    }

    @Test
    void addTask_SiteNotFound_ThrowsNoSuchElementException() throws Exception {
        String dateStr = "01/06/2025";
        String timeStr = "14:00";
        String address = "unknown";

        when(taskManager.addTask(any(), any(), eq(address))).thenThrow(new NoSuchElementException());

        assertThrows(NoSuchElementException.class, () ->
                taskService.addTask(dateStr, timeStr, address)
        );
    }

    @Test
    void getAllTasksString_ReturnsString() throws Exception {
        when(taskManager.getAllTasksString()).thenReturn("task1\ntask2");
        String result = taskService.getAllTasksString();
        assertTrue(result.contains("task1"));
    }

    @Test
    void assignDriverAndTruckToTask_ReturnsTrue() throws Exception {
        when(taskManager.assignDriverAndTruckToTask(any(), any(), any())).thenReturn(true);
        boolean result = taskService.assignDriverAndTruckToTask("01/06/2025", "14:00", "site");
        assertTrue(result);
    }

    @Test
    void assignDriverAndTruckToTask_ThrowsNullPointer() {
        assertThrows(NullPointerException.class, () -> taskService.assignDriverAndTruckToTask(null, "14:00", "site"));
    }
}

