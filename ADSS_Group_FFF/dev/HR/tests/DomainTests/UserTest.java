package HR.tests.DomainTests;

import HR.Domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void constructor_and_getters() {
        Role r1 = new Role("HR");
        Role r2 = new Role("Cashier");
        User u = new User("u1", "Alice", "pwHash", List.of(r1, r2));

        assertEquals("u1", u.getId());
        assertEquals("Alice", u.getName());
        assertEquals("pwHash", u.getPassword());
        assertEquals(2, u.getRoles().size());
        assertTrue(u.getRoles().contains(r1));
        assertTrue(u.getRoles().contains(r2));
    }

    @Test
    public void setPassword_and_setName_modifyFields() {
        User u = new User("u2", "Bob", "initial", List.of(new Role("Cleaner")));
        u.setPassword("newHash");
        assertEquals("newHash", u.getPassword());

        u.setName("Bobby");
        assertEquals("Bobby", u.getName());
    }
}
