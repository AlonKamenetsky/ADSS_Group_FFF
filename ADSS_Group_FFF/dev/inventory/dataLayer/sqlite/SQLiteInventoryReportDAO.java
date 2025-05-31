package inventory.dataLayer.sqlite;

import inventory.dataLayer.daos.InventoryReportDAO;
import inventory.domainLayer.InventoryProduct;
import inventory.domainLayer.InventoryReport;
import inventory.dataLayer.utils.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SQLite‐backed InventoryReportDAO.
 * We store each report’s header in InventoryReport (id, dateGenerated),
 * and store each (report_id, product_id) pair in InventoryReportItems.
 */
public class SQLiteInventoryReportDAO implements InventoryReportDAO {

    private final Connection conn;
    private final SQLiteInventoryProductDAO productDAO; // to resolve InventoryProduct from ID

    public SQLiteInventoryReportDAO(SQLiteInventoryProductDAO productDAO) throws SQLException {
        this.conn = DatabaseManager.getInstance().getConnection();
        this.productDAO = productDAO;
    }

    @Override
    public void save(InventoryReport report) {
        // 1) Insert into InventoryReport table
        String headerSql = "INSERT INTO InventoryReport (id, dateGenerated) VALUES (?, ?)";
        try (PreparedStatement headerStmt = conn.prepareStatement(headerSql)) {
            headerStmt.setString(1, report.getId());
            headerStmt.setLong(2, report.getDateGenerated().getTime());
            headerStmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save InventoryReport header ID=" + report.getId(), e);
        }

        // 2) Insert each (reportId, productId) into InventoryReportItems
        String itemsSql = "INSERT INTO InventoryReportItems (report_id, product_id) VALUES (?, ?)";
        try (PreparedStatement itemsStmt = conn.prepareStatement(itemsSql)) {
            for (InventoryProduct p : report.getItems()) {
                itemsStmt.setString(1, report.getId());
                itemsStmt.setInt(2, p.getId());
                itemsStmt.addBatch();
            }
            itemsStmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save InventoryReport items ID=" + report.getId(), e);
        }
    }

    @Override
    public InventoryReport findById(String id) {
        // 1) Fetch header
        String headerSql = "SELECT dateGenerated FROM InventoryReport WHERE id = ?";
        Date dateGen = null;
        try (PreparedStatement headerStmt = conn.prepareStatement(headerSql)) {
            headerStmt.setString(1, id);
            try (ResultSet rs = headerStmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                dateGen = new Date(rs.getLong("dateGenerated"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read InventoryReport header ID=" + id, e);
        }

        // 2) Fetch item IDs
        List<InventoryProduct> products = new ArrayList<>();
        String itemsSql = "SELECT product_id FROM InventoryReportItems WHERE report_id = ?";
        try (PreparedStatement itemsStmt = conn.prepareStatement(itemsSql)) {
            itemsStmt.setString(1, id);
            try (ResultSet rs = itemsStmt.executeQuery()) {
                while (rs.next()) {
                    int pid = rs.getInt("product_id");
                    InventoryProduct p = productDAO.findById(pid);
                    if (p != null) {
                        products.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read InventoryReport items ID=" + id, e);
        }

        return new InventoryReport(id, dateGen, products);
    }

    @Override
    public List<InventoryReport> findAll() {
        // 1) Get all report IDs
        List<String> allIds = new ArrayList<>();
        String sql = "SELECT id FROM InventoryReport";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                allIds.add(rs.getString("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list all InventoryReport IDs", e);
        }

        // 2) Call findById on each ID
        List<InventoryReport> out = new ArrayList<>();
        for (String rid : allIds) {
            InventoryReport rep = findById(rid);
            if (rep != null) {
                out.add(rep);
            }
        }
        return out;
    }

    @Override
    public void delete(String id) {
        // 1) Delete from InventoryReportItems
        String deleteItems = "DELETE FROM InventoryReportItems WHERE report_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteItems)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete report items for ID=" + id, e);
        }

        // 2) Delete from InventoryReport
        String deleteHeader = "DELETE FROM InventoryReport WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteHeader)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete InventoryReport header ID=" + id, e);
        }
    }

    @Override
    public void update(InventoryReport report) {
        // For simplicity, we delete the old join rows and re‐insert:
        delete(report.getId());
        save(report);
    }
}
