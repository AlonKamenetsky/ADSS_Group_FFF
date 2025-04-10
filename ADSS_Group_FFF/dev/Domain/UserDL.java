package Domain;

import java.util.List;

public class UserDL {
    private String id;
    private String name;
    private List<RoleDL> roles;
    private String password;

    public UserDL(String id, String name, String password, List<RoleDL> roles) {
        this.id = id;
        this.name = name;
        this.roles = roles;
    }

    public String getId() { return id; }

    public String getName() { return name; }

    public List<RoleDL> getRoles() { return roles; }

    public Object getPassword() {
        return password;
    }

    public void setPassword(String number) {
        this.password = number;
    }
}
