package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Domain.*;
import java.util.List;

class RolesRepoTest {

    private RolesRepo rolesRepo;

    @BeforeEach
    void setUp() {
        rolesRepo = RolesRepo.getInstance();
        // Clean up roles before each test
        rolesRepo.getRoles().clear();
    }

    @Test
    void testAddRole() {
        Role role = new Role("HR");
        rolesRepo.addRole(role);

        assertEquals(1, rolesRepo.getRoles().size());
        assertTrue(rolesRepo.getRoles().contains(role));
    }

    @Test
    void testGetRoleByNameExists() {
        Role role = new Role("Cashier");
        rolesRepo.addRole(role);

        Role foundRole = rolesRepo.getRoleByName("Cashier");
        assertNotNull(foundRole);
        assertEquals("Cashier", foundRole.getName());
    }

    @Test
    void testGetRoleByNameNotExists() {
        Role foundRole = rolesRepo.getRoleByName("Warehouse");
        assertNull(foundRole);
    }

    @Test
    void testAddMultipleRoles() {
        Role role1 = new Role("HR");
        Role role2 = new Role("Manager");
        Role role3 = new Role("Warehouse");

        rolesRepo.addRole(role1);
        rolesRepo.addRole(role2);
        rolesRepo.addRole(role3);

        List<Role> roles = rolesRepo.getRoles();

        assertEquals(3, roles.size());
        assertTrue(roles.contains(role1));
        assertTrue(roles.contains(role2));
        assertTrue(roles.contains(role3));
    }
}
