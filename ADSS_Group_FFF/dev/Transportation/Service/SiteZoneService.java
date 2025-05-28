package Transportation.Service;

import Transportation.DTO.SiteDTO;
import Transportation.DTO.ZoneDTO;
import Transportation.Domain.SiteManager;
import Transportation.Domain.SiteZoneManager;
import Transportation.Domain.ZoneManager;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class SiteZoneService {
    private final SiteZoneManager siteZoneManager;
    private final SiteManager siteManager;
    private final ZoneManager zoneManager;

    public SiteZoneService() {
        this.siteZoneManager = new SiteZoneManager();
        this.siteManager = new SiteManager();
        this.zoneManager = new ZoneManager();
    }

    public SiteDTO addSiteToZone(String siteAddress, String zoneName) throws NoSuchElementException, NullPointerException {
        if (siteAddress == null || zoneName == null) {
            throw new NullPointerException();
        }
        try {
            Optional<SiteDTO> site = siteManager.findSiteByAddress(siteAddress.toLowerCase());
            Optional<ZoneDTO> zone = zoneManager.findZoneByName(zoneName.toLowerCase());
            if (site.isEmpty() || zone.isEmpty()) {
                throw new NoSuchElementException();
            }
            return siteZoneManager.mapSiteToZone(site.get(), zone.get().zoneId());
        } catch (SQLException e) {
            throw new RuntimeException("Database access error");
        }
    }

    public SiteDTO removeSiteFromZone(String siteAddress) throws NoSuchElementException, NullPointerException {
        if (siteAddress == null) {
            throw new NullPointerException();
        }
        try {
            Optional<SiteDTO> site = siteManager.findSiteByAddress(siteAddress.toLowerCase());
            if (site.isEmpty()) {
                throw new NoSuchElementException();
            }
            return siteZoneManager.removeSiteFromZone(site.get());
        } catch (SQLException e) {
            throw new RuntimeException("Database access error");
        }
    }

    public List<SiteDTO> getSitesByZone(String zoneName) throws NoSuchElementException, NullPointerException {
        if (zoneName == null) {
            throw new NullPointerException();
        }
        try {
            Optional<ZoneDTO> zone = zoneManager.findZoneByName(zoneName.toLowerCase());
            if (zone.isEmpty()) {
                throw new NoSuchElementException();
            }
            return siteZoneManager.getSitesByZone(zone.get().zoneId());
        } catch (SQLException e) {
            throw new RuntimeException("Database access error");
        }
    }

    public ZoneDTO getZoneWithSites(String zoneName) throws NoSuchElementException, NullPointerException {
        if (zoneName == null) {
            throw new NullPointerException();
        }
        try {
            return siteZoneManager.getZoneWithSites(zoneName.toLowerCase());
        } catch (SQLException e) {
            throw new RuntimeException("Database access error");
        }
    }

    public String getZoneNameBySite(SiteDTO site) throws NoSuchElementException {
        try {
            return siteZoneManager.getZoneNameBySite(site);
        } catch (SQLException e) {
            throw new RuntimeException("Database access error");
        }
    }
}