package Transportation.Service;

import Transportation.DTO.ZoneDTO;
import Transportation.Domain.ZoneManager;

import javax.management.InstanceAlreadyExistsException;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ZoneService {
    private final ZoneManager zoneManager;

    public ZoneService(ZoneManager zoneManager) {
        this.zoneManager = new ZoneManager();
    }

    public void AddZone(String _zoneName) throws NullPointerException, InstanceAlreadyExistsException {
        if (_zoneName == null) {
            throw new NullPointerException();
        }
        try {
            zoneManager.addZone(_zoneName.toLowerCase());
        } catch (SQLException e) {
            throw new RuntimeException("Could not create user due to a system error.");
        }
    }

    public void deleteZone(String _zoneName) throws NullPointerException, NoSuchElementException {
        if (_zoneName == null) {
            throw new NullPointerException();
        }
        try {
            Optional<ZoneDTO> maybeZone = zoneManager.findZoneByName(_zoneName);
            if (maybeZone.isEmpty()) {
                throw new NoSuchElementException();
            }

            int zoneId = maybeZone.get().zoneId();
            zoneManager.removeZone(zoneId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void UpdateZone(String _zoneName, String newZoneName) throws NullPointerException, NoSuchElementException {
        if (_zoneName == null || newZoneName == null) {
            throw new NullPointerException();
        }
        try {
            Optional<ZoneDTO> maybeZone = zoneManager.findZoneByName(_zoneName);
            if (maybeZone.isEmpty()) {
                throw new NoSuchElementException();
            }

            ZoneDTO existingZone = maybeZone.get();
            ZoneDTO updatedZone = new ZoneDTO(
                    existingZone.zoneId(),
                    newZoneName,
                    existingZone.sitesRelated()
            );
            zoneManager.modifyZone(updatedZone);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ZoneDTO> viewAllZones() {
        try {
            return zoneManager.getAllZones();
        }
        catch(SQLException e) {
            throw new RuntimeException();
        }
    }

    // will occur in presentation using ZoneDTO.isEmpty()
//    public void doesZoneExist(String zoneName) throws NullPointerException, NoSuchElementException {
//        if (zoneName == null) {
//            throw new NullPointerException();
//        }
//        if (!zoneManager.doesZoneExist(zoneName.toLowerCase())) {
//            throw new NoSuchElementException();
//        }
//    }
}