package Transportation.Service;

import Transportation.DTO.SiteDTO;
import Transportation.Domain.SiteManager;

import javax.management.InstanceAlreadyExistsException;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class SiteService {
    private final SiteManager siteManager;

    public SiteService() {
        this.siteManager = new SiteManager();
    }

    public SiteDTO addSite(String _address, String _contactName, String _phoneNumber) throws NullPointerException, IllegalArgumentException, NoSuchElementException, InstanceAlreadyExistsException {
        if (_address == null || _contactName == null || _phoneNumber == null) {
            throw new NullPointerException();
        }
        if (!_contactName.matches("[a-zA-Z]+")) {
            throw new IllegalArgumentException("Not a valid contact name.");
        }
        try {
            return siteManager.addSite(_address.toLowerCase(), _contactName, _phoneNumber);
        } catch (SQLException e) {
            throw new RuntimeException("Database access error");
        }
    }

    public void deleteSite(String _address) throws NullPointerException, NoSuchElementException {
        if (_address == null) {
            throw new NullPointerException();
        }
        try {
            Optional<SiteDTO> maybeSite = siteManager.findSiteByAddress(_address.toLowerCase());
            if (maybeSite.isEmpty()) {
                throw new NoSuchElementException();
            }

            int siteId = maybeSite.get().zoneId();
            siteManager.removeSite(siteId);
        } catch (SQLException e) {
            throw new RuntimeException("Database access error");
        }
    }

    public Optional<SiteDTO> getSiteByAddress(String address) throws NullPointerException {
        if (address == null) {
            throw new NullPointerException();
        }
        try {
            return siteManager.findSiteByAddress(address.toLowerCase());
        } catch (SQLException e) {
            throw new RuntimeException("Database access error");
        }
    }

    public List<SiteDTO> viewAllSites() {
        try {
            return siteManager.getAllSites();
        } catch (SQLException e) {
            throw new RuntimeException("Database access error");
        }
    }
}