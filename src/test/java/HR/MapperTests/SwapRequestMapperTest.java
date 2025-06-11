package HR.tests.MapperTests;

import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.SwapRequest;
import HR.DTO.SwapRequestDTO;
import HR.Mapper.SwapRequestMapper;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SwapRequestMapperTest {

    @Test
    public void toDTO_nullReturnsNull() {
        assertNull(SwapRequestMapper.toDTO(null));
    }

    @Test
    public void fromDTO_nullReturnsNull() {
        assertNull(SwapRequestMapper.fromDTO(null));
    }

    @Test
    public void toDTO_and_fromDTO_roundTrip_minimalPlaceholder() {
        // Arrange: create placeholders for Employee, Shift, Role
        Employee emp = new Employee(
                "emp42",
                List.of(new Role("Driver")),
                "Alice",
                "pwdHash",
                "ACCT123",
                3000f,
                new Date()
        );
        Shift shift = new Shift(
                "shift99",
                new Date(),
                Shift.ShiftTime.Morning,
                Map.of(),
                Map.of()
        );
        Role role = new Role("Driver");
        SwapRequest original = new SwapRequest(77, emp, shift, role);

        // Act: map to DTO, then back to domain
        SwapRequestDTO dto = SwapRequestMapper.toDTO(original);
        SwapRequest reconstructed = SwapRequestMapper.fromDTO(dto);

        // Assert: DTO fields
        assertNotNull(dto);
        assertEquals(77, dto.getId());
        assertEquals("emp42", dto.getEmployeeId());
        assertEquals("shift99", dto.getShiftId());
        assertEquals("Driver", dto.getRoleName());
        assertFalse(dto.isResolved());

        // Round-trip: reconstructed domain has placeholder fields
        assertNotNull(reconstructed);
        assertEquals(77, reconstructed.getId());
        assertEquals("emp42", reconstructed.getEmployee().getId());
        assertEquals("shift99", reconstructed.getShift().getID());
        assertEquals("Driver", reconstructed.getRole().getName());
    }
}
