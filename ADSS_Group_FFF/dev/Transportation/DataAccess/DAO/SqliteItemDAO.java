package Transportation.DataAccess.DAO;

import Transportation.DTO.ItemDTO;
import Util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteItemDAO implements ItemDAO {

    @Override
    public List<ItemDTO> findAll() throws SQLException {
        String sql = "SELECT item_id, item_name, weight FROM items ORDER BY item_id";
        try (Statement st = Database.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            List<ItemDTO> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new ItemDTO(rs.getInt("item_id"), rs.getString("item_name"), rs.getFloat("weight")));
            }
            return list;
        }
    }

    @Override
    public Optional<ItemDTO> findById(int id) throws SQLException {
        String sql = "SELECT item_id, item_name, weight FROM items WHERE item_id = ?";

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(new ItemDTO(
                        rs.getInt("item_id"),
                        rs.getString("item_name"),
                        rs.getFloat("weight")
                ))
                        : Optional.empty();
            }
        }
    }

    @Override
    public Optional<ItemDTO> findByName(String name) throws SQLException {
        String sql = "SELECT item_id, item_name, weight FROM items WHERE item_name = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(new ItemDTO(rs.getInt("item_id"), rs.getString("item_name"), rs.getFloat("weight")))
                        : Optional.empty();
            }
        }
    }

    @Override
    public ItemDTO insert(ItemDTO item) throws SQLException {
        if (item.itemId() == null) {
            String sql = "INSERT INTO items(item_name, weight) VALUES(?, ?)";
            try (PreparedStatement ps = Database.getConnection()
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, item.itemName());
                ps.setFloat(2, item.itemWeight());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    return new ItemDTO(keys.getInt(1), item.itemName(), item.itemWeight());
                }
            }
        } else {
            String sql = "UPDATE items SET item_name = ?, weight = ? WHERE item_id = ?";
            try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
                ps.setString(1, item.itemName());
                ps.setFloat(2, item.itemWeight());
                ps.setInt(3, item.itemId());
                ps.executeUpdate();
                return item;
            }
        }
    }

    @Override
    public void delete(ItemDTO item) throws SQLException {
        String sql = "DELETE FROM items WHERE item_id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, item.itemId());
            ps.executeUpdate();
        }
    }
}