package Domain;

import java.util.List;

public class User {
    private String id;
    private String name;
    private List<Role> roles;
    private String password;

    public User(String id, String name, String password, List<Role> roles) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.roles = roles;
    }

    public String getId() { return id; }

    public String getName() { return name; }

    public List<Role> getRoles() { return roles; }

    public Object getPassword() {
        return password;
    }

    public void setPassword(String number) {
        this.password = number;
    }
}
