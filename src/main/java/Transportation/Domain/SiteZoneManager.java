package Transportation.Domain;

import Transportation.DTO.SiteDTO;
import Transportation.DTO.ZoneDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class SiteZoneManager {
    private final SiteManager siteManager;
    private final ZoneManager zoneManager;

    public SiteZoneManager() {
        this.siteManager = new SiteManager();
        this.zoneManager = new ZoneManager();
    }

    // for mock tests
    public SiteZoneManager(SiteManager siteManager1, ZoneManager zoneManager1) {
        this.siteManager = siteManager1;
        this.zoneManager = zoneManager1;
    }

    public SiteDTO mapSiteToZone(SiteDTO site, int zoneId) throws SQLException {
        return siteManager.mapSiteToZone(site, zoneId);
    }

    public SiteDTO removeSiteFromZone(SiteDTO site) throws SQLException {
        return siteManager.mapSiteToZone(site, -1);
    }

    public List<SiteDTO> getSitesByZone(int zoneId) throws SQLException {
        return siteManager.findAllByZoneId(zoneId);
    }

    public ZoneDTO getZoneWithSites(String zoneName) throws SQLException, NoSuchElementException {
        Optional<ZoneDTO> zone = zoneManager.findZoneByName(zoneName);
        if (zone.isEmpty()) {
            throw new NoSuchElementException();
        }
        List<SiteDTO> relatedSites = siteManager.findAllByZoneId(zone.get().zoneId());
        ArrayList<String> siteAddresses = new ArrayList<>();
        for (SiteDTO site : relatedSites) {
            siteAddresses.add(site.siteAddress()); // Or site.contactName(), etc.
        }

        return new ZoneDTO(zone.get().zoneId(), zone.get().zoneName(), siteAddresses);
    }

    public String getZoneNameBySite(SiteDTO site) throws SQLException, NoSuchElementException {
        Optional<ZoneDTO> zone = zoneManager.getZoneById(site.zoneId());
        if (zone.isPresent()) {
            return zone.get().zoneName();
        }
        else {
            throw new NoSuchElementException();
        }
    }
}