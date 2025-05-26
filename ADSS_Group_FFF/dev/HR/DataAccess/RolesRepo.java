package HR.DataAccess;

import HR.Domain.Role;
import HR.Presentation.PresentationUtils;

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
            PresentationUtils.typewriterPrint("Role added: " + role.getName(), 20);
        } else {
            PresentationUtils.typewriterPrint("Role already exists.",20);
        }
    }

    public void removeRole(Role role) {
        if (roles.contains(role)) {
            roles.remove(role);
            PresentationUtils.typewriterPrint("Role removed: " + role.getName(), 20);
        } else {
            PresentationUtils.typewriterPrint("Role not found.",20);
        }
    }


}
