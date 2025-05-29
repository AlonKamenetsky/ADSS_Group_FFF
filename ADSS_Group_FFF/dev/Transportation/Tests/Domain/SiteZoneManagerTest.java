package Transportation.Tests.Domain;

import Transportation.DTO.SiteDTO;
import Transportation.DTO.ZoneDTO;
import Transportation.Domain.SiteManager;
import Transportation.Domain.SiteZoneManager;
import Transportation.Domain.ZoneManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SiteZoneManagerTest {

    @Mock
    private SiteManager siteManager;

    @Mock
    private ZoneManager zoneManager;

    @InjectMocks
    private SiteZoneManager siteZoneManager;

    private SiteDTO site;
    private ZoneDTO zone;

    @BeforeEach
    public void setUp() {
        site = new SiteDTO(1, "Address A", "Contact A", "050-0000000", 3);
        zone = new ZoneDTO(3, "North", new ArrayList<>(List.of("Address A")));
    }

    @Test
    public void testMapSiteToZone_success() throws SQLException {
        when(siteManager.mapSiteToZone(site, 3)).thenReturn(site);

        SiteDTO result = siteZoneManager.mapSiteToZone(site, 3);

        assertEquals(site, result);
        verify(siteManager).mapSiteToZone(site, 3);
    }

    @Test
    public void testRemoveSiteFromZone_setsZoneIdMinus1() throws SQLException {
        SiteDTO updated = new SiteDTO(1, "Address A", "Contact A", "050-0000000", -1);
        when(siteManager.mapSiteToZone(site, -1)).thenReturn(updated);

        SiteDTO result = siteZoneManager.removeSiteFromZone(site);

        assertEquals(-1, result.zoneId());
        verify(siteManager).mapSiteToZone(site, -1);
    }

    @Test
    public void testGetZoneWithSites_success() throws SQLException {
        when(zoneManager.findZoneByName("North")).thenReturn(Optional.of(zone));
        when(siteManager.findAllByZoneId(3)).thenReturn(List.of(site));

        ZoneDTO zoneResult = siteZoneManager.getZoneWithSites("North");

        assertEquals("North", zoneResult.zoneName());
        assertTrue(zoneResult.sitesRelated().contains("Address A"));
    }

    @Test
    public void testGetZoneWithSites_notFound() throws SQLException {
        when(zoneManager.findZoneByName("South")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                siteZoneManager.getZoneWithSites("South"));
    }

    @Test
    public void testGetZoneNameBySite_success() throws SQLException {
        when(zoneManager.getZoneById(3)).thenReturn(Optional.of(zone));

        String result = siteZoneManager.getZoneNameBySite(site);

        assertEquals("North", result);
    }

    @Test
    public void testGetZoneNameBySite_notFound() throws SQLException {
        when(zoneManager.getZoneById(3)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                siteZoneManager.getZoneNameBySite(site));
    }
}
