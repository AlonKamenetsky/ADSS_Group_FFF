package HR.tests.MapperTests;

import HR.Domain.User;
import HR.Domain.Role;
import HR.DTO.UserDTO;
import HR.DTO.RoleDTO;

import HR.Mapper.UserMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

    @Test
    public void testToDTO_withValidUser_convertsCorrectly() {
        Role role1 = new Role("Admin");
        Role role2 = new Role("User");
        User user = new User("U1", "Alice", "secret", List.of(role1, role2));

        UserDTO dto = UserMapper.toDTO(user);

        assertNotNull(dto);
        assertEquals("U1", dto.getId());
        assertEquals("Alice", dto.getName());
        List<RoleDTO> roles = dto.getRoles();
        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertTrue(roles.stream().anyMatch(r -> r.getName().equals("Admin")));
        assertTrue(roles.stream().anyMatch(r -> r.getName().equals("User")));
    }

    @Test
    public void testToDTO_withNullUser_returnsNull() {
        UserDTO dto = UserMapper.toDTO(null);
        assertNull(dto);
    }

    @Test
    public void testFromDTO_withValidDTO_convertsCorrectly() {
        RoleDTO rdto1 = new RoleDTO("Manager");
        RoleDTO rdto2 = new RoleDTO("Clerk");
        UserDTO dto = new UserDTO("U2", "Bob", List.of(rdto1, rdto2));

        User user = UserMapper.fromDTO(dto);

        assertNotNull(user);
        assertEquals("U2", user.getId());
        assertEquals("Bob", user.getName());
        assertNull(user.getPassword());
        List<Role> roles = user.getRoles();
        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertTrue(roles.stream().anyMatch(r -> r.getName().equals("Manager")));
        assertTrue(roles.stream().anyMatch(r -> r.getName().equals("Clerk")));
    }

    @Test
    public void testFromDTO_withNullDTO_returnsNull() {
        User user = UserMapper.fromDTO(null);
        assertNull(user);
    }
}
