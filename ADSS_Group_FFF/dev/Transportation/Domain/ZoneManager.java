package Transportation.Domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class ZoneManager {
    private final HashMap<Integer, Zone> allZones;
    private int nextZoneId = 1;


    public ZoneManager() {
        allZones = new HashMap<Integer, Zone>();
    }

    public void addZone(String _zoneName) {
        int _zoneId = nextZoneId++;
        Zone newZone = new Zone(_zoneId, _zoneName);
        allZones.putIfAbsent(_zoneId, newZone);
    }

    public void removeZone(String _zoneName) {
        Zone z = getZoneByName(_zoneName);
        allZones.remove(z.getZoneId());
    }

    public Zone getZoneById(int zoneId) {
        return allZones.get(zoneId);
    }

    public String getSitesByZone(String zoneName) {
        Zone currZone = getZoneByName(zoneName);
        if (currZone == null) {
            throw new NoSuchElementException();
        }
        return currZone.toString();
    }

    public Zone getZoneByName(String zoneName) {
        for (Zone zone : allZones.values()) {
            if (zone.getName().equals(zoneName)) {
                return zone;
            }
        }
        throw new NoSuchElementException();
    }

    public void modifyZone(String _zoneName, String newZoneName) throws NoSuchElementException {
        Zone z = getZoneByName(_zoneName);
        if (z != null) {
            getZoneById(z.getZoneId()).setZoneName(newZoneName);
        }
        else {
            throw new NoSuchElementException();
        }
    }

    public boolean doesZoneExist(String zoneName) {
        int zoneId = getZoneByName(zoneName.toLowerCase()).getZoneId();
        return allZones.containsKey(zoneId);
    }

    public List<Zone> getAllZones() {
        return new ArrayList<>(allZones.values());
    }

    public String getAllZonesString() {
        List<Zone> allZones = getAllZones();
        if (allZones.isEmpty()) return "No zones available.";

        StringBuilder sb = new StringBuilder("All Zones:\n");
        for (Zone z : allZones) {
            sb.append(z).append("\n----------------------\n");
        }
        return sb.toString();
    }
}