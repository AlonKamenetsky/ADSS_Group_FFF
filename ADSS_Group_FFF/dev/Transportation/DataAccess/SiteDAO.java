package Transportation.DataAccess;

import  Transportation.DTO.SiteDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface SiteDAO {
     SiteDTO insert(SiteDTO site) throws SQLException;
     void delete(int siteId) throws SQLException;
     SiteDTO update(SiteDTO site, int zoneId) throws SQLException;
     List<SiteDTO> findAll() throws SQLException;
     List<SiteDTO> findAllByZoneId(int zoneId) throws SQLException;
     Optional<SiteDTO> findById(int siteId) throws SQLException;
     Optional<SiteDTO> findByAddress(String address) throws SQLException;
}