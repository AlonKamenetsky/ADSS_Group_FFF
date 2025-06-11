package HR.DataAccess;

import HR.DataAccess.RoleDAO;
import HR.Domain.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAOImpl implements RoleDAO {

    private final Connection connection;

    public RoleDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Role role) {
        String sql = "INSERT INTO roles (name) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, role.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Insert role failed", e);
        }
    }

    @Override
    public void delete(String roleName) {
        String sql = "DELETE FROM roles WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, roleName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Delete role failed", e);
        }
    }

    @Override
    public List<Role> selectAll() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM roles";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                roles.add(new Role(rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Select all roles failed", e);
        }
        return roles;
    }

    @Override
    public List<Role> findAll() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM roles";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                roles.add(new Role(rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Select all roles failed", e);
        }
        return roles;
    }

    @Override
    public Role findByName(String name) {
        String sql = "SELECT * FROM roles WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return new Role(rs.getString("name"));
        } catch (SQLException e) {
            throw new RuntimeException("Select role failed", e);
        }
        return null;
    }

}
