package Transportation.Tests.Domain;

import Transportation.Domain.TruckManager;
import Transportation.DTO.TruckDTO;
import org.junit.jupiter.api.TestInstance;
import Transportation.Domain.Repositories.TruckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class TruckManagerTest {

    @Mock
    TruckRepository repository;

    @InjectMocks
    TruckManager manager;

    TruckDTO truckDTO;

    @BeforeEach
    void setup() {
        Mockito.reset(repository);
        truckDTO = new TruckDTO(1, "Medium", "123-ABC", "Volvo", 3000, 8000, true);
    }

    @Test
    void addTruck_success() throws Exception {
        when(repository.findTruckByLicense("123-ABC")).thenReturn(Optional.empty());

        manager.addTruck("Medium", "123-ABC", "Volvo", 3000, 8000);

        verify(repository).addTruck("Medium", "123-ABC", "Volvo", 3000, 8000, true);
    }

    @Test
    void addTruck_alreadyExists_throwsException() throws Exception {
        when(repository.findTruckByLicense("123-ABC")).thenReturn(Optional.of(truckDTO));

        assertThrows(InstanceAlreadyExistsException.class, () -> manager.addTruck("Medium", "123-ABC", "Volvo", 3000, 8000));
    }

    @Test
    void removeTruck_success() throws Exception {
        when(repository.findTruckById(1)).thenReturn(Optional.of(truckDTO));

        manager.removeTruck(1);

        verify(repository).deleteTruck(1);
    }

    @Test
    void removeTruck_notFound_throwsException() throws Exception {
        when(repository.findTruckById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> manager.removeTruck(1));
    }

    @Test
    void getAllTrucks_returnsList() throws Exception {
        List<TruckDTO> trucks = List.of(truckDTO);
        when(repository.getAllTrucks()).thenReturn(trucks);

        List<TruckDTO> result = manager.getAllTrucks();

        assertEquals(1, result.size());
        assertEquals("123-ABC", result.get(0).licenseNumber());
    }

    @Test
    void setTruckAvailability_success() throws Exception {
        when(repository.findTruckById(1)).thenReturn(Optional.of(truckDTO));

        manager.setTruckAvailability(1, false);

        verify(repository).updateAvailability(1, false);
    }

    @Test
    void setTruckAvailability_notFound_throws() throws Exception {
        when(repository.findTruckById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> manager.setTruckAvailability(1, false));
    }

    @Test
    void getNextTruckAvailable_returnsMatch() throws Exception {
        TruckDTO heavier = new TruckDTO(2, "Large", "456-DEF", "Scania", 4000, 12000, true);
        when(repository.getAllTrucks()).thenReturn(List.of(truckDTO, heavier));

        Optional<TruckDTO> result = manager.getNextTruckAvailable(6000);

        assertTrue(result.isPresent());
        assertEquals("123-ABC", result.get().licenseNumber());
    }

    @Test
    void getTruckIdByLicense_found_returnsId() throws Exception {
        when(repository.findTruckByLicense("123-ABC")).thenReturn(Optional.of(truckDTO));

        int id = manager.getTruckIdByLicense("123-ABC");
        assertEquals(1, id);
    }

    @Test
    void getTruckIdByLicense_notFound_throws() throws Exception {
        when(repository.findTruckByLicense("123-ABB")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> manager.getTruckIdByLicense("123-ABB"));
    }
}