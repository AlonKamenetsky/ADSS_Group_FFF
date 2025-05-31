package Transportation.Tests.Domain;

import Transportation.DTO.SiteDTO;
import Transportation.Domain.*;
import Transportation.DTO.TransportationTaskDTO;
import Transportation.Domain.Repositories.TransportationDocRepository;
import Transportation.Domain.Repositories.TransportationTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskManagerTest {

    @Mock
    TransportationDocRepository docRepository;
    @Mock
    TransportationTaskRepository taskRepository;
    @Mock
    SiteManager siteManager;
    @Mock
    DriverManager driverManager;
    @Mock
    TruckManager truckManager;
    @Mock
    ItemManager itemManager;

    TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new TaskManager(docRepository, taskRepository, siteManager, driverManager, truckManager, itemManager);
    }

    @Test
    void testAddTask_Success() throws SQLException, ParseException {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.NOON;
        String address = "Test Address";
        SiteDTO mockSite = new SiteDTO(1, "Test Address", "John", "123", 0);
        TransportationTaskDTO mockTask = new TransportationTaskDTO(1, date, time, address,new ArrayList<>(), "", "", 0);
        when(siteManager.findSiteByAddress(address)).thenReturn(Optional.of(mockSite));
        when(taskRepository.createTask(date, time, address)).thenReturn(mockTask);

        TransportationTaskDTO result = taskManager.addTask(date, time, address);
        assertEquals(mockTask, result);
    }

    @Test
    void testAddTask_NoSite_ThrowsException() throws SQLException {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.NOON;
        String address = "Unknown Address";

        when(siteManager.findSiteByAddress(address)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                taskManager.addTask(date, time, address)
        );
    }

    @Test
    void testRemoveTask_TaskExists() throws SQLException {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.NOON;
        String address = "Test Address";
        SiteDTO mockSite = new SiteDTO(1, address, "John", "123", 0);
        List<String> destinations = Arrays.asList("Jerusalem", "Haifa");
        TransportationTaskDTO mockTask = new TransportationTaskDTO(1, date, time, address, destinations,"DRIVER123" , "TRUCK123", 100);

        when(siteManager.findSiteByAddress(address)).thenReturn(Optional.of(mockSite));
        when(taskRepository.findTaskByDateTimeAndSource(date, time, mockSite.siteId())).thenReturn(Optional.of(mockTask));
        when(truckManager.getTruckIdByLicense(mockTask.truckLicenseNumber())).thenReturn(10);

        taskManager.removeTask(date, time, address);

        verify(truckManager).setTruckAvailability(10, true);
        verify(taskRepository).deleteTask(mockTask.taskId());
    }

    @Test
    void testRemoveTask_TaskNotFound() throws SQLException {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.NOON;
        String address = "Test Address";
        SiteDTO mockSite = new SiteDTO(1, address, "John", "123", 0);

        when(siteManager.findSiteByAddress(address)).thenReturn(Optional.of(mockSite));
        when(taskRepository.findTaskByDateTimeAndSource(date, time, mockSite.siteId())).thenReturn(Optional.empty());

        taskManager.removeTask(date, time, address);

        verify(taskRepository, never()).deleteTask(anyInt());
    }
}
