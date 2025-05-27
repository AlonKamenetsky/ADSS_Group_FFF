package HR.Domain;
import java.util.List;

public interface RoleRepo {
    List<Role> getRoles();
    Role getRoleByName(String name);
    void addRole(Role role);
    void removeRole(Role role);
}
