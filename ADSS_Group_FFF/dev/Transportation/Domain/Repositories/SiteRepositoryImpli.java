package Transportation.Domain.Repositories;

import Transportation.DTO.SiteDTO;
import Transportation.DataAccess.SiteDAO;
import Transportation.DataAccess.SqliteSiteDAO;
import Transportation.Domain.Site;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SiteRepositoryImpli implements SiteRepository {

    private final SiteDAO siteDAO;
    private final ArrayList<Site> tempSiteList;


    public SiteRepositoryImpli() {
        this.tempSiteList = new ArrayList<>();
        this.siteDAO = new SqliteSiteDAO();
    }

    @Override
    public SiteDTO addSite(String address, String contactName, String phoneNumber) throws SQLException {
        SiteDTO newSite = siteDAO.insert(new SiteDTO(null, address, contactName, phoneNumber, -1));
        tempSiteList.add(new Site(newSite.siteId(), newSite.siteAddress(), newSite.contactName(), newSite.phoneNumber(), newSite.zoneId()));
        return newSite;
    }

    @Override
    public void deleteSite(int siteId) throws SQLException {
        Site site = findSiteInList(siteId);
        if (site != null) {
            tempSiteList.remove(site);
        }
        siteDAO.delete(siteId);
    }

    @Override
    public SiteDTO mapSiteToZone(SiteDTO site, int zoneId) throws SQLException {
        Site siteDomain = findSiteInList(site.siteId());
        if (siteDomain != null) {
            siteDomain.setZoneId(zoneId);
        }
        return siteDAO.update(site, zoneId);
    }

    @Override
    public List<SiteDTO> getAllSites() throws SQLException {
        if (!tempSiteList.isEmpty()) {
            List<SiteDTO> returnedList = new ArrayList<>();
            for (Site currSite : tempSiteList) {
                returnedList.add(toDTO(currSite));
            }
            return returnedList;
        } else {
            return siteDAO.findAll();
        }
    }

    @Override
    public List<SiteDTO> findAllByZoneId(int zoneId) throws SQLException {
        if (!tempSiteList.isEmpty()) {
            List<SiteDTO> returnedList = new ArrayList<>();
            for (Site site : tempSiteList) {
                if (site.getZone() == zoneId) {
                    returnedList.add(toDTO(site));
                }
            }
            return returnedList;
        }

        return siteDAO.findAllByZoneId(zoneId);
    }

    @Override
    public Optional<SiteDTO> findSite(int siteId) throws SQLException {
        Site site = findSiteInList(siteId);
        if (site != null) {
            return Optional.of(toDTO(site));
        }

        return siteDAO.findById(siteId);
    }

    @Override
    public Optional<SiteDTO> findBySiteAddress(String address) throws SQLException {
        Site site = findSiteInListAddress(address);
        if (site != null) {
            return Optional.of(toDTO(site));
        }

        return siteDAO.findByAddress(address);
    }

    // helper methods
    public SiteDTO toDTO(Site site) {
        return new SiteDTO(site.getSiteId(), site.getAddress(), site.getContactName(), site.getPhoneNumber(), site.getZone());
    }

    @Override
    public Site fromSiteDTO(SiteDTO siteDTO) {
        return new Site(siteDTO.siteId(), siteDTO.siteAddress(), siteDTO.contactName(), siteDTO.contactName(), siteDTO.zoneId());
    }

    private Site findSiteInList(int siteId) {
        for (Site currSite : tempSiteList) {
            if (currSite.getSiteId() == siteId) {
                return currSite;
            }
        }
        return null;
    }

    private Site findSiteInListAddress(String address) {
        for (Site currSite : tempSiteList) {
            if (currSite.getAddress().equals(address)) {
                return currSite;
            }
        }
        return null;
    }
}