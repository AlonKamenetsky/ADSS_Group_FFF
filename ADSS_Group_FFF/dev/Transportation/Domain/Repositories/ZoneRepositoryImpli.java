package Transportation.Domain.Repositories;

import Transportation.DTO.TruckDTO;
import Transportation.DataAccess.SqliteZoneDAO;
import Transportation.DTO.ZoneDTO;
import Transportation.DataAccess.ZoneDAO;
import Transportation.Domain.Site;
import Transportation.Domain.Truck;
import Transportation.Domain.TruckType;
import Transportation.Domain.Zone;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ZoneRepositoryImpli implements ZoneRepository {

    private final ZoneDAO zoneDAO;
    private final ArrayList<Zone> tempZoneList;

    public ZoneRepositoryImpli() {
        this.tempZoneList = new ArrayList<>();
        this.zoneDAO = new SqliteZoneDAO();
    }

    @Override
    public ZoneDTO addZone(String zoneName) throws SQLException {
        ZoneDTO zoneDTO = zoneDAO.insert(new ZoneDTO(null,zoneName,new ArrayList<>()));
        tempZoneList.add(new Zone(zoneDTO.zoneId(), zoneDTO.zoneName()));
        return zoneDTO;
    }

    @Override
    public void deleteZone(int zoneId) throws SQLException {
        Zone zone = findZoneInList(zoneId);
        if (zone != null) {
            tempZoneList.remove(zone);
        }
        zoneDAO.delete(zoneId);
    }

    // check again
    @Override
    public ZoneDTO updateZone(ZoneDTO updatedZone) throws SQLException {
        ZoneDTO updated = zoneDAO.update(updatedZone);
        for (Zone zone : tempZoneList) {
          if(toDTO(zone).equals(updatedZone))  {
            zone.setZoneName(updated.zoneName());
          }
        }
        return updated;
    }

    @Override
    public List<ZoneDTO> getAllZones() throws SQLException {
        if (!tempZoneList.isEmpty()) {
            List<ZoneDTO> returnedList = new ArrayList<>();
            for (Zone currZone : tempZoneList) {
                returnedList.add(toDTO(currZone));
            }
            return returnedList;
        } else {
            return zoneDAO.findAll();
        }
    }

    @Override
    public Optional<ZoneDTO> findZone(int zoneId) throws SQLException {
        Zone zone = findZoneInList(zoneId);
        if (zone != null){
            ZoneDTO zoneDTO = toDTO(zone);
            return Optional.of(zoneDTO);
        }
        return zoneDAO.findById(zoneId);
    }

    @Override
    public Optional<ZoneDTO> findByZoneName(String name) throws SQLException {
        for (Zone zone : tempZoneList) {
            zone.getName().equals(name);
            ZoneDTO zoneDTO = toDTO(zone);
            return Optional.of(zoneDTO);
        }
        return zoneDAO.findByName(name);
    }

    //Helping methods
    private  ZoneDTO toDTO(Zone zone) {
        return new ZoneDTO(zone.getZoneId(),zone.getName(), zone.getSitesRelated().stream()
                .map(Site::getAddress)
                .collect(Collectors.toCollection(ArrayList::new)));
    }

    private Zone fromDTO(ZoneDTO dto) {
        return new Zone(dto.zoneId(), dto.zoneName());
    }


    private Zone findZoneInList(int zoneId) {
        for (Zone currZone : tempZoneList) {
            if (currZone.getZoneId() == zoneId) {
                return currZone;
            }
        }
        return null;
    }
}