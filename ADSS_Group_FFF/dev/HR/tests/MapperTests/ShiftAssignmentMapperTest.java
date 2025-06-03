package HR.tests.MapperTests;

import HR.Domain.Role;
import HR.Domain.ShiftAssignment;
import HR.DTO.ShiftAssignmentDTO;
import HR.Mapper.ShiftAssignmentMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftAssignmentMapperTest {

    @Test
    public void testToDTO_withValidDomain_convertsCorrectly() {
        // Given a domain ShiftAssignment
        Role role = new Role("Driver");
        ShiftAssignment sa = new ShiftAssignment("E1", "S1", role);

        // When mapping to DTO
        ShiftAssignmentDTO dto = ShiftAssignmentMapper.toDTO(sa);

        // Then fields should match
        assertNotNull(dto);
        assertEquals("E1", dto.getEmployeeId());
        assertEquals("S1", dto.getShiftId());
        assertEquals("Driver", dto.getRoleName());
    }

    @Test
    public void testToDTO_withNullDomain_returnsNull() {
        assertNull(ShiftAssignmentMapper.toDTO(null));
    }

    @Test
    public void testFromDTO_withValidDTO_convertsCorrectly() {
        // Given a DTO with employeeId, shiftId, roleName
        ShiftAssignmentDTO dto = new ShiftAssignmentDTO("E2", "S2", "Manager");

        // When mapping to domain object
        ShiftAssignment sa = ShiftAssignmentMapper.fromDTO(dto);

        // Then fields should match
        assertNotNull(sa);
        assertEquals("E2", sa.getEmployeeId());
        assertEquals("S2", sa.getShiftId());
        assertNotNull(sa.getRole());
        assertEquals("Manager", sa.getRole().getName());
    }

    @Test
    public void testFromDTO_withNullDTO_returnsNull() {
        assertNull(ShiftAssignmentMapper.fromDTO(null));
    }
}
