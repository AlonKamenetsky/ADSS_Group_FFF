package HR.tests.MapperTests;

import HR.Domain.Role;
import HR.Domain.User;
import HR.DTO.RoleDTO;
import HR.DTO.UserDTO;
import HR.Mapper.UserMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

    @Test
    public void toDTO_nullReturnsNull() {
        assertNull(UserMapper.toDTO(null));
    }

    @Test
    public void fromDTO_nullReturnsNull() {
        assertNull(UserMapper.fromDTO(null));
    }

    @Test
    public void toDTO_and_fromDTO_roundTrip() {
        // Arrange: create a User domain object with two roles
        Role r1 = new Role("HR");
        Role r2 = new Role("Cashier");
        User original = new User("u123", "Alice", "secretHash", List.of(r1, r2));

        // Act: map to DTO, then back to domain
        UserDTO dto = UserMapper.toDTO(original);
        User reconstructed = UserMapper.fromDTO(dto);

        // Assert DTO fields
        assertNotNull(dto);
        assertEquals("u123", dto.getId());
        assertEquals("Alice", dto.getName());

        List<RoleDTO> roleDtos = dto.getRoles();
        assertEquals(2, roleDtos.size());
        assertTrue(roleDtos.stream().anyMatch(rd -> rd.getName().equals("HR")));
        assertTrue(roleDtos.stream().anyMatch(rd -> rd.getName().equals("Cashier")));

        // Reconstructed domain: password should be null, roles should match names
        assertNotNull(reconstructed);
        assertEquals("u123", reconstructed.getId());
        assertEquals("Alice", reconstructed.getName());
        assertNull(reconstructed.getPassword());

        List<Role> recRoles = reconstructed.getRoles();
        assertEquals(2, recRoles.size());
        assertTrue(recRoles.stream().anyMatch(r -> r.getName().equals("HR")));
        assertTrue(recRoles.stream().anyMatch(r -> r.getName().equals("Cashier")));
    }
}
