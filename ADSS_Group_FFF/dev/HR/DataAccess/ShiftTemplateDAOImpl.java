package HR.DataAccess;

import HR.Domain.ShiftTemplate;
import HR.Domain.Shift;
import HR.Domain.Role;
import Util.Database;

import java.sql.*;
import java.time.DayOfWeek;
import java.util.*;

public class ShiftTemplateDAOImpl implements ShiftTemplateDAO {

    private final Connection conn;

    public ShiftTemplateDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(ShiftTemplate template) {
        String insertTemplateSQL = "INSERT INTO shift_templates (day_of_week, shift_time) VALUES (?, ?)";
        String insertRoleSQL = "INSERT INTO shift_template_roles (template_id, role_name, count) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(insertTemplateSQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, template.getDay().name());
            stmt.setString(2, template.getTime().name());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int templateId = generatedKeys.getInt(1);

                    try (PreparedStatement roleStmt = conn.prepareStatement(insertRoleSQL)) {
                        for (Map.Entry<Role, Integer> entry : template.getDefaultCounts().entrySet()) {
                            roleStmt.setInt(1, templateId);
                            roleStmt.setString(2, entry.getKey().getName());
                            roleStmt.setInt(3, entry.getValue());
                            roleStmt.addBatch();
                        }
                        roleStmt.executeBatch();
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Insert shift template failed", e);
        }
    }

    @Override
    public List<ShiftTemplate> selectAll() {
        List<ShiftTemplate> templates = new ArrayList<>();
        String selectTemplateSQL = "SELECT * FROM shift_templates";
        String selectRolesSQL = "SELECT * FROM shift_template_roles WHERE template_id = ?";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectTemplateSQL)) {

            while (rs.next()) {
                int templateId = rs.getInt("id");
                DayOfWeek day = DayOfWeek.valueOf(rs.getString("day_of_week"));
                Shift.ShiftTime time = Shift.ShiftTime.valueOf(rs.getString("shift_time"));
                ShiftTemplate template = new ShiftTemplate(day, time);

                try (PreparedStatement roleStmt = conn.prepareStatement(selectRolesSQL)) {
                    roleStmt.setInt(1, templateId);
                    try (ResultSet roleRs = roleStmt.executeQuery()) {
                        while (roleRs.next()) {
                            String roleName = roleRs.getString("role_name");
                            int count = roleRs.getInt("count");
                            template.setDefaultCount(new Role(roleName), count);
                        }
                    }
                }

                templates.add(template);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Select all shift templates failed", e);
        }
        return templates;
    }
}
