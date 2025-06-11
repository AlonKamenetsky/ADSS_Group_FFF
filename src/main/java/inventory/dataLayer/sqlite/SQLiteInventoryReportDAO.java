package inventory.dataLayer.sqlite;

import inventory.dataLayer.daos.InventoryReportDAO;
import inventory.domainLayer.InventoryProduct;
import inventory.domainLayer.InventoryReport;
import inventory.dataLayer.utils.DatabaseManager;


import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SQLiteInventoryReportDAO implements InventoryReportDAO {
    private final Connection conn;
    private final SQLiteInventoryProductDAO productDAO;

    public SQLiteInventoryReportDAO(SQLiteInventoryProductDAO productDAO) throws SQLException {
        this.conn = DatabaseManager.getInstance().getConnection();
        this.productDAO = productDAO;
        // Assume DatabaseManager already created:
        // CREATE TABLE IF NOT EXISTS InventoryReport (
        //   id TEXT PRIMARY KEY,
        //   dateGenerated INTEGER NOT NULL
        // );
        // CREATE TABLE IF NOT EXISTS InventoryReportItems (
        //   report_id TEXT NOT NULL,
        //   product_id INTEGER NOT NULL,
        //   FOREIGN KEY(report_id) REFERENCES InventoryReport(id),
        //   FOREIGN KEY(product_id) REFERENCES InventoryProduct(id)
        // );
    }

    @Override
    public void save(InventoryReport report) {
        String insertHeader = "INSERT INTO InventoryReport (id, dateGenerated) VALUES (?, ?)";
        String insertItem   = "INSERT INTO InventoryReportItems (report_id, product_id) VALUES (?, ?)";
        try (PreparedStatement headerStmt = conn.prepareStatement(insertHeader)) {
            headerStmt.setString(1, report.getId());
            headerStmt.setLong(2, report.getDateGenerated().getTime());
            headerStmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save InventoryReport header ID=" + report.getId(), e);
        }

        try (PreparedStatement itemStmt = conn.prepareStatement(insertItem)) {
            for (InventoryProduct p : report.getItems()) {
                itemStmt.setString(1, report.getId());
                itemStmt.setInt(2, p.getId());
                itemStmt.addBatch();
            }
            itemStmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save InventoryReport items for report ID=" + report.getId(), e);
        }
    }

    @Override
    public InventoryReport findById(String id) {
        // 1) First fetch the header row
        String fetchHeader = "SELECT dateGenerated FROM InventoryReport WHERE id = ?";
        Date dateGenerated;
        try (PreparedStatement headerStmt = conn.prepareStatement(fetchHeader)) {
            headerStmt.setString(1, id);
            try (ResultSet rs = headerStmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                dateGenerated = new Date(rs.getLong("dateGenerated"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find InventoryReport header ID=" + id, e);
        }

        // 2) Fetch all product IDs in this report
        String fetchItems = "SELECT product_id FROM InventoryReportItems WHERE report_id = ?";
        List<InventoryProduct> items = new ArrayList<>();
        try (PreparedStatement itemStmt = conn.prepareStatement(fetchItems)) {
            itemStmt.setString(1, id);
            try (ResultSet rs = itemStmt.executeQuery()) {
                while (rs.next()) {
                    int pid = rs.getInt("product_id");
                    InventoryProduct p = productDAO.findById(pid);
                    if (p != null) {
                        items.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find InventoryReport items ID=" + id, e);
        }

        return new InventoryReport(id, dateGenerated, items);
    }

    @Override
    public List<InventoryReport> findAll() {
        // 1) Get all report IDs
        String sql = "SELECT id FROM InventoryReport";
        List<InventoryReport> result = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String rid = rs.getString("id");
                InventoryReport rpt = findById(rid);
                if (rpt != null) {
                    result.add(rpt);
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve all InventoryReports", e);
        }
    }

    @Override
    public void update(InventoryReport report) {
        // Simplest strategy: delete old join rows, then re-insert everything
        delete(report.getId());
        save(report);
    }

    @Override
    public void delete(String id) {
        // 1) Delete from InventoryReportItems
        String deleteItems = "DELETE FROM InventoryReportItems WHERE report_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteItems)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete InventoryReport items for ID=" + id, e);
        }

        // 2) Delete from InventoryReport header
        String deleteHeader = "DELETE FROM InventoryReport WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteHeader)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete InventoryReport header ID=" + id, e);
        }
    }
}
