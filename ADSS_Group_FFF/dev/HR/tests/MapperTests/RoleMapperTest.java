package HR.tests.MapperTests;

import HR.Domain.Role;
import HR.DTO.RoleDTO;
import HR.Mapper.RoleMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoleMapperTest {

    @Test
    void testToDTO() {
        Role domainRole = new Role("Logistics");
        RoleDTO dto = RoleMapper.toDTO(domainRole);

        assertNotNull(dto);
        assertEquals("Logistics", dto.getName());
    }

    @Test
    void testToDTO_NullInput() {
        assertNull(RoleMapper.toDTO(null));
    }

    @Test
    void testFromDTO() {
        RoleDTO dto = new RoleDTO("Manager");
        Role domainRole = RoleMapper.fromDTO(dto);

        assertNotNull(domainRole);
        assertEquals("Manager", domainRole.getName());
    }

    @Test
    void testFromDTO_NullInput() {
        assertNull(RoleMapper.fromDTO(null));
    }
}
