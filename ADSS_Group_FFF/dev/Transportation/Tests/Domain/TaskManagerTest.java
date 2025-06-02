package Transportation.Tests.Domain;

import Transportation.DTO.SiteDTO;
import Transportation.DTO.TransportationDocDTO;
import Transportation.Domain.*;
import Transportation.DTO.TransportationTaskDTO;
import Transportation.Domain.Repositories.TransportationDocRepository;
import Transportation.Domain.Repositories.TransportationTaskRepository;
import Transportation.Service.HREmployeeAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskManagerTest {
    private TaskManager taskManager;
    private TransportationDocRepository docRepo;
    private TransportationTaskRepository taskRepo;
    private SiteManager siteManager;
    private ItemManager itemManager;

    @BeforeEach
    void setUp() {
        docRepo = mock(TransportationDocRepository.class);
        taskRepo = mock(TransportationTaskRepository.class);
        siteManager = mock(SiteManager.class);
        TruckManager truckManager = mock(TruckManager.class);
        itemManager = mock(ItemManager.class);
        EmployeeProvider adapter = mock(HREmployeeAdapter.class);

        taskManager = new TaskManager(docRepo, taskRepo, siteManager, truckManager, itemManager, adapter);
    }

    @Test
    void testAddDocToTask_success() throws Exception {
        LocalDate date = LocalDate.of(2024, 1, 1);
        LocalTime time = LocalTime.of(8, 0);
        String source = "source";
        String dest = "dest";

        SiteDTO sourceSite = new SiteDTO(1, source, "a", "1", 1);
        SiteDTO destSite = new SiteDTO(2, dest, "b", "2", 1);
        TransportationTaskDTO taskDTO = new TransportationTaskDTO(1, date, time,source,new ArrayList<>() ,"", "", 0);

        when(siteManager.findSiteByAddress(source)).thenReturn(Optional.of(sourceSite));
        when(siteManager.findSiteByAddress(dest)).thenReturn(Optional.of(destSite));
        when(taskRepo.findTaskByDateTimeAndSource(date, time, 1)).thenReturn(Optional.of(taskDTO));
        when(itemManager.makeList(any())).thenReturn(100);

        HashMap<String, Integer> items = new HashMap<>();
        items.put("apple", 5);

        taskManager.addDocToTask(date, time, source, dest, items);

        verify(docRepo).createDoc(eq(1), eq(2), eq(100));
        verify(taskRepo).addDestination(1, 2);
    }

    @Test
    void testUpdateWeight_success() throws Exception {
        LocalDate date = LocalDate.of(2024, 1, 1);
        LocalTime time = LocalTime.of(8, 0);
        String source = "source";
        int taskId = 1;

        SiteDTO sourceSite = new SiteDTO(10, source, "c", "3", 1);
        TransportationTaskDTO taskDTO = new TransportationTaskDTO(taskId, date, time,source,new ArrayList<>() ,"", "", 0);
        TransportationDocDTO doc = new TransportationDocDTO(101, 10, 20, 1);

        when(siteManager.findSiteByAddress(source)).thenReturn(Optional.of(sourceSite));
        when(taskRepo.findTaskByDateTimeAndSource(date, time, 10)).thenReturn(Optional.of(taskDTO));
        when(docRepo.findDocByTaskId(taskId)).thenReturn(List.of(doc));
        when(docRepo.findDocItemsListId(doc.docId())).thenReturn(20);
        when(itemManager.findWeightList(20)).thenReturn(100.0f);
        when(taskRepo.updateWeight(taskId, 100.0f)).thenReturn( new TransportationTaskDTO(1, date, time,source,new ArrayList<>() ,"", "", 100f));

        TransportationTaskDTO result = taskManager.updateWeightForTask(date, time, source);

        assertEquals(100.0f, result.weightBeforeLeaving());
    }
}