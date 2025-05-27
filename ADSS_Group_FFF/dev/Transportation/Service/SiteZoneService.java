package Transportation.Service;

import Transportation.DTO.SiteDTO;
import Transportation.Domain.SiteZoneManager;

import java.util.List;
import java.util.NoSuchElementException;

public class SiteZoneService {
    private final SiteZoneManager siteZoneManager;

    public SiteZoneService(SiteZoneManager szm) {
        this.siteZoneManager = szm;
    }

    public void addSiteToZone(String siteAddress, String zoneName) throws NoSuchElementException, NullPointerException {
        if(siteAddress == null || zoneName == null) {
            throw new NullPointerException();
        }
        siteZoneManager.addSiteToZone(siteAddress.toLowerCase(), zoneName.toLowerCase());
    }

    public void removeSiteFromZone(String siteAddress, String zoneName) throws NoSuchElementException, NullPointerException {
        if(siteAddress == null || zoneName == null) {
            throw new NullPointerException();
        }
       siteZoneManager.removeSiteFromZone(siteAddress.toLowerCase(), zoneName.toLowerCase());
    }

    public List<SiteDTO> getSitesByZone(String zoneName) {
        if (zoneName == null) {
            throw new NullPointerException();
        }
        return zoneManager.getSitesByZone(zoneName.toLowerCase());
    }
}