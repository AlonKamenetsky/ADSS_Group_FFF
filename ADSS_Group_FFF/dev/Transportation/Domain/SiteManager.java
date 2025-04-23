package Transportation.Domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SiteManager {
    private final HashMap<Integer, Site> allSites;
    private final ZoneManager zoneManager;
    private int nextSiteId = 1;

    public SiteManager(ZoneManager _zoneManager) {
        zoneManager = _zoneManager;
        allSites = new HashMap<Integer, Site>();
    }

    public void addSite(String _address, String _contactName, String _phoneNumber, String _zone) {
        int _siteId = nextSiteId++;
        Zone relatedZone = zoneManager.getZoneByName(_zone.toLowerCase());
        if (relatedZone == null) {
            throw new IllegalArgumentException("Invalid zone.");
        }
        int relatedZoneId = relatedZone.getZoneId();
        Site newSite = new Site(_siteId, _address.toLowerCase(), _contactName, _phoneNumber, relatedZoneId);
        allSites.putIfAbsent(_siteId, newSite);
    }

    public void removeSite(String _address) {
        Site site = getSiteByAddress(_address.toLowerCase());
        allSites.remove(site.getSiteId());
    }

    public List<Site> getAllSites() {
        return new ArrayList<>(allSites.values());
    }

    public Site getSiteById(int _siteId) {
        return allSites.get(_siteId);
    }

    public Site getSiteByAddress(String address) {
        for (Site site : allSites.values()) {
            if (site.getAddress().equalsIgnoreCase(address)) {
                return site;
            }
        }
        return null;
    }

    public void modifySiteZone(String siteAddress, int zoneId) {
        for (Site site : allSites.values()) {
            if (site.getAddress().equalsIgnoreCase(siteAddress)) {
                site.setZoneId(zoneId);
            }
        }
    }

    public int getSiteZone(String siteAddress) {
        for (Site site : allSites.values()) {
            if (site.getAddress().equalsIgnoreCase(siteAddress)) {
                return site.getZone();
            }
        }
        return -1;
    }

    public boolean doesSiteExist(String address) {
        int _siteId = getSiteByAddress(address).getSiteId();
        return allSites.containsKey(_siteId);
    }

    public String viewSite(String address) {
        Site currSite = getSiteByAddress(address);
        if (currSite == null) {
            return "Site not found.\n";
        }

        return formatSite(currSite);
    }

    public String viewAllSites() {
        if (allSites.isEmpty()) return "No sites available.";

        StringBuilder sb = new StringBuilder("All Sites:\n");

        for (Site site : allSites.values()) {
            sb.append(formatSite(site));
        }

        return sb.toString();
    }

    private String formatSite(Site site) {
        Zone zone = zoneManager.getZoneById(site.getZone());
        String zoneName = (zone != null) ? zone.getName() : "Unknown";

        return String.format(
                "Site Address: %s\nContact: %s\nPhone: %s\nZone: %s\n----------------------\n",
                site.getAddress(),
                site.getContactName(),
                site.getPhoneNumber(),
                zoneName.toUpperCase()
        );
    }
}