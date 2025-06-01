package HR.Service;

import HR.DataAccess.*;
import HR.Domain.*;
import HR.Presentation.PresentationUtils;
import Util.Database;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

public class RoleService {
    private static RoleService instance;
    private final RoleDAO roleDAO;

    private RoleService() {
        Connection conn = Database.getConnection();
        this.roleDAO = new RoleDAOImpl(conn);
    }

    public static RoleService getInstance() {
        if (instance == null) {
            instance = new RoleService();
        }
        return instance;
    }

    public void addRole(String name) {
        Role role = new Role(name);
        roleDAO.insert(role);
    }

    public void removeRole(Role role) {
        roleDAO.delete(role.getName());
    }

    public List<Role> getRoles() {
        return roleDAO.findAll();
    }

    public Role getRoleByName(String name) {
        return roleDAO.findByName(name);
    }
}
