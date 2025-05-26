package tests.DomainTests;

import Domain.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void testRoleName() {
        Role role = new Role("Cashier");
        assertEquals("Cashier", role.getName());
    }

    @Test
    void testRoleEquality() {
        Role role1 = new Role("HR");
        Role role2 = new Role("HR");
        Role role3 = new Role("Warehouse");

        assertEquals(role1.getName(), role2.getName());
        assertNotEquals(role1.getName(), role3.getName());
    }

}
