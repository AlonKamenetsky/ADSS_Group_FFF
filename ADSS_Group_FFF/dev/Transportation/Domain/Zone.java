package Transportation.Domain;

import java.util.ArrayList;

public class Zone {
    private final int zoneId;
    private String zoneName;
    private final ArrayList<Site> sitesRelated;

    public Zone(int _zoneId, String _zoneName) {
        zoneId = _zoneId;
        zoneName = _zoneName;
        sitesRelated = new ArrayList<>();
    }

    public void addSiteToZone(Site site) {
        sitesRelated.add(site);
    }

    public void removeSiteFromZone(Site site) {
        sitesRelated.remove(site);
    }

    public String getName() {
        return zoneName;
    }

    public int getZoneId() {
        return zoneId;
    }

    public ArrayList<Site> getSitesRelated () { return sitesRelated; }

    public void setZoneName(String newName) {
        zoneName = newName;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Zone Name: ").append(zoneName.toUpperCase()).append("\n");

        if (sitesRelated.isEmpty()) {
            sb.append("No sites assigned to this zone.");
        } else {
            sb.append("Sites in this zone:\n");
            for (Site site : sitesRelated) {
                sb.append("- ").append(site.getAddress()).append("\n");
            }
        }

        return sb.toString();
    }

}