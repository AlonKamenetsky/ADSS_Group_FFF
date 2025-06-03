package HR.tests.MapperTests;

import HR.Domain.*;
import HR.DTO.*;
import HR.Mapper.ShiftMapper;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftMapperTest {

    @Test
    public void testToDTO_withValidShift_returnsCorrectDTO() {
        Role role = new Role("Driver");
        Employee employee = new Employee("E1", List.of(role), "John", null, "1234", 5000f, new Date());
        Shift shift = new Shift(
                "S1",
                Date.valueOf("2025-06-04"),
                Shift.ShiftTime.Morning,
                new HashMap<>(Map.of(role, new ArrayList<>())) ,
                new HashMap<>(Map.of(role, 2))
        );
        ShiftAssignment assignment = new ShiftAssignment(shift, employee, role);
        shift.setAssignedEmployees(List.of(assignment));

        ShiftDTO dto = ShiftMapper.toDTO(shift);

        assertEquals("S1", dto.getId());
        assertEquals(LocalDate.of(2025, 6, 4), dto.getDate());
        assertEquals(Shift.ShiftTime.Morning, dto.getType());
        assertEquals(1, dto.getAssignedEmployees().size());
        assertEquals(2, dto.getRequiredCounts().get("Driver"));
    }

    @Test
    public void testFromDTO_withValidDTO_returnsCorrectShift() {
        Map<String, Integer> requiredCounts = Map.of("Driver", 1);
        ShiftAssignmentDTO assignmentDTO = new ShiftAssignmentDTO("S1", "E1", "Driver");
        ShiftDTO dto = new ShiftDTO("S1", LocalDate.of(2025, 6, 4), Shift.ShiftTime.Morning,
                requiredCounts, List.of(assignmentDTO));

        Shift shift = ShiftMapper.fromDTO(dto);

        assertEquals("S1", shift.getID());
        assertEquals(Date.valueOf("2025-06-04"), shift.getDate());
        assertEquals(Shift.ShiftTime.Morning, shift.getType());
        assertEquals(1, shift.getRequiredCounts().size());
        assertEquals(1, shift.getAssignedEmployees().size());
        assertEquals("Driver", shift.getAssignedEmployees().get(0).getRole().getName());
        assertEquals("S1", shift.getAssignedEmployees().get(0).getShift().getID());
    }

    @Test
    public void testToDTO_withNullInput_returnsNull() {
        assertNull(ShiftMapper.toDTO(null));
    }

    @Test
    public void testFromDTO_withNullInput_returnsNull() {
        assertNull(ShiftMapper.fromDTO(null));
    }
}
