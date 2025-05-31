package inventory.dataLayer.sqlite;

import inventory.dataLayer.daos.CategoryDAO;
import inventory.dataLayer.dtos.CategoryDTO;
import inventory.dataLayer.mappers.CategoryMapper;
import inventory.domainLayer.Category;
import inventory.dataLayer.utils.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQLite‐backed implementation of CategoryDAO.
 * Uses CategoryDTO + CategoryMapper to translate between domain and SQL.
 */
public class SQLiteCategoryDAO implements CategoryDAO {

    private final Connection conn;

    public SQLiteCategoryDAO() throws SQLException {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    @Override
    public void save(Category category) {
        String sql = "INSERT INTO Category(name, parent_name) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category.getName());
            pstmt.setString(2, (category.getParentCategory() != null) ? category.getParentCategory().getName() : null);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save Category: " + category.getName(), e);
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
                // We’ll need to fetch parent (if exists) before constructing children lists
                Category parent = null;
                if (parentName != null) {
                    parent = findByName(parentName); // recursion OK since table is small
                }
                Category cat = new Category(rs.getString("name"), parent);
                // Now fetch subcategories (if any)
                String subSql = "SELECT name FROM Category WHERE parent_name = ?";
                try (PreparedStatement subStmt = conn.prepareStatement(subSql)) {
                    subStmt.setString(1, name);
                    try (ResultSet subRs = subStmt.executeQuery()) {
                        while (subRs.next()) {
                            Category sub = findByName(subRs.getString("name"));
                            if (sub != null) {
                                cat.addSubCategory(sub);
                            }
                        }
                    }
                }
                return cat;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find Category by name=" + name, e);
        }
    }

    @Override
    public List<Category> findAll() {
        String sql = "SELECT name, parent_name FROM Category";
        List<CategoryDTO> dtoList = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                CategoryDTO dto = new CategoryDTO();
                dto.setName(rs.getString("name"));
                dto.setParentCategoryName(rs.getString("parent_name"));
                // subCategoryNames will be filled later
                dtoList.add(dto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list all categories", e);
        }

        // We need two‐pass to reconstruct parent/sub relationships:
        Map<String, Category> lookup = new HashMap<>();
        // 1) Create bare Category instances (without subcategories filled yet)
        for (CategoryDTO dto : dtoList) {
            Category parent = (dto.getParentCategoryName() != null)
                    ? lookup.get(dto.getParentCategoryName())
                    : null;
            Category c = new Category(dto.getName(), parent);
            lookup.put(dto.getName(), c);
        }
        // 2) Now populate subcategories
        for (CategoryDTO dto : dtoList) {
            Category c = lookup.get(dto.getName());
            if (dto.getSubCategoryNames() != null) {
                for (String subName : dto.getSubCategoryNames()) {
                    Category subCat = lookup.get(subName);
                    if (subCat != null) {
                        c.addSubCategory(subCat);
                    }
                }
            }
        }

        return new ArrayList<>(lookup.values());
    }

    @Override
    public void delete(String name) {
        String sql = "DELETE FROM Category WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete Category '" + name + "'", e);
        }
    }

    @Override
    public void update(Category category) {
        // Changing name is tricky because 'name' is PK. We assume name cannot change; only parent can change.
        String sql = "UPDATE Category SET parent_name = ? WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, (category.getParentCategory() != null) ? category.getParentCategory().getName() : null);
            pstmt.setString(2, category.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update Category: " + category.getName(), e);
        }
    }
}
