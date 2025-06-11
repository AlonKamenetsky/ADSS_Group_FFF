
package HR.tests.ServiceTests;

import HR.DTO.RoleDTO;
import HR.Service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RoleServiceTest {

    private RoleService roleService;

    @BeforeEach
    public void setUp() {
        roleService = RoleService.getInstance();
    }

    @Test
    public void testAddAndGetRole() {
        roleService.addRole(new RoleDTO("TestRole"));
        RoleDTO role = roleService.getRoleByName("TestRole");
        assertNotNull(role);
        assertEquals("TestRole", role.getName());
    }

    @Test
    public void testGetRolesReturnsDTOs() {
        roleService.addRole(new RoleDTO("AnotherRole"));
        List<RoleDTO> roles = roleService.getRoles();
        assertFalse(roles.isEmpty());
        assertTrue(roles.stream().anyMatch(r -> r.getName().equals("AnotherRole")));
    }

    @Test
    public void testRemoveRole() {
        RoleDTO toRemove = new RoleDTO("TempRole");
        roleService.addRole(toRemove);
        roleService.removeRole(toRemove.getName());
        assertNull(roleService.getRoleByName(toRemove.getName()));
    }
}
