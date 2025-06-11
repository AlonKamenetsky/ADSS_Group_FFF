package HR.tests.MapperTests;

import HR.Domain.Role;
import HR.Domain.ShiftAssignment;
import HR.DTO.ShiftAssignmentDTO;
import HR.Mapper.ShiftAssignmentMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftAssignmentMapperTest {

    @Test
    public void toDTO_nullReturnsNull() {
        assertNull(ShiftAssignmentMapper.toDTO(null));
    }

    @Test
    public void fromDTO_nullReturnsNull() {
        assertNull(ShiftAssignmentMapper.fromDTO(null));
    }

    @Test
    public void toDTO_and_fromDTO_roundTrip() {
        // Arrange: create a ShiftAssignment with known values
        Role role = new Role("Cashier");
        ShiftAssignment original = new ShiftAssignment("emp123", "shift456", role);

        // Act: map to DTO, then back to domain
        ShiftAssignmentDTO dto = ShiftAssignmentMapper.toDTO(original);
        ShiftAssignment reconstructed = ShiftAssignmentMapper.fromDTO(dto);

        // Assert: DTO fields match original
        assertNotNull(dto);
        assertEquals("emp123", dto.getEmployeeId());
        assertEquals("shift456", dto.getShiftId());
        assertEquals("Cashier", dto.getRoleName());

        // Assert: reconstructed domain matches original (except Shift reference)
        assertNotNull(reconstructed);
        assertEquals("emp123", reconstructed.getEmployeeId());
        assertEquals("shift456", reconstructed.getShiftId());
        assertEquals("Cashier", reconstructed.getRole().getName());
    }
}
