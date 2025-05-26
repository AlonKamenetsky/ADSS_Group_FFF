package Transportation.Domain.Repositories;

import Transportation.DTO.ZoneDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ZoneRepository {
    ZoneDTO insert(String zoneName) throws SQLException;
    void update(ZoneDTO zone) throws SQLException;
    void delete(ZoneDTO zone) throws SQLException;
    List<ZoneDTO> getAllZones() throws SQLException;
    Optional<ZoneDTO> findById(int id) throws SQLException;
    Optional<ZoneDTO> findByName(String name) throws SQLException;

}
