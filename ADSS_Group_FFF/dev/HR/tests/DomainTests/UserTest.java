package HR.tests.DomainTests;

import HR.Domain.Role;
import HR.Domain.User;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testUserGettersAndSetters() {
        Role role = new Role("Cashier");
        User user = new User("1", "Dana", "pass", List.of(role));
        assertEquals("1", user.getId());
        assertEquals("Dana", user.getName());
        assertEquals("pass", user.getPassword());
        assertEquals(1, user.getRoles().size());
        assertEquals("Cashier", user.getRoles().get(0).getName());

        user.setPassword("newpass");
        assertEquals("newpass", user.getPassword());
    }
}
