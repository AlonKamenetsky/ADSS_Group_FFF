package Transportation.DataAccess;

import Transportation.DTO.TruckDTO;
import Transportation.Domain.TruckType;
import Util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class SqliteTruckDAO implements TruckDAO {

    @Override
    public TruckDTO insert(TruckDTO truck) throws SQLException {
        if (truck.truckId() == null) {
            String sql = "INSERT INTO trucks(truck_type, license_number, model, net_weight, max_weight, is_free) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = Database.getConnection()
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, truck.truckType().name());
                ps.setString(2, truck.licenseNumber());
                ps.setString(3, truck.model());
                ps.setFloat(4, truck.netWeight());
                ps.setFloat(5, truck.maxWeight());
                ps.setBoolean(6, truck.isFree());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    return new TruckDTO(
                            keys.getInt(1),
                            truck.truckType(),
                            truck.licenseNumber(),
                            truck.model(),
                            truck.netWeight(),
                            truck.maxWeight(),
                            truck.isFree()
                    );
                }
            }
        } else {
            String sql = "UPDATE trucks SET truck_type = ?, model = ?, net_weight = ?, max_weight = ?, is_free = ? " +
                    "WHERE truck_id = ?";
            try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
                ps.setString(1, truck.truckType().name());
                ps.setString(2, truck.model());
                ps.setFloat(3, truck.netWeight());
                ps.setFloat(4, truck.maxWeight());
                ps.setBoolean(5, truck.isFree());
                ps.setInt(6, truck.truckId());
                ps.executeUpdate();
                return truck;
            }
        }
    }


    @Override
    public Optional<TruckDTO> findByLicense(String licenseNumber) throws SQLException {
        String sql = "SELECT truck_id, truck_type, license_number, model, net_weight, max_weight, is_free " +
                "FROM trucks WHERE license_number = ?";

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, licenseNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(new TruckDTO(
                        rs.getInt("truck_id"),
                        TruckType.valueOf(rs.getString("truck_type")),
                        rs.getString("license_number"),
                        rs.getString("model"),
                        rs.getFloat("net_weight"),
                        rs.getFloat("max_weight"),
                        rs.getBoolean("is_free")
                ))
                        : Optional.empty();

            }
        }
    }

    @Override
    public ArrayList<TruckDTO> findAll() throws SQLException {
        String sql = "SELECT truck_id, truck_type, license_number, model, net_weight, max_weight, is_free " +
                "FROM trucks ORDER BY truck_id";
        ArrayList<TruckDTO> trucks = new ArrayList<>();

        try (Statement st = Database.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                trucks.add(new TruckDTO(
                        rs.getInt("truck_id"),
                        TruckType.valueOf(rs.getString("truck_type")),
                        rs.getString("license_number"),
                        rs.getString("model"),
                        rs.getFloat("net_weight"),
                        rs.getFloat("max_weight"),
                        rs.getBoolean("is_free")
                ));
            }
        }

        return trucks;
    }

    @Override
    public void delete(int truckId) throws SQLException {
        String sql = "DELETE FROM trucks WHERE truck_id = ?";

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, truckId);
            ps.executeUpdate();
        }
    }
}