package Transportation.Data.DTO;

import Transportation.Domain.Site;
import Transportation.Domain.Zone;

import java.util.ArrayList;

public class ZoneDTO {
    private final int zoneId;
    private final String zoneName;
    private final ArrayList<String> sitesRelated;

    //Constructor
    public ZoneDTO(int zoneId, String zoneName, ArrayList<String> sitesRelated) {
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.sitesRelated = sitesRelated;
    }

    //Convert zone to zoneDTO
    public static ZoneDTO fromEntity(Zone zone) {
        ArrayList<Site> sites = zone.getSitesRelated();
        ArrayList<String> addresses = new ArrayList<>();

        for (Site site : sites) {
            addresses.add(site.getAddress());
        }

        return new ZoneDTO(
                zone.getZoneId(),
                zone.getName(),
                addresses
        );
    }

    //Getters
    public int getZoneId() {
        return zoneId;
    }
    public String getZoneName() {
        return zoneName;
    }
    public ArrayList<String> getSitesRelated() {
        return sitesRelated;
    }
}
