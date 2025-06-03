package HR.tests.MapperTests;

import HR.Domain.Role;
import HR.DTO.RoleDTO;
import HR.Mapper.RoleMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoleMapperTest {

    @Test
    public void testToDTO_withValidRole_returnsCorrectDTO() {
        Role domain = new Role("Manager");
        RoleDTO dto = RoleMapper.toDTO(domain);

        assertNotNull(dto);
        assertEquals("Manager", dto.getName());
    }

    @Test
    public void testFromDTO_withValidDTO_returnsCorrectRole() {
        RoleDTO dto = new RoleDTO("Driver");
        Role role = RoleMapper.fromDTO(dto);

        assertNotNull(role);
        assertEquals("Driver", role.getName());
    }

    @Test
    public void testToDTO_withNull_returnsNull() {
        assertNull(RoleMapper.toDTO(null));
    }

    @Test
    public void testFromDTO_withNull_returnsNull() {
        assertNull(RoleMapper.fromDTO(null));
    }
}
