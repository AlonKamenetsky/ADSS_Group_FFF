package Transportation.Domain;

import Transportation.DTO.SiteDTO;

import java.util.List;
import java.util.NoSuchElementException;

public class SiteZoneManager {
    private final SiteManager siteManager;
    private final ZoneManager zoneManager;

    public SiteZoneManager(SiteManager sm, ZoneManager zm) {
        this.siteManager = sm;
        this.zoneManager = zm;
    }

    public void addSiteToZone(String siteAddress, String zoneName) throws NoSuchElementException {
        Site site = siteManager.getSiteByAddress(siteAddress.toLowerCase());
        Zone zone = zoneManager.getZoneByName(zoneName.toLowerCase());
        if (site == null || zone == null) {
            throw new NoSuchElementException();
        }
        if (siteManager.getSiteZone(siteAddress.toLowerCase()) != zone.getZoneId()) {
            siteManager.modifySiteZone(siteAddress.toLowerCase(), zone.getZoneId());
        }
        zone.addSiteToZone(site);
    }

    public void removeSiteFromZone(String siteAddress, String zoneName) {
        Site site = siteManager.getSiteByAddress(siteAddress.toLowerCase());
        Zone zone = zoneManager.getZoneByName(zoneName.toLowerCase());

        if (site == null || zone == null) return;

        Zone currZone = zoneManager.getZoneByName(zoneName.toLowerCase());
        if (siteManager.getSiteZone(siteAddress.toLowerCase()) == currZone.getZoneId()) {
            siteManager.modifySiteZone(siteAddress.toLowerCase(), -1);
            currZone.removeSiteFromZone(site);
        }
    }

    public List<SiteDTO> getSiteByZone(int zoneId) {
        return null;
    }
}