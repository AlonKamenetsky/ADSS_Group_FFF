// File: UserDTO.java
package HR.DTO;

import java.util.List;

public class UserDTO {
    private String id;
    private String name;
    private List<RoleDTO> roles;   // list of RoleDTOs instead of domain Role
    // NOTE: we do NOT include password here.

    public UserDTO() { }

    public UserDTO(String id, String name, List<RoleDTO> roles) {
        this.id = id;
        this.name = name;
        this.roles = roles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }
}
