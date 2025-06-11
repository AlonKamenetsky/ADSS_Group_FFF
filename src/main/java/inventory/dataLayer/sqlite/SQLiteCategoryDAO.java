package inventory.dataLayer.sqlite;

import inventory.dataLayer.daos.CategoryDAO;
import inventory.domainLayer.Category;
import inventory.dataLayer.utils.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteCategoryDAO implements CategoryDAO {
    private final Connection conn;

    public SQLiteCategoryDAO() throws SQLException {
        this.conn = DatabaseManager.getInstance().getConnection();
        // We assume DatabaseManager already created the Category table:
        // CREATE TABLE IF NOT EXISTS Category (
        //   name TEXT PRIMARY KEY,
        //   parent_name TEXT,
        //   FOREIGN KEY(parent_name) REFERENCES Category(name)
        // );
    }

    @Override
    public void save(Category category) {
        String sql = "INSERT INTO Category (name, parent_name) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category.getName());
            pstmt.setString(
                    2,
                    (category.getParentCategory() != null)
                            ? category.getParentCategory().getName()
                            : null
            );
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to save Category: " + category.getName(), e
            );
        }
    }

    @Override
    public Category findByName(String name) {
        String sql = "SELECT name, parent_name FROM Category WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                String parentName = rs.getString("parent_name");
                Category parent = (parentName != null) ? findByName(parentName) : null;
                return new Category(rs.getString("name"), parent);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find Category by name: " + name, e);
        }
    }

    @Override
    public List<Category> findAll() {
        String sql = "SELECT name, parent_name FROM Category";
        List<Category> result = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            // First pass: build a map of name → parent_name (as String)
            List<String> names = new ArrayList<>();
            List<String> parentNames = new ArrayList<>();
            while (rs.next()) {
                names.add(rs.getString("name"));
                parentNames.add(rs.getString("parent_name"));
            }
            // Convert to domain objects: need two passes to set parents
            // 1) Create all Category instances with null parent
            for (String nm : names) {
                result.add(new Category(nm, null));
            }
            // 2) Wire up parent references
            for (int i = 0; i < names.size(); i++) {
                String pname = parentNames.get(i);
                if (pname != null) {
                    Category child = result.get(i);
                    // find the parent in 'result'
                    for (Category c : result) {
                        if (c.getName().equals(pname)) {
                            child.setParentCategory(c);
                            break;
                        }
                    }
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve all Categories", e);
        }
    }

    @Override
    public void update(Category category) {
        // Only parent_name can change (name is PK, assumed immutable)
        String sql = "UPDATE Category SET parent_name = ? WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(
                    1,
                    (category.getParentCategory() != null)
                            ? category.getParentCategory().getName()
                            : null
            );
            pstmt.setString(2, category.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to update Category: " + category.getName(), e
            );
        }
    }

    @Override
    public void delete(String name) {
        String sql = "DELETE FROM Category WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to delete Category: " + name, e
            );
        }
    }
}
