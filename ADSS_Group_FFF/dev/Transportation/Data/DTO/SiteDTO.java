package Transportation.Data.DTO;

import java.util.ArrayList;
import Transportation.Domain.Site;
import Transportation.Domain.Zone;

public class SiteDTO {
    private final int siteId;
    private final String siteName;
    private final String contactName;
    private final String phoneNumber;
    private final int zoneId;

    //Constructor
    public SiteDTO(int siteId, String siteName, String contactName, String phoneNumber, int zoneId) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
        this.zoneId = zoneId;
    }

    //Convert
    public static SiteDTO fromEntity(Site site) {
        return new SiteDTO(
                site.getSiteId(),
                site.getAddress(),
                site.getContactName(),
                site.getContactName(),
                site.getZone()
        );
    }

    //Getters
    public int getSiteId() {
        return siteId;
    }
    public String getSiteName() {
        return siteName;
    }
    public String getContactName() {
        return contactName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public int getZoneId() {
        return zoneId;
    }
}
