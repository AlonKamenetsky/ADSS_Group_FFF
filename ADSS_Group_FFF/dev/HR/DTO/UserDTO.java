package HR.DTO;

import java.util.List;
import java.util.Objects;

public class UserDTO {
    private String id;
    private String name;
    private List<RoleDTO> roles;   // list of RoleDTOs instead of domain Role

    public UserDTO() { }

    /**
     * @param id    Unique user ID
     * @param name  User’s display name
     * @param roles List of RoleDTOs for this user
     */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(id, userDTO.id) &&
                Objects.equals(name, userDTO.name) &&
                Objects.equals(roles, userDTO.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, roles);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", roles=" + roles +
                '}';
    }
}
