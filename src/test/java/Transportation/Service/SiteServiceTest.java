package Transportation.Tests.Service;

import Transportation.DTO.SiteDTO;
import Transportation.Domain.SiteManager;
import Transportation.Service.SiteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import java.sql.SQLException;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
class SiteServiceTest {

    @Mock
    SiteManager siteManager;

    @InjectMocks
    SiteService siteService;

    @BeforeEach
    void setUp() {
        siteService = new SiteService(siteManager);
    }

    @Test
    void addSite_Success() throws Exception {
        SiteDTO mockSite = new SiteDTO(1, "test address", "John", "123456", -1); // no zone assigned for site
        when(siteManager.addSite("test address", "John", "123456")).thenReturn(mockSite);

        SiteDTO result = siteService.addSite("test address", "John", "123456");
        assertEquals(mockSite, result);
    }

    @Test
    void addSite_NullInput_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> siteService.addSite(null, "John", "123456"));
        assertThrows(NullPointerException.class, () -> siteService.addSite("Address", null, "123456"));
        assertThrows(NullPointerException.class, () -> siteService.addSite("Address", "John", null));
    }

    @Test
    void addSite_InvalidContactName_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> siteService.addSite("Address", "John123", "123456"));
    }

    @Test
    void deleteSite_Success() throws Exception {
        SiteDTO mockSite = new SiteDTO(1, "address", "John", "123", 42);
        when(siteManager.findSiteByAddress("address")).thenReturn(Optional.of(mockSite));

        siteService.deleteSite("address");

        verify(siteManager).removeSite(1);
    }

    @Test

    @MockitoSettings(strictness = Strictness.LENIENT)
    void deleteSite_NoSiteFound_ThrowsException() throws Exception {
        when(siteManager.findSiteByAddress("missing")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> siteService.deleteSite("missing"));
    }

    @Test
    void getSiteByAddress_ReturnsCorrectSite() throws SQLException {
        SiteDTO mockSite = new SiteDTO(1, "address", "John", "123", 0);
        when(siteManager.findSiteByAddress("address")).thenReturn(Optional.of(mockSite));

        Optional<SiteDTO> result = siteService.getSiteByAddress("address");
        assertTrue(result.isPresent());
        assertEquals(mockSite, result.get());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void viewAllSites_ReturnsList() throws SQLException {
        when(siteManager.getAllSites()).thenReturn(Collections.emptyList());
        assertEquals(0, siteService.viewAllSites().size());
    }
}
