package Transportation.Tests.Service;

import Transportation.Service.ZoneService;

import Transportation.DTO.ZoneDTO;
import Transportation.Domain.ZoneManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZoneServiceTest {

    @Mock
    ZoneManager zoneManager;

    ZoneService zoneService;

    @BeforeEach
    void setUp() {
        zoneService = new ZoneService();
    }

    @Test
    void addZone_Success() throws Exception {
        when(zoneManager.addZone("north")).thenReturn(new ZoneDTO(1, "North", null));
        zoneService.AddZone("north");
        verify(zoneManager).addZone("north");
    }

    @Test
    void addZone_Null_ThrowsException() {
        assertThrows(NullPointerException.class, () -> zoneService.AddZone(null));
    }

    @Test
    void deleteZone_Success() throws Exception {
        ZoneDTO mockZone = new ZoneDTO(1, "north", new ArrayList<>());
        when(zoneManager.findZoneByName("north")).thenReturn(Optional.of(mockZone));

        zoneService.deleteZone("north");
        verify(zoneManager).removeZone(1);
    }

    @Test
    void deleteZone_NotFound_ThrowsException() throws Exception {
        when(zoneManager.findZoneByName("south")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> zoneService.deleteZone("south"));
    }

    @Test
    void updateZone_Success() throws Exception {
        ZoneDTO mockZone = new ZoneDTO(1, "old", new ArrayList<>(List.of("site1")));
        when(zoneManager.findZoneByName("old")).thenReturn(Optional.of(mockZone));
        ZoneDTO updated = new ZoneDTO(1, "new",new ArrayList<>(List.of("site1")));
        when(zoneManager.modifyZone(updated)).thenReturn(updated);

        ZoneDTO result = zoneService.UpdateZone("old", "new");
        assertEquals("new", result.zoneName());
    }

    @Test
    void updateZone_NullInput_ThrowsException() {
        assertThrows(NullPointerException.class, () -> zoneService.UpdateZone(null, "new"));
        assertThrows(NullPointerException.class, () -> zoneService.UpdateZone("old", null));
    }

    @Test
    void viewAllZones_ReturnsList() throws SQLException {
        when(zoneManager.getAllZones()).thenReturn(Collections.emptyList());
        assertTrue(zoneService.viewAllZones().isEmpty());
    }
}