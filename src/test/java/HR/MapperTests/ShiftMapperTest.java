package HR.tests.MapperTests;

import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.ShiftAssignment;
import HR.DTO.ShiftAssignmentDTO;
import HR.DTO.ShiftDTO;
import HR.Mapper.ShiftMapper;
import HR.Service.ShiftService;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftMapperTest {

    @Test
    public void toDTO_nullReturnsNull() {
        assertNull(ShiftMapper.toDTO(null));
    }

    @Test
    public void fromDTO_nullReturnsNull() {
        assertNull(ShiftMapper.fromDTO(null));
    }

    @Test
    public void toDTO_and_fromDTO_roundTrip() {
        // Arrange: build a Shift domain object
        String shiftId = "shift-001";
        Date today = new Date();
        Shift.ShiftTime shiftTime = Shift.ShiftTime.Morning;

        // requiredCounts: 2 cashiers, 1 cleaner
        Role cashierRole = new Role("Cashier");
        Role cleanerRole = new Role("Cleaner");
        Map<Role, Integer> requiredCounts = new HashMap<>();
        requiredCounts.put(cashierRole, 2);
        requiredCounts.put(cleanerRole, 1);

        // requiredRoles is empty for this test
        Map<Role, ArrayList<Employee>> requiredRoles = Map.of();

        Shift domainShift = new Shift(
                shiftId,
                today,
                shiftTime,
                requiredRoles,
                requiredCounts
        );

        // Add two assignments: empA as Cashier, empB as Cleaner
        ShiftAssignment assignA = new ShiftAssignment("empA", shiftId, cashierRole);
        assignA.setShift(domainShift);
        ShiftAssignment assignB = new ShiftAssignment("empB", shiftId, cleanerRole);
        assignB.setShift(domainShift);
        domainShift.setAssignedEmployees(List.of(assignA, assignB));

        // Act: map to DTO, then back to domain
        ShiftDTO dto = ShiftMapper.toDTO(domainShift);
        Shift reconstructed = ShiftMapper.fromDTO(dto);

        // Assert: DTO fields
        assertNotNull(dto);
        assertEquals(shiftId, dto.getId());
        assertEquals(today, dto.getDate());
        assertEquals(shiftTime, dto.getType());

        // requiredCountsDto should have string keys
        Map<String, Integer> countsDto = dto.getRequiredCounts();
        assertEquals(2, countsDto.get("Cashier"));
        assertEquals(1, countsDto.get("Cleaner"));

        // assignment DTOs
        List<ShiftAssignmentDTO> assignDtos = dto.getAssignedEmployees();
        assertTrue(assignDtos.stream().anyMatch(a ->
                a.getEmployeeId().equals("empA") && a.getRoleName().equals("Cashier")
        ));
        assertTrue(assignDtos.stream().anyMatch(a ->
                a.getEmployeeId().equals("empB") && a.getRoleName().equals("Cleaner")
        ));

        // Round-trip: reconstructed domain
        assertNotNull(reconstructed);
        assertEquals(shiftId, reconstructed.getID());
        assertEquals(today, reconstructed.getDate());
        assertEquals(shiftTime, reconstructed.getType());

        // requiredCounts must match original
        Map<Role, Integer> recCounts = reconstructed.getRequiredCounts();
        assertEquals(2, recCounts.get(new Role("Cashier")));
        assertEquals(1, recCounts.get(new Role("Cleaner")));

        // assignedEmployees should contain two ShiftAssignment with correct IDs and roles
        List<ShiftAssignment> recAssigns = reconstructed.getAssignedEmployees();
        assertTrue(recAssigns.stream().anyMatch(sa ->
                sa.getEmployeeId().equals("empA") && sa.getRole().getName().equals("Cashier")
        ));
        assertTrue(recAssigns.stream().anyMatch(sa ->
                sa.getEmployeeId().equals("empB") && sa.getRole().getName().equals("Cleaner")
        ));
        // Each assignment’s "shift" reference should point to the reconstructed Shift
        recAssigns.forEach(sa -> assertEquals(reconstructed, ShiftService.getInstance().getShiftById(sa.getShiftId())));
    }
}
