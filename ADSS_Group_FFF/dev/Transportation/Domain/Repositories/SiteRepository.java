package Transportation.Domain.Repositories;
import Transportation.Domain.Site;
import Transportation.Domain.Zone;
import Transportation.DTO.SiteDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface SiteRepository {
    SiteDTO addSite(String address, String _contactName, String _phoneNumber, int zoneId) throws SQLException;
    void deleteSite(SiteDTO site) throws SQLException;
    List<SiteDTO> findAll() throws SQLException;
    Optional<SiteDTO> findSite(int siteId) throws SQLException;
    Optional<SiteDTO> findBySiteAddress(String address) throws SQLException;

}
