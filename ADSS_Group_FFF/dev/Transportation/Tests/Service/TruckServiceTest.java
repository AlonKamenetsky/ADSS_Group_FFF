package Transportation.Tests.Service;

import Transportation.Service.TruckService;

import Transportation.DTO.TruckDTO;
import Transportation.Domain.TruckManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TruckServiceTest {

    @Mock
    TruckManager truckManager;
    TruckService truckService;

    @BeforeEach
    void setUp() {
        truckService = new TruckService(truckManager);
    }

    @Test
    void addTruck_Success() throws Exception {
        doNothing().when(truckManager).addTruck("type", "ABC123", "model", 1000, 5000);
        truckService.AddTruck("type", "ABC123", "model", 1000, 5000);
        verify(truckManager).addTruck("type", "ABC123", "model", 1000, 5000);
    }

    @Test
    void addTruck_NullInputs_ThrowsException() {
        assertThrows(NullPointerException.class, () -> truckService.AddTruck(null, "ABC123", "model", 1000, 5000));
        assertThrows(NullPointerException.class, () -> truckService.AddTruck("type", null, "model", 1000, 5000));
        assertThrows(NullPointerException.class, () -> truckService.AddTruck("type", "ABC123", null, 1000, 5000));
        assertThrows(NullPointerException.class, () -> truckService.AddTruck("type", "ABC123", "model", -1, 5000));
        assertThrows(NullPointerException.class, () -> truckService.AddTruck("type", "ABC123", "model", 1000, -5));
    }

    @Test
    void deleteTruck_Success() throws SQLException {
        when(truckManager.getTruckIdByLicense("ABC123")).thenReturn(1);
        doNothing().when(truckManager).removeTruck(1);

        truckService.deleteTruck("ABC123");
        verify(truckManager).removeTruck(1);
    }

    @Test
    void getTruckByLicenseNumber_ReturnsFormattedString() throws SQLException {
        TruckDTO mockTruck = new TruckDTO(1, "ABC123", "model", "type", 1000, 5000, true);
        when(truckManager.getTruckIdByLicense("ABC123")).thenReturn(1);
        when(truckManager.getTruckById(1)).thenReturn(Optional.of(mockTruck));

        String result = truckService.getTruckByLicenseNumber("ABC123");
        assertTrue(result.contains("License: ABC123"));
        assertTrue(result.contains("Available: Yes"));
    }

    @Test
    void changeTruckAvailability_Success() throws SQLException {
        when(truckManager.getTruckIdByLicense("ABC123")).thenReturn(1);
        doNothing().when(truckManager).setTruckAvailability(1, false);

        truckService.ChangeTruckAvailability("ABC123", false);
        verify(truckManager).setTruckAvailability(1, false);
    }

    @Test
    void viewAllTrucks_EmptyStringWhenNoTrucks() throws SQLException {
        when(truckManager.getAllTrucksString()).thenReturn("");
        assertEquals("", truckService.viewAllTrucks());
    }
}
