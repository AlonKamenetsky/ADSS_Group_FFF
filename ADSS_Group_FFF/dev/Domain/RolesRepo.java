package Domain;

import java.util.ArrayList;
import java.util.List;

public class RolesRepo {
    private static RolesRepo instance = null;
    private List<RoleDL> roles;

    private RolesRepo() {
        roles = new ArrayList<>();
        roles.add(new RoleDL("HR"));
        roles.add(new RoleDL("Cashier"));
        roles.add(new RoleDL("Warehouse"));
        roles.add(new RoleDL("Driver"));
        roles.add(new RoleDL("ShiftManager"));
        roles.add(new RoleDL("Cleanerr"));
    }

    public static RolesRepo getInstance() {
        if (instance == null) {
            instance = new RolesRepo();
        }
        return instance;
    }

    public List<RoleDL> getRoles() {
        return roles;
    }

    public RoleDL getRoleByName(String name) {
        return roles.stream()
                .filter(role -> role.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    public void addRole(RoleDL role) {
        if (getRoleByName(role.getName()) == null) {
            roles.add(role);
        } else {
            System.out.println("Role already exists.");
        }
    }


}
