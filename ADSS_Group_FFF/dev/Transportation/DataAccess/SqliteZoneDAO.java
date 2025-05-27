package Transportation.DataAccess;

import Transportation.DTO.ZoneDTO;
import Util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteZoneDAO implements ZoneDAO {

    @Override
    public ZoneDTO insert(ZoneDTO zone) throws SQLException {
        if (zone.zoneId() == null) {
            String sql = "INSERT INTO zones(zone_name) VALUES (?)";
            try (PreparedStatement ps = Database.getConnection()
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, zone.zoneName());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    return new ZoneDTO(keys.getInt(1), zone.zoneName(), new ArrayList<>());
                }
            }
        } else {
            String sql = "UPDATE zones SET zone_name = ? WHERE zone_id = ?";
            try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
                ps.setString(1, zone.zoneName());
                ps.setInt(2, zone.zoneId());
                ps.executeUpdate();
                return zone;
            }
        }
    }

    @Override
    public void delete(int zoneId) throws SQLException {
        String sql = "DELETE FROM zones WHERE zone_id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, zoneId);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<ZoneDTO> findById(int zoneId) throws SQLException {
        String sql = "SELECT zone_id, zone_name FROM zones WHERE zone_id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, zoneId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(new ZoneDTO(
                        rs.getInt("zone_id"),
                        rs.getString("zone_name"),
                        getSitesForZone(zoneId)
                ))
                        : Optional.empty();
            }
        }
    }

    @Override
    public Optional<ZoneDTO> findByName(String zoneName) throws SQLException {
        String sql = "SELECT zone_id, zone_name FROM zones WHERE zone_name = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, zoneName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("zone_id");
                    return Optional.of(new ZoneDTO(
                            id,
                            rs.getString("zone_name"),
                            getSitesForZone(id)
                    ));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    @Override
    public List<ZoneDTO> findAll() throws SQLException {
        String sql = "SELECT zone_id, zone_name FROM zones ORDER BY zone_id";
        List<ZoneDTO> list = new ArrayList<>();

        try (Statement st = Database.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int zoneId = rs.getInt("zone_id");
                list.add(new ZoneDTO(
                        zoneId,
                        rs.getString("zone_name"),
                        getSitesForZone(zoneId)
                ));
            }
        }

        return list;
    }


    // helper method retrieving sites by zone
    private ArrayList<String> getSitesForZone(int zoneId) throws SQLException {
        String sql = "SELECT address FROM sites WHERE zone_id = ?";
        ArrayList<String> siteAddresses = new ArrayList<>();

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, zoneId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    siteAddresses.add(rs.getString("address"));
                }
            }
        }

        return siteAddresses;
    }
}
