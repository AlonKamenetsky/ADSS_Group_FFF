package Domain;

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

}
