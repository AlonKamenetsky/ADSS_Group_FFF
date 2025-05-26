package HR.Domain;

import java.util.Objects;

public class Role {

    private final String Name;

    public Role(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public Boolean equals(Role role) {
        return this.Name.equals(role.getName());
    }

    public String toString() {
        return Name;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Role role = (Role) obj;
        return Objects.equals(Name, role.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(Name);
    }

}
