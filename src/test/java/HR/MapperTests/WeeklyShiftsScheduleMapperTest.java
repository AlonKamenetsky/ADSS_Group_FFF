package HR.tests.MapperTests;

import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.WeeklyShiftsSchedule;
import HR.DTO.ShiftDTO;
import HR.DTO.WeeklyShiftsScheduleDTO;
import HR.Mapper.WeeklyShiftsScheduleMapper;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class WeeklyShiftsScheduleMapperTest {

    @Test
    public void toDTO_nullReturnsNull() {
        assertNull(WeeklyShiftsScheduleMapper.toDTO(null));
    }

    @Test
    public void fromDTO_nullReturnsNull() {
        assertNull(WeeklyShiftsScheduleMapper.fromDTO(null));
    }

    @Test
    public void toDTO_and_fromDTO_roundTrip() {
        // Arrange: create a WeeklyShiftsSchedule with one shift in current week and one in next
        WeeklyShiftsSchedule original = new WeeklyShiftsSchedule();

        // Create two dummy shifts
        Shift s1 = new Shift(
                "shiftA",
                new Date(0),
                Shift.ShiftTime.Morning,
                Map.of(),            // requiredRoles
                Map.of(new Role("Cashier"), 2)
        );
        Shift s2 = new Shift(
                "shiftB",
                new Date(1000),
                Shift.ShiftTime.Evening,
                Map.of(),
                Map.of(new Role("Cleaner"), 1)
        );

        // Add s1 to currentWeek, s2 to nextWeek
        original.getCurrentWeek().add(s1);
        original.getNextWeek().add(s2);

        // Act: map to DTO, then back to domain
        WeeklyShiftsScheduleDTO dto = WeeklyShiftsScheduleMapper.toDTO(original);
        WeeklyShiftsSchedule reconstructed = WeeklyShiftsScheduleMapper.fromDTO(dto);

        // Assert DTO fields
        assertNotNull(dto);
        List<ShiftDTO> currDtos = dto.getCurrentWeek();
        List<ShiftDTO> nextDtos = dto.getNextWeek();
        assertEquals(1, currDtos.size());
        assertEquals("shiftA", currDtos.get(0).getId());
        assertEquals(2, currDtos.get(0).getRequiredCounts().get("Cashier"));

        assertEquals(1, nextDtos.size());
        assertEquals("shiftB", nextDtos.get(0).getId());
        assertEquals(1, nextDtos.get(0).getRequiredCounts().get("Cleaner"));

        // Reconstructed domain: check lists
        assertNotNull(reconstructed);
        assertEquals(1, reconstructed.getCurrentWeek().size());
        assertEquals("shiftA", reconstructed.getCurrentWeek().get(0).getID());
        assertEquals(2, reconstructed.getCurrentWeek().get(0).getRequiredCounts().get(new Role("Cashier")));

        assertEquals(1, reconstructed.getNextWeek().size());
        assertEquals("shiftB", reconstructed.getNextWeek().get(0).getID());
        assertEquals(1, reconstructed.getNextWeek().get(0).getRequiredCounts().get(new Role("Cleaner")));
    }
}
