package HR.tests.MapperTests;

import HR.Domain.WeeklyAvailability;
import HR.DTO.WeeklyAvailabilityDTO;
import HR.Domain.Shift;
import HR.Mapper.WeeklyAvailabilityMapper;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;

public class WeeklyAvailabilityMapperTest {

    @Test
    public void toDTO_nullReturnsNull() {
        assertNull(WeeklyAvailabilityMapper.toDTO(null));
    }

    @Test
    public void fromDTO_nullReturnsNull() {
        assertNull(WeeklyAvailabilityMapper.fromDTO(null));
    }

    @Test
    public void toDTO_and_fromDTO_roundTrip() {
        // Arrange: create a WeeklyAvailability domain object
        WeeklyAvailability original = new WeeklyAvailability(DayOfWeek.WEDNESDAY, Shift.ShiftTime.Evening);

        // Act: map to DTO, then back to domain
        WeeklyAvailabilityDTO dto = WeeklyAvailabilityMapper.toDTO(original);
        WeeklyAvailability reconstructed = WeeklyAvailabilityMapper.fromDTO(dto);

        // Assert DTO fields
        assertNotNull(dto);
        assertEquals(DayOfWeek.WEDNESDAY, dto.getDay());
        assertEquals(Shift.ShiftTime.Evening, dto.getTime());

        // Reconstructed domain should match
        assertNotNull(reconstructed);
        assertEquals(DayOfWeek.WEDNESDAY, reconstructed.getDay());
        assertEquals(Shift.ShiftTime.Evening, reconstructed.getTime());
    }
}
