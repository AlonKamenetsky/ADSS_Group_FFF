package Domain;

public class RoleDL {

    private final String Name;

    public RoleDL(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public Boolean equals(RoleDL role) {
        return this.Name.equals(role.getName());
    }

}
