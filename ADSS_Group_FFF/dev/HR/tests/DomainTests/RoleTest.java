package HR.tests.DomainTests;

import HR.Domain.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoleTest {

    @Test
    public void constructor_and_getName() {
        Role r = new Role("Manager");
        assertEquals("Manager", r.getName());
    }

    @Test
    public void equalsAndHashCode() {
        Role r1 = new Role("Cashier");
        Role r2 = new Role("Cashier");
        Role r3 = new Role("Cleaner");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
        assertNotEquals(r1.hashCode(), r3.hashCode());
    }

    @Test
    public void toString_returnsName() {
        Role r = new Role("HR");
        assertEquals("HR", r.toString());
    }
}
