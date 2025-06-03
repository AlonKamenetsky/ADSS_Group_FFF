package Transportation.Tests.Service;

import Transportation.DTO.SiteDTO;
import Transportation.DTO.ZoneDTO;
import Transportation.Domain.SiteManager;
import Transportation.Domain.SiteZoneManager;
import Transportation.Domain.ZoneManager;
import Transportation.Service.SiteZoneService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SiteZoneServiceTest {

    @Mock
    SiteZoneManager siteZoneManager;

    @Mock
    SiteManager siteManager;

    @Mock
    ZoneManager zoneManager;

    @InjectMocks
    SiteZoneService service;

    SiteDTO mockSite;
    ZoneDTO mockZone;

    @BeforeEach
    void setUp() {
        mockSite = new SiteDTO(1, "address", "John", "0501234567", 2);
        ArrayList<String> sitesRelated = new ArrayList<>();
        sitesRelated.add("address");
        mockZone = new ZoneDTO(2, "north", sitesRelated);
    }

    @Test
    void addSiteToZone_success() throws Exception {
        when(siteManager.findSiteByAddress("address")).thenReturn(Optional.of(mockSite));
        when(zoneManager.findZoneByName("north")).thenReturn(Optional.of(mockZone));
        when(siteZoneManager.mapSiteToZone(mockSite, 2)).thenReturn(mockSite);

        SiteDTO result = service.addSiteToZone("address", "north");

        assertEquals(mockSite, result);
        verify(siteZoneManager).mapSiteToZone(mockSite, 2);
    }

    @Test
    void addSiteToZone_missingSite_throwsException() throws SQLException {
        when(siteManager.findSiteByAddress("address")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.addSiteToZone("address", "north"));
    }

    @Test
    void removeSiteFromZone_success() throws Exception {
        when(siteManager.findSiteByAddress("address")).thenReturn(Optional.of(mockSite));
        when(siteZoneManager.removeSiteFromZone(mockSite)).thenReturn(mockSite);

        SiteDTO result = service.removeSiteFromZone("address");

        assertEquals(mockSite, result);
    }

    @Test
    void getSitesByZone_success() throws Exception {
        List<SiteDTO> mockSites = List.of(mockSite);
        when(zoneManager.findZoneByName("north")).thenReturn(Optional.of(mockZone));
        when(siteZoneManager.getSitesByZone(2)).thenReturn(mockSites);

        List<SiteDTO> result = service.getSitesByZone("north");

        assertEquals(mockSites, result);
    }

    @Test
    void getZoneWithSites_success() throws Exception {
        when(siteZoneManager.getZoneWithSites("north")).thenReturn(mockZone);

        ZoneDTO result = service.getZoneWithSites("north");

        assertEquals(mockZone, result);
    }

    @Test
    void getZoneNameBySite_success() throws Exception {
        when(siteZoneManager.getZoneNameBySite(mockSite)).thenReturn("north");

        String result = service.getZoneNameBySite(mockSite);

        assertEquals("north", result);
    }

    @Test
    void getZoneNameBySite_sqlError_throwsRuntimeException() throws Exception {
        when(siteZoneManager.getZoneNameBySite(mockSite)).thenThrow(new SQLException("fail"));

        assertThrows(RuntimeException.class, () -> service.getZoneNameBySite(mockSite));
    }

    @Test
    void addSiteToZone_nullInputs_throws() {
        assertThrows(NullPointerException.class, () -> service.addSiteToZone(null, "north"));
        assertThrows(NullPointerException.class, () -> service.addSiteToZone("address", null));
    }

    @Test
    void removeSiteFromZone_nullInput_throws() {
        assertThrows(NullPointerException.class, () -> service.removeSiteFromZone(null));
    }

    @Test
    void getSitesByZone_nullInput_throws() {
        assertThrows(NullPointerException.class, () -> service.getSitesByZone(null));
    }

    @Test
    void getZoneWithSites_nullInput_throws() {
        assertThrows(NullPointerException.class, () -> service.getZoneWithSites(null));
    }
}