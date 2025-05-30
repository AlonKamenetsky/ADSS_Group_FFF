package Transportation.Domain.Repositories;

import Transportation.DTO.SiteDTO;
import Transportation.DataAccess.SiteDAO;
import Transportation.DataAccess.SqliteSiteDAO;
import Transportation.Domain.Site;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SiteRepositoryImpli implements SiteRepository {
    private final SiteDAO siteDAO;


    public SiteRepositoryImpli() {
        this.siteDAO = new SqliteSiteDAO();
    }

    @Override
    public SiteDTO addSite(String address, String contactName, String phoneNumber) throws SQLException {
        return siteDAO.insert(new SiteDTO(null, address, contactName, phoneNumber, -1));
    }

    @Override
    public void deleteSite(int siteId) throws SQLException {
        siteDAO.delete(siteId);
    }

    @Override
    public SiteDTO mapSiteToZone(SiteDTO site, int zoneId) throws SQLException {
        return siteDAO.update(site, zoneId);
    }

    @Override
    public List<SiteDTO> findAll() throws SQLException {
        return siteDAO.findAll();
    }

    @Override
    public List<SiteDTO> findAllByZoneId(int zoneId) throws SQLException {
        return siteDAO.findAllByZoneId(zoneId);
    }

    @Override
    public Optional<SiteDTO> findSite(int siteId) throws SQLException {
        return siteDAO.findById(siteId);
    }

    @Override
    public Optional<SiteDTO> findBySiteAddress(String address) throws SQLException {
        return siteDAO.findByAddress(address);
    }

    public SiteDTO toDTO(Site site) {
        return new SiteDTO(site.getSiteId(), site.getAddress(), site.getContactName(), site.getPhoneNumber(), site.getZone());
    }

    @Override
    public Site fromSiteDTO(SiteDTO siteDTO) {
        return new Site(siteDTO.siteId(),siteDTO.siteAddress(),siteDTO.contactName(),siteDTO.contactName(),siteDTO.zoneId());

    }
}