package Transportation.Domain.Repositories;

import Transportation.DTO.ZoneDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ZoneRepository {
    ZoneDTO addZone(String zoneName) throws SQLException;
    void deleteZone(int zoneId) throws SQLException;
    ZoneDTO updateZone(ZoneDTO zoneDTO) throws SQLException;
    List<ZoneDTO> getAllZones() throws SQLException;
    Optional<ZoneDTO> findZone(int id) throws SQLException;
    Optional<ZoneDTO> findByZoneName(String name) throws SQLException;
}