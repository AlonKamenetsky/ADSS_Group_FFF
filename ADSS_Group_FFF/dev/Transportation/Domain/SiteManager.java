package Transportation.Domain;

import Transportation.DTO.SiteDTO;
import Transportation.Domain.Repositories.SiteRepository;
import Transportation.Domain.Repositories.SiteRepositoryImpli;

import javax.management.InstanceAlreadyExistsException;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class SiteManager {
    private final SiteRepository siteRepository;

    public SiteManager() {
        siteRepository = new SiteRepositoryImpli();
    }

    //for mock test
    public SiteManager(SiteRepository siteRepository1){
        this.siteRepository = siteRepository1;
    }

    public SiteDTO addSite(String address, String contactName, String phoneNumber) throws SQLException, InstanceAlreadyExistsException {
        if (findSiteByAddress(address).isPresent()) {
            throw new InstanceAlreadyExistsException();
        } else {
            return siteRepository.addSite(address, contactName, phoneNumber);
        }
    }

    public void removeSite(int siteId) throws SQLException, NoSuchElementException {
        if (getSiteById(siteId).isEmpty()) {
            throw new NoSuchElementException();
        } else {
            siteRepository.deleteSite(siteId);
        }
    }

    public SiteDTO mapSiteToZone(SiteDTO updateSite, int zoneId) throws SQLException {
        return siteRepository.mapSiteToZone(updateSite, zoneId);
    }

    public List<SiteDTO> findAllByZoneId(int zoneId) throws SQLException {
        return siteRepository.findAllByZoneId(zoneId);
    }

    public Optional<SiteDTO> getSiteById(int siteId) throws SQLException {
        return siteRepository.findSite(siteId);
    }

    public List<SiteDTO> getAllSites() throws SQLException {
        return siteRepository.getAllSites();
    }

    // new methods for part B
    public Optional<SiteDTO> findSiteByAddress(String address) throws SQLException {
        return siteRepository.findBySiteAddress(address);
    }
}