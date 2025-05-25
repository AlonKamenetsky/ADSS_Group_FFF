package Transportation.Service;

import Transportation.Domain.SiteManager;
import Transportation.Domain.SiteZoneManager;

import java.util.NoSuchElementException;

public class SiteService {
    private final SiteManager siteManager;
    private final SiteZoneManager siteZoneManager;

    public SiteService(SiteManager siteManager, SiteZoneManager siteZoneManager) {
        this.siteManager = siteManager;
        this.siteZoneManager = siteZoneManager;
    }

    public void addSite(String _address, String _contactName, String _phoneNumber, String _zone) throws NullPointerException, IllegalArgumentException, NoSuchElementException {
        if (_address == null || _contactName == null || _phoneNumber == null || _zone == null) {
            throw new NullPointerException();
        }
        if (!_contactName.matches("[a-zA-Z]+")) {
            throw new IllegalArgumentException("Not a valid contact name.");
        }
        siteManager.addSite(_address.toLowerCase(), _contactName, _phoneNumber, _zone.toLowerCase());
        siteZoneManager.addSiteToZone(_address.toLowerCase(), _zone.toLowerCase());
    }

    public void deleteSite(String _address) throws NullPointerException, NoSuchElementException {
        if (_address == null) {
            throw new NullPointerException();
        }
        if (siteManager.doesSiteExist(_address.toLowerCase())) {
            siteManager.removeSite(_address.toLowerCase());
        } else {
            throw new NoSuchElementException();
        }
    }

    public String getSiteByAddress(String _address) {
        if (_address == null) {
            return null;
        }
        if (siteManager.doesSiteExist(_address.toLowerCase())) {
            return siteManager.viewSite(_address.toLowerCase());
        }
        return "Site not found";
    }

    public String viewAllSites() {
        return siteManager.viewAllSites();
    }
}