package HR.Service;

import HR.DataAccess.RolesRepo;
import HR.Domain.Role;

import java.util.List;

public class RoleService {
    private static RoleService instance;
    private final RolesRepo repo;

    private RoleService() {
        repo = RolesRepo.getInstance();
    }

    public static RoleService getInstance() {
        if (instance == null) {
            instance = new RoleService();
        }
        return instance;
    }
    public void addRole(String name) {
        Role role = new Role(name);
        repo.addRole(role);
    }


    public void removeRole(Role role) {
        repo.removeRole(role);
    }

    public List<Role> getRoles() {
        return repo.getRoles();
    }

    public Role getRoleByName(String name) {
        return repo.getRoleByName(name);
    }
}
