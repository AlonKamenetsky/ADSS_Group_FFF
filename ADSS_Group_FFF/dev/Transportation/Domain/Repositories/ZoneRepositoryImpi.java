package Transportation.Domain.Repositories;

import Transportation.DataAccess.DAO.SqliteZoneDAO;
import Transportation.Domain.Zone;
import Transportation.DTO.ZoneDTO;
import Transportation.DataAccess.DAO.ZoneDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ZoneRepositoryImpi implements ZoneRepository {

    private final ZoneDAO zoneDAO;

    public ZoneRepositoryImpi() {
        this.zoneDAO = new SqliteZoneDAO();

    }

    @Override
    public ZoneDTO addZone(String zoneName, ArrayList<String> sites) throws SQLException {
       return zoneDAO.insert(new ZoneDTO(null,zoneName,sites));
    }

    @Override
    public void deleteZone(ZoneDTO zone) throws SQLException {
        zoneDAO.delete(zone);
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
