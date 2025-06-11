package Transportation.Tests.Domain;

import Transportation.DTO.SiteDTO;
import Transportation.Domain.Repositories.SiteRepository;
import Transportation.Domain.SiteManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.InstanceAlreadyExistsException;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SiteManagerTest {

    @Mock
    SiteRepository siteRepository;

    @InjectMocks
    SiteManager siteManager;

    SiteDTO sampleSite;

    @BeforeEach
    void setUp() {
        sampleSite = new SiteDTO(1, "Main St", "Alice", "123456789", 1);
    }

    @Test
    void addSite_siteDoesNotExist_addsSuccessfully() throws Exception {
        when(siteRepository.findBySiteAddress("Main St")).thenReturn(Optional.empty());
        when(siteRepository.addSite("Main St", "Alice", "123456789")).thenReturn(sampleSite);

        SiteDTO result = siteManager.addSite("Main St", "Alice", "123456789");

        assertEquals(sampleSite, result);
        verify(siteRepository).addSite("Main St", "Alice", "123456789");
    }

    @Test
    void addSite_siteAlreadyExists_throwsException() throws SQLException {
        when(siteRepository.findBySiteAddress("Main St")).thenReturn(Optional.of(sampleSite));

        assertThrows(InstanceAlreadyExistsException.class,
                () -> siteManager.addSite("Main St", "Alice", "123456789"));
    }

    @Test
    void removeSite_siteExists_removesSuccessfully() throws Exception {
        when(siteRepository.findSite(1)).thenReturn(Optional.of(sampleSite));

        siteManager.removeSite(1);

        verify(siteRepository).deleteSite(1);
    }

    @Test
    void removeSite_siteDoesNotExist_throwsException() throws SQLException {
        when(siteRepository.findSite(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> siteManager.removeSite(1));
    }
}