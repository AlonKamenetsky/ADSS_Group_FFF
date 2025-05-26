package Domain;

import java.util.ArrayList;
import java.util.List;

public class RolesRepo {
    private static RolesRepo instance = null;
    private List<Role> roles;

    private RolesRepo() {
        roles = new ArrayList<>();
    }

    public static RolesRepo getInstance() {
        if (instance == null) {
            instance = new RolesRepo();
        }
        return instance;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public Role getRoleByName(String name) {
        return roles.stream()
                .filter(role -> role.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    public void addRole(Role role) {
        if (getRoleByName(role.getName()) == null) {
            roles.add(role);
        } else {
            System.out.println("Role already exists.");
        }
    }

    public void removeRole(Role role) {
        if (roles.contains(role)) {
            roles.remove(role);
        } else {
            System.out.println("Role not found.");
        }
    }


}
