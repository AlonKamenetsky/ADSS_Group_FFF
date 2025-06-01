package inventory.dataLayer.sqlite;

import inventory.dataLayer.daos.InventoryProductDAO;
import inventory.domainLayer.Category;
import inventory.domainLayer.InventoryProduct;
import inventory.domainLayer.ItemStatus;
import inventory.dataLayer.utils.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteInventoryProductDAO implements InventoryProductDAO {
    private final Connection conn;
    private final SQLiteCategoryDAO categoryDAO;

    public SQLiteInventoryProductDAO(SQLiteCategoryDAO categoryDAO) throws SQLException {
        this.conn = DatabaseManager.getInstance().getConnection();
        this.categoryDAO = categoryDAO;
        // Assume DatabaseManager already created:
        // CREATE TABLE IF NOT EXISTS InventoryProduct (
        //   id INTEGER PRIMARY KEY,
        //   name TEXT NOT NULL,
        //   manufacturer TEXT,
        //   shelfQuantity INTEGER NOT NULL,
        //   backroomQuantity INTEGER NOT NULL,
        //   minThreshold INTEGER NOT NULL,
        //   purchasePrice REAL NOT NULL,
        //   salePrice REAL NOT NULL,
        //   status TEXT NOT NULL,
        //   category_name TEXT,
        //   FOREIGN KEY(category_name) REFERENCES Category(name)
        // );
    }

    @Override
    public void save(InventoryProduct product) {
        String sql = "INSERT INTO InventoryProduct "
                + "(id, name, manufacturer, shelfQuantity, backroomQuantity, minThreshold, "
                + " purchasePrice, salePrice, status, category_name) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, product.getId());
            pstmt.setString(2, product.getName());
            pstmt.setString(3, product.getManufacturer());
            pstmt.setInt(4, product.getShelfQuantity());
            pstmt.setInt(5, product.getBackroomQuantity());
            pstmt.setInt(6, product.getMinThreshold());
            pstmt.setDouble(7, product.getPurchasePrice());
            pstmt.setDouble(8, product.getSalePrice());
            pstmt.setString(9, product.getStatus().name());
            pstmt.setString(
                    10,
                    (product.getCategory() != null)
                            ? product.getCategory().getName()
                            : null
            );
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to save InventoryProduct ID=" + product.getId(), e
            );
        }
    }

    @Override
    public InventoryProduct findById(int id) {
        String sql = "SELECT * FROM InventoryProduct WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapRowToInventoryProduct(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find InventoryProduct by ID=" + id, e
            );
        }
    }

    @Override
    public List<InventoryProduct> findAll() {
        String sql = "SELECT id FROM InventoryProduct";
        List<InventoryProduct> result = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                InventoryProduct ip = findById(id);
                if (ip != null) {
                    result.add(ip);
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve all InventoryProducts", e);
        }
    }

    @Override
    public void update(InventoryProduct product) {
        String sql = "UPDATE InventoryProduct SET "
                + " name = ?, "
                + " manufacturer = ?, "
                + " shelfQuantity = ?, "
                + " backroomQuantity = ?, "
                + " minThreshold = ?, "
                + " purchasePrice = ?, "
                + " salePrice = ?, "
                + " status = ?, "
                + " category_name = ? "
                + "WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getManufacturer());
            pstmt.setInt(3, product.getShelfQuantity());
            pstmt.setInt(4, product.getBackroomQuantity());
            pstmt.setInt(5, product.getMinThreshold());
            pstmt.setDouble(6, product.getPurchasePrice());
            pstmt.setDouble(7, product.getSalePrice());
            pstmt.setString(8, product.getStatus().name());
            pstmt.setString(
                    9,
                    (product.getCategory() != null)
                            ? product.getCategory().getName()
                            : null
            );
            pstmt.setInt(10, product.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to update InventoryProduct ID=" + product.getId(), e
            );
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM InventoryProduct WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to delete InventoryProduct ID=" + id, e
            );
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Private helper: build an InventoryProduct from the current ResultSet row
    // ─────────────────────────────────────────────────────────────────
    private InventoryProduct mapRowToInventoryProduct(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String manufacturer = rs.getString("manufacturer");
        int shelfQty = rs.getInt("shelfQuantity");
        int backroomQty = rs.getInt("backroomQuantity");
        int minThreshold = rs.getInt("minThreshold");
        double purchasePrice = rs.getDouble("purchasePrice");
        double salePrice = rs.getDouble("salePrice");
        ItemStatus status = ItemStatus.valueOf(rs.getString("status"));
        String categoryName = rs.getString("category_name");

        Category category = (categoryName != null)
                ? categoryDAO.findByName(categoryName)
                : null;

        return new InventoryProduct(
                id, name, manufacturer,
                shelfQty, backroomQty, minThreshold,
                purchasePrice, salePrice,
                status, category
        );
    }
}
