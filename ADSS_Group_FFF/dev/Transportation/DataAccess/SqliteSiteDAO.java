package Transportation.DataAccess;

import Transportation.DTO.SiteDTO;
import Util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteSiteDAO implements SiteDAO {

    @Override
    public List<SiteDTO> findAll() throws SQLException {
        String sql = "SELECT site_id, address, contact_name, phone_number, zone_id FROM sites ORDER BY site_id";
        try (Statement st = Database.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            List<SiteDTO> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new SiteDTO(
                        rs.getInt("site_id"),
                        rs.getString("address"),
                        rs.getString("contact_name"),
                        rs.getString("phone_number"),
                        rs.getInt("zone_id")
                ));
            }
            return list;
        }
    }

    @Override
    public List<SiteDTO> findAllByZoneId(int zoneId) throws SQLException {
        String sql = "SELECT site_id, address, contact_name, phone_number, zone_id FROM sites WHERE zone_id = ?";

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, zoneId);

            try (ResultSet rs = ps.executeQuery()) {
                List<SiteDTO> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new SiteDTO(
                            rs.getInt("site_id"),
                            rs.getString("address"),
                            rs.getString("contact_name"),
                            rs.getString("phone_number"),
                            rs.getInt("zone_id")
                    ));
                }
                return list;
            }
        }
    }


    @Override
    public Optional<SiteDTO> findById(int siteId) throws SQLException {
        String sql = "SELECT site_id, address, contact_name, phone_number, zone_id FROM sites WHERE site_id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, siteId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(new SiteDTO(
                        rs.getInt("site_id"),
                        rs.getString("address"),
                        rs.getString("contact_name"),
                        rs.getString("phone_number"),
                        rs.getInt("zone_id")
                ))
                        : Optional.empty();
            }
        }
    }

    @Override
    public Optional<SiteDTO> findByAddress(String address) throws SQLException {
        String sql = "SELECT site_id, address, contact_name, phone_number, zone_id FROM sites WHERE address = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, address);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(new SiteDTO(
                        rs.getInt("site_id"),
                        rs.getString("address"),
                        rs.getString("contact_name"),
                        rs.getString("phone_number"),
                        rs.getInt("zone_id")
                ))
                        : Optional.empty();
            }
        }
    }

    @Override
    public SiteDTO insert(SiteDTO site) throws SQLException {
        String sql = "INSERT INTO sites(address, contact_name, phone_number, zone_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = Database.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, site.siteAddress());
            ps.setString(2, site.contactName());
            ps.setString(3, site.phoneNumber());
            ps.setInt(4, site.zoneId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new SiteDTO(keys.getInt(1), site.siteAddress(), site.contactName(), site.phoneNumber(), site.zoneId());
            }
        }
    }

    @Override
    public void delete(int siteId) throws SQLException {
        String sql = "DELETE FROM sites WHERE site_id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, siteId);
            ps.executeUpdate();
        }
    }

    public SiteDTO update(SiteDTO site, int zoneId) throws SQLException {
        String sql = "UPDATE sites SET zone_id = ? WHERE site_id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, zoneId);
            ps.setInt(2, site.siteId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new SiteDTO(keys.getInt(1), site.siteAddress(), site.contactName(), site.phoneNumber(), zoneId);
            }
        }
    }

    // helper method
    private int getSiteIdByAddress(String address) throws SQLException {
        String sql = "SELECT site_id FROM sites WHERE address = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, address);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("site_id");
                } else {
                    throw new SQLException("Site not found: " + address);
                }
            }
        }
    }
}