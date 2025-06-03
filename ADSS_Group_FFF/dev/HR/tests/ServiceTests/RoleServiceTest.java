
package HR.tests.ServiceTests;

import HR.DataAccess.RoleDAO;
import HR.Domain.Role;
import HR.DTO.RoleDTO;
import HR.Mapper.RoleMapper;

import HR.Service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoleServiceTest {

    @Mock private RoleDAO roleDAO;

    @InjectMocks private RoleService roleService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        roleService = new RoleService(roleDAO);
    }

    @Test
    public void testAddRole_validInput_callsInsert() {
        RoleDTO dto = new RoleDTO("Manager");
        Role role = new Role("Manager");

        try (MockedStatic<RoleMapper> mocked = mockStatic(RoleMapper.class)) {
            mocked.when(() -> RoleMapper.fromDTO(dto)).thenReturn(role);

            roleService.addRole(dto);

            verify(roleDAO).insert(role);
        }
    }

    @Test
    public void testAddRole_nullInput_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> roleService.addRole(null));
    }

    @Test
    public void testRemoveRole_validName_callsDelete() {
        roleService.removeRole("Driver");

        verify(roleDAO).delete("Driver");
    }

    @Test
    public void testRemoveRole_nullName_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> roleService.removeRole(null));
    }

    @Test
    public void testRemoveRole_emptyName_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> roleService.removeRole(""));
    }

    @Test
    public void testGetRoles_returnsDTOList() {
        List<Role> roles = List.of(new Role("Clerk"));
        RoleDTO dto = new RoleDTO("Clerk");

        when(roleDAO.findAll()).thenReturn(roles);

        try (MockedStatic<RoleMapper> mocked = mockStatic(RoleMapper.class)) {
            mocked.when(() -> RoleMapper.toDTO(roles.get(0))).thenReturn(dto);

            List<RoleDTO> result = roleService.getRoles();

            assertEquals(1, result.size());
            assertEquals("Clerk", result.get(0).getName());
        }
    }

    @Test
    public void testGetRoleByName_valid_returnsDTO() {
        Role role = new Role("Admin");
        RoleDTO dto = new RoleDTO("Admin");

        when(roleDAO.findByName("Admin")).thenReturn(role);

        try (MockedStatic<RoleMapper> mocked = mockStatic(RoleMapper.class)) {
            mocked.when(() -> RoleMapper.toDTO(role)).thenReturn(dto);

            RoleDTO result = roleService.getRoleByName("Admin");

            assertNotNull(result);
            assertEquals("Admin", result.getName());
        }
    }

    @Test
    public void testGetRoleByName_nullName_returnsNull() {
        RoleDTO result = roleService.getRoleByName(null);
        assertNull(result);
    }

    @Test
    public void testGetRoleByName_emptyName_returnsNull() {
        RoleDTO result = roleService.getRoleByName("");
        assertNull(result);
    }
}
