package Transportation.Domain.Repositories;

import Transportation.DTO.SiteDTO;
import Transportation.Domain.Site;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface SiteRepository {
    SiteDTO addSite(String address, String _contactName, String _phoneNumber) throws SQLException;
    void deleteSite(int siteId) throws SQLException;
    SiteDTO mapSiteToZone(SiteDTO site, int zoneId) throws SQLException;
    List<SiteDTO> getAllSites() throws SQLException;
    List<SiteDTO> findAllByZoneId(int zoneId) throws SQLException;
    Optional<SiteDTO> findSite(int siteId) throws SQLException;
    Optional<SiteDTO> findBySiteAddress(String address) throws SQLException;
    Site fromSiteDTO(SiteDTO siteDTO);
}