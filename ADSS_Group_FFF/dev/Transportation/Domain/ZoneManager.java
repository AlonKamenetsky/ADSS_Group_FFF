package Transportation.Domain;

import Transportation.DTO.ZoneDTO;
import Transportation.Domain.Repositories.ZoneRepository;
import Transportation.Domain.Repositories.ZoneRepositoryImpli;

import javax.management.InstanceAlreadyExistsException;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ZoneManager {
    private final ZoneRepository zoneRepo;

    public ZoneManager() {
        this.zoneRepo = new ZoneRepositoryImpli();
    }

    //for mock tests
    public ZoneManager(ZoneRepository zoneRepository) {
        this.zoneRepo = zoneRepository;
    }

    public ZoneDTO addZone(String _zoneName) throws SQLException, InstanceAlreadyExistsException {
        if(findZoneByName(_zoneName).isPresent()) {
            throw new InstanceAlreadyExistsException();
        }
        else {
            return zoneRepo.addZone(_zoneName);
        }
    }

    public void removeZone(int zoneId) throws SQLException, NoSuchElementException {
        if (getZoneById(zoneId).isEmpty()) {
            throw new NoSuchElementException();
        }
        else {
            zoneRepo.deleteZone(zoneId);
        }
    }

    public Optional<ZoneDTO> getZoneById(int zoneId) throws SQLException {
        return zoneRepo.findZone(zoneId);
    }

    public ZoneDTO modifyZone(ZoneDTO updatedZone) throws SQLException {
        return zoneRepo.updateZone(updatedZone);
    }

    public List<ZoneDTO> getAllZones() throws SQLException {
        return zoneRepo.getAllZones();
    }

    // might not need, check later. can use DTO attributes and print simply.
    public String getAllZonesString() throws SQLException {
        List<ZoneDTO> allZones = getAllZones();
        if (allZones.isEmpty()) return "No zones available.";

        StringBuilder sb = new StringBuilder("All Zones:\n");
        for (ZoneDTO z : allZones) {
            sb.append(z).append("\n----------------------\n");
        }
        return sb.toString();
    }

    // new methods for part B
    public Optional<ZoneDTO> findZoneByName(String name) throws SQLException {
        return zoneRepo.findByZoneName(name);
    }
}