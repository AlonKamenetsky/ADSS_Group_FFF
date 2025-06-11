// File: RoleDTO.java
package HR.DTO;

public class RoleDTO {
    private String name;

    public RoleDTO() { }

    public RoleDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(String name) {
        if (this.name == null) {
            return name == null;
        }
        return this.name.toLowerCase().equals(name == null ? null : name.toLowerCase());
    }
}
