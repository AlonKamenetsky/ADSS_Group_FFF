package Transportation.Domain.Repositories;

import Transportation.DataAccess.SqliteZoneDAO;
import Transportation.DTO.ZoneDTO;
import Transportation.DataAccess.ZoneDAO;
import Transportation.Domain.Site;
import Transportation.Domain.Zone;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ZoneRepositoryImpli implements ZoneRepository {

    private final ZoneDAO zoneDAO;

    public ZoneRepositoryImpli() {
        this.zoneDAO = new SqliteZoneDAO();
    }

    @Override
    public ZoneDTO addZone(String zoneName) throws SQLException {
        return zoneDAO.insert(new ZoneDTO(null,zoneName,new ArrayList<>()));
    }

    @Override
    public void deleteZone(int zoneId) throws SQLException {
        zoneDAO.delete(zoneId);
    }

    // check again
    @Override
    public ZoneDTO updateZone(ZoneDTO updatedZone) throws SQLException {
        return zoneDAO.update(updatedZone);
    }

    @Override
    public List<ZoneDTO> getAllZones() throws SQLException {
            return zoneDAO.findAll();

    }

    @Override
    public Optional<ZoneDTO> findZone(int zoneId) throws SQLException {
        return zoneDAO.findById(zoneId);
    }

    @Override
    public Optional<ZoneDTO> findByZoneName(String name) throws SQLException {
        return zoneDAO.findByName(name);
    }

    //Helping methods
    private  ZoneDTO toDTO(Zone zone) {
        return new ZoneDTO(zone.getZoneId(),zone.getName(), zone.getSitesRelated().stream()
                .map(Site::getAddress)
                .collect(Collectors.toCollection(ArrayList::new)));
    }
}