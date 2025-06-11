package Transportation.Domain;

public class Site {
    private final int siteId;
    private final String address;
    private final String contactName;
    private final String phoneNumber;
    private int zoneId;


    public Site(int _siteId, String _address, String _contactName, String _phoneNumber, int zoneId) {
        siteId = _siteId;
        address = _address;
        contactName = _contactName;
        phoneNumber = _phoneNumber;
        this.zoneId = zoneId;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Site otherSite)) return false;
        return siteId == otherSite.siteId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setZoneId(int zoneId) { this.zoneId = zoneId; }

    public int getSiteId() {
        return siteId;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getZone() {
        return zoneId;
    }

    @Override
    public String toString() {
        return getAddress();
    }
}