package Transportation.Domain.Repositories;

import Transportation.DataAccess.SqliteZoneDAO;
import Transportation.DTO.ZoneDTO;
import Transportation.DataAccess.ZoneDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ZoneRepositoryImpli implements ZoneRepository {

    private final ZoneDAO zoneDAO;

    public ZoneRepositoryImpli() {
        this.zoneDAO = new SqliteZoneDAO();
    }

    @Override
    public ZoneDTO addZone(String zoneName) throws SQLException {
       return zoneDAO.insert(new ZoneDTO(null,zoneName, new ArrayList<>()));
    }


    @Override
    public void deleteZone(int zoneId) throws SQLException {
        zoneDAO.delete(zoneId);
    }

    @Override
    public ZoneDTO updateZone(ZoneDTO updatedZone) throws SQLException {
        return zoneDAO.update(updatedZone);
    }

    @Override
    public List<ZoneDTO> getAllZones() throws SQLException {
        return zoneDAO.findAll();
    }

    @Override
    public Optional<ZoneDTO> findZone(int id) throws SQLException {
        return zoneDAO.findById(id);
    }

    @Override
    public Optional<ZoneDTO> findByZoneName(String name) throws SQLException {
        return zoneDAO.findByName(name);
    }
}