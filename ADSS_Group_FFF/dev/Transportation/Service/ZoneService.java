package Transportation.Service;

import Transportation.Domain.ZoneManager;

import java.util.NoSuchElementException;

public class ZoneService {
    private final ZoneManager zoneManager;

    public ZoneService(ZoneManager zoneManager) {
        this.zoneManager = zoneManager;
    }

    public void AddZone(String _zoneName) throws NullPointerException {
        if (_zoneName == null) {
            throw new NullPointerException();
        }
        zoneManager.addZone(_zoneName.toLowerCase());
    }

    public void deleteZone(String _zoneName) throws NullPointerException, NoSuchElementException {
        if (_zoneName == null) {
            throw new NullPointerException();
        }
        if (zoneManager.doesZoneExist(_zoneName.toLowerCase())) {
            zoneManager.removeZone(_zoneName.toLowerCase());
        } else {
            throw new NoSuchElementException();
        }
    }

    public void UpdateZone(String _zoneName, String newZoneName) throws NullPointerException, NoSuchElementException {
        if (_zoneName == null) {
            throw new NullPointerException();
        }
        if (zoneManager.doesZoneExist(_zoneName.toLowerCase())) {
            zoneManager.modifyZone(_zoneName.toLowerCase(), newZoneName.toLowerCase());
        }
    }

    public String getSitesByZone(String zoneName) {
        if (zoneName == null) {
            throw new NullPointerException();
        }
        return zoneManager.getSitesByZone(zoneName.toLowerCase());
    }


    public String viewAllZones() {
        return zoneManager.getAllZonesString();
    }

    public void doesZoneExist(String zoneName) throws NullPointerException, NoSuchElementException {
        if (zoneName == null) {
            throw new NullPointerException();
        }
        if (!zoneManager.doesZoneExist(zoneName.toLowerCase())) {
            throw new NoSuchElementException();
        }
    }
}