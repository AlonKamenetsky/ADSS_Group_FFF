package inventory.dataLayer.sqlite;

import inventory.dataLayer.daos.DiscountDAO;
import inventory.domainLayer.Category;
import inventory.domainLayer.Discount;
import inventory.domainLayer.InventoryProduct;
import inventory.dataLayer.utils.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class SQLiteDiscountDAO implements DiscountDAO {
    private final Connection conn;
    private final SQLiteCategoryDAO categoryDAO;
    private final SQLiteInventoryProductDAO productDAO;

    public SQLiteDiscountDAO(
            SQLiteCategoryDAO categoryDAO,
            SQLiteInventoryProductDAO productDAO
    ) throws SQLException {
        this.conn = DatabaseManager.getInstance().getConnection();
        this.categoryDAO = categoryDAO;
        this.productDAO = productDAO;
        // Assume DatabaseManager already created:
        // CREATE TABLE IF NOT EXISTS Discount (
        //   id TEXT PRIMARY KEY,
        //   percent REAL NOT NULL,
        //   startDate INTEGER NOT NULL,   -- stored as epoch millis
        //   endDate INTEGER NOT NULL,
        //   appliesToCategory TEXT,
        //   appliesToItemId INTEGER,
        //   FOREIGN KEY(appliesToCategory) REFERENCES Category(name),
        //   FOREIGN KEY(appliesToItemId) REFERENCES InventoryProduct(id)
        // );
    }

    @Override
    public void save(Discount discount) {
        String sql = "INSERT INTO Discount "
                + "(id, percent, startDate, endDate, appliesToCategory, appliesToItemId) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, discount.getId());
            pstmt.setDouble(2, discount.getPercent());
            pstmt.setLong(3, discount.getStartDate().getTime());
            pstmt.setLong(4, discount.getEndDate().getTime());
            pstmt.setString(
                    5,
                    (discount.getAppliesToCategory() != null)
                            ? discount.getAppliesToCategory().getName()
                            : null
            );
            pstmt.setObject(
                    6,
                    (discount.getAppliesToItem() != null)
                            ? discount.getAppliesToItem().getId()
                            : null,
                    Types.INTEGER
            );
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to save Discount ID=" + discount.getId(), e
            );
        }
    }

    @Override
    public Discount findById(String id) {
        String sql = "SELECT * FROM Discount WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapRowToDiscount(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find Discount by ID=" + id, e
            );
        }
    }

    @Override
    public List<Discount> findAll() {
        String sql = "SELECT id FROM Discount";
        List<Discount> result = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                Discount d = findById(id);
                if (d != null) {
                    result.add(d);
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve all Discounts", e);
        }
    }

    @Override
    public void update(Discount discount) {
        String sql = "UPDATE Discount SET "
                + " percent = ?, "
                + " startDate = ?, "
                + " endDate = ?, "
                + " appliesToCategory = ?, "
                + " appliesToItemId = ? "
                + "WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, discount.getPercent());
            pstmt.setLong(2, discount.getStartDate().getTime());
            pstmt.setLong(3, discount.getEndDate().getTime());
            pstmt.setString(
                    4,
                    (discount.getAppliesToCategory() != null)
                            ? discount.getAppliesToCategory().getName()
                            : null
            );
            pstmt.setObject(
                    5,
                    (discount.getAppliesToItem() != null)
                            ? discount.getAppliesToItem().getId()
                            : null,
                    Types.INTEGER
            );
            pstmt.setString(6, discount.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to update Discount ID=" + discount.getId(), e
            );
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM Discount WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to delete Discount ID=" + id, e
            );
        }
    }

    @Override
    public Discount findByItemId(int itemId) {
        String sql = "SELECT * FROM Discount WHERE appliesToItemId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapRowToDiscount(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error finding Discount for item ID=" + itemId, e
            );
        }
    }

    @Override
    public Discount findByCategoryName(String categoryName) {
        String sql = "SELECT * FROM Discount WHERE appliesToCategory = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categoryName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapRowToDiscount(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error finding Discount for category='" + categoryName + "'", e
            );
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Private helper to build a Discount from the current ResultSet row
    // ─────────────────────────────────────────────────────────────────
    private Discount mapRowToDiscount(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        double percent = rs.getDouble("percent");
        Date startDate = new Date(rs.getLong("startDate"));
        Date endDate = new Date(rs.getLong("endDate"));

        String catName = rs.getString("appliesToCategory");
        Integer itemIdRaw = (rs.getObject("appliesToItemId") != null)
                ? rs.getInt("appliesToItemId")
                : null;

        Category category = (catName != null)
                ? categoryDAO.findByName(catName)
                : null;

        InventoryProduct item = (itemIdRaw != null)
                ? productDAO.findById(itemIdRaw)
                : null;

        return new Discount(id, percent, startDate, endDate, category, item);
    }
}
