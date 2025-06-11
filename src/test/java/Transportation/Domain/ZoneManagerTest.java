package Transportation.Tests.Domain;
import Transportation.DTO.ZoneDTO;
import Transportation.Domain.Repositories.ZoneRepository;
import Transportation.Domain.ZoneManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.InstanceAlreadyExistsException;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ZoneManagerTest {

    @Mock
    ZoneRepository zoneRepository;

    @InjectMocks
    ZoneManager zoneManager;

    @Test
    public void testAddZone_Success() throws Exception {
        // arrange
        String zoneName = "NORTH";
        when(zoneRepository.findByZoneName(zoneName)).thenReturn(Optional.empty());
        when(zoneRepository.addZone(zoneName)).thenReturn(new ZoneDTO(1, zoneName,null));

        // act
        ZoneDTO result = zoneManager.addZone(zoneName);

        // assert
        assertNotNull(result);
        assertEquals("NORTH", result.zoneName());
        verify(zoneRepository).addZone(zoneName);
    }

    @Test
    public void testAddZone_AlreadyExists_Throws() throws SQLException {
        // arrange
        String zoneName = "SOUTH";
        when(zoneRepository.findByZoneName(zoneName))
                .thenReturn(Optional.of(new ZoneDTO(99, zoneName,null)));

        // act & assert
        assertThrows(InstanceAlreadyExistsException.class, () -> zoneManager.addZone(zoneName));

        verify(zoneRepository, never()).addZone(anyString());
    }
}
