package Transportation.Service;

import Transportation.Domain.SiteZoneManager;

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
}