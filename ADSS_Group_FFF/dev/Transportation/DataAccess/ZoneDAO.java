package Transportation.DataAccess;

import  Transportation.DTO.ZoneDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ZoneDAO {
    ZoneDTO insert(ZoneDTO zone) throws SQLException;
    void delete(int zoneId) throws SQLException;
    ZoneDTO update(ZoneDTO zone) throws SQLException;
    Optional<ZoneDTO> findById(int zoneId) throws SQLException;
    Optional<ZoneDTO> findByName(String zoneName) throws SQLException;
    List<ZoneDTO> findAll() throws SQLException;
}