package Transportation.Domain.Repositories;

import Transportation.DataAccess.DAO.SqliteZoneDAO;
import Transportation.Domain.Zone;
import Transportation.DTO.ZoneDTO;
import Transportation.DataAccess.DAO.ZoneDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ZoneRepositoryImpi implements ZoneRepository {

    private final ZoneDAO zoneDAO;

    public ZoneRepositoryImpi() {
        this.zoneDAO = new SqliteZoneDAO();

    }

    @Override
    public ZoneDTO insert(String zoneName) throws SQLException {
       return zoneDAO.insert(new zoneDTO(null,zoneName));
    }

    @Override
    public void update(ZoneDTO zone) throws SQLException {
        zoneDAO.update(zone);
    }

    @Override
    public void delete(ZoneDTO zone) throws SQLException {
        zoneDAO.delete(zone);
    }

    @Override
    public List<ZoneDTO> getAllZones() throws SQLException {
        return zoneDAO.findAll();
    }

    @Override
    public Optional<ZoneDTO> findById(int id) throws SQLException {
        return zoneDAO.findById(id);
    }

    @Override
    public Optional<ZoneDTO> findByName(String name) throws SQLException {
        return zoneDAO.findByName(name);
    }
}
