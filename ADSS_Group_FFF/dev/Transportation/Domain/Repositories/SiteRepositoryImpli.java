package Transportation.Domain.Repositories;

import Transportation.DTO.SiteDTO;
import Transportation.DataAccess.SiteDAO;
import Transportation.DataAccess.SqliteSiteDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SiteRepositoryImpli implements SiteRepository {
    private final SiteDAO siteDAO;

    public SiteRepositoryImpli() {
        this.siteDAO = new SqliteSiteDAO();
    }

    @Override
    public SiteDTO addSite(String address, String contactName, String phoneNumber, int zoneId) throws SQLException {
        return siteDAO.insert(new SiteDTO(null, address, contactName, phoneNumber, zoneId));
    }

    @Override
    public void deleteSite(int siteId) throws SQLException {
        siteDAO.delete(siteId);
    }

    @Override
    public List<SiteDTO> findAll() throws SQLException {
        return siteDAO.findAll();
    }

    @Override
    public Optional<SiteDTO> findSite(int siteId) throws SQLException {
        return siteDAO.findById(siteId);
    }

    @Override
    public Optional<SiteDTO> findBySiteAddress(String address) throws SQLException {
        return siteDAO.findByAddress(address);
    }
}