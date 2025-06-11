package HR.DataAccess;

import HR.Domain.Role;
import java.util.List;

public interface RoleDAO {
    void insert(Role role);
    void delete(String roleName);
    List<Role> selectAll();
    List<Role> findAll();
    Role findByName(String name);
}
