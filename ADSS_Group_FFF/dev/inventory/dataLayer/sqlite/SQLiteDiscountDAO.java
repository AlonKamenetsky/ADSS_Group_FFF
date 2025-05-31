package inventory.dataLayer.sqlite;

import inventory.dataLayer.daos.DiscountDAO;
import inventory.dataLayer.dtos.DiscountDTO;
import inventory.dataLayer.mappers.DiscountMapper;
import inventory.domainLayer.Category;
import inventory.domainLayer.Discount;
import inventory.domainLayer.InventoryProduct;
import inventory.dataLayer.utils.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SQLite‐backed DiscountDAO.
 * Uses DiscountDTO ↔ Discount via DiscountMapper.
 */
public class SQLiteDiscountDAO implements DiscountDAO {

    private final Connection conn;
    private final SQLiteCategoryDAO categoryDAO;
    private final SQLiteInventoryProductDAO productDAO;

    public SQLiteDiscountDAO(SQLiteCategoryDAO categoryDAO,
                             SQLiteInventoryProductDAO productDAO) throws SQLException {
        this.conn = DatabaseManager.getInstance().getConnection();
        this.categoryDAO = categoryDAO;
        this.productDAO = productDAO;
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
            pstmt.setString(5,
                    (discount.getAppliesToCategory() != null)
                            ? discount.getAppliesToCategory().getName()
                            : null
            );
            pstmt.setObject(6,
                    (discount.getAppliesToItem() != null)
                            ? discount.getAppliesToItem().getId()
                            : null,
                    Types.INTEGER
            );
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save Discount ID=" + discount.getId(), e);
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
                double percent = rs.getDouble("percent");
                Date start = new Date(rs.getLong("startDate"));
                Date end = new Date(rs.getLong("endDate"));
                String categoryName = rs.getString("appliesToCategory");
                Integer itemId = (rs.getObject("appliesToItemId") != null)
                        ? rs.getInt("appliesToItemId")
                        : null;

                Category cat = null;
                if (categoryName != null) {
                    cat = categoryDAO.findByName(categoryName);
                }
                InventoryProduct item = null;
                if (itemId != null) {
                    item = productDAO.findById(itemId);
                }
                return new Discount(id, percent, start, end, cat, item);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find Discount by ID=" + id, e);
        }
    }

    @Override
    public List<Discount> findAll() {
        String sql = "SELECT id FROM Discount";
        List<Discount> out = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                Discount d = findById(id);
                if (d != null) {
                    out.add(d);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list all Discounts", e);
        }
        return out;
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM Discount WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete Discount ID=" + id, e);
        }
    }

    @Override
    public void update(Discount discount) {
        String sql = "UPDATE Discount SET "
                + "percent = ?, "
                + "startDate = ?, "
                + "endDate = ?, "
                + "appliesToCategory = ?, "
                + "appliesToItemId = ? "
                + "WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, discount.getPercent());
            pstmt.setLong(2, discount.getStartDate().getTime());
            pstmt.setLong(3, discount.getEndDate().getTime());
            pstmt.setString(4,
                    (discount.getAppliesToCategory() != null)
                            ? discount.getAppliesToCategory().getName()
                            : null
            );
            pstmt.setObject(5,
                    (discount.getAppliesToItem() != null)
                            ? discount.getAppliesToItem().getId()
                            : null,
                    Types.INTEGER
            );
            pstmt.setString(6, discount.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update Discount ID=" + discount.getId(), e);
        }
    }

    @Override
    public Discount findByItemId(int itemId) {
        final String sql = "SELECT * FROM Discount WHERE appliesToItemId = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRowToDiscount(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error looking up discount for item " + itemId, e);
        }
    }

    @Override
    public Discount findByCategoryName(String categoryName) {
        final String sql = "SELECT * FROM Discount WHERE appliesToCategory = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoryName);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRowToDiscount(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error looking up discount for category \"" + categoryName + "\"", e);
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Helper: Map current ResultSet row → Discount domain object
    // ─────────────────────────────────────────────────────────────────

    private Discount mapRowToDiscount(ResultSet rs) throws SQLException {
        String id         = rs.getString("id");
        double percent    = rs.getDouble("percent");
        Date startDate    = new Date(rs.getLong("startDate"));
        Date endDate      = new Date(rs.getLong("endDate"));
        String catName    = rs.getString("appliesToCategory");
        Integer itemIdRaw = rs.getObject("appliesToItemId") != null
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
