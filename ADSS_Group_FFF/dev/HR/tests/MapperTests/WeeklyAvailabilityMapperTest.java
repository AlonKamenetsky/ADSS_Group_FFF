package HR.tests.MapperTests;

import HR.Domain.WeeklyAvailability;
import HR.Domain.Shift.ShiftTime;      // <— import from Shift, not WeeklyAvailability
import HR.DTO.WeeklyAvailabilityDTO;

import HR.Mapper.WeeklyAvailabilityMapper;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;

public class WeeklyAvailabilityMapperTest {

    @Test
    public void testToDTO_withValidDomain_convertsCorrectly() {
        // Given a domain WeeklyAvailability for Tuesday Morning
        WeeklyAvailability wa = new WeeklyAvailability(DayOfWeek.TUESDAY, ShiftTime.Morning);

        // When mapping to DTO
        WeeklyAvailabilityDTO dto = WeeklyAvailabilityMapper.toDTO(wa);

        // Then the DTO fields should match
        assertNotNull(dto);
        assertEquals(DayOfWeek.TUESDAY, dto.getDay());
        assertEquals(ShiftTime.Morning, dto.getTime());
    }

    @Test
    public void testToDTO_withNullDomain_returnsNull() {
        assertNull(WeeklyAvailabilityMapper.toDTO(null));
    }

    @Test
    public void testFromDTO_withValidDTO_convertsCorrectly() {
        // Given a DTO for Friday Evening
        WeeklyAvailabilityDTO dto = new WeeklyAvailabilityDTO(DayOfWeek.FRIDAY, ShiftTime.Evening);

        // When mapping to domain object
        WeeklyAvailability wa = WeeklyAvailabilityMapper.fromDTO(dto);

        // Then the domain fields should match
        assertNotNull(wa);
        assertEquals(DayOfWeek.FRIDAY, wa.getDay());
        assertEquals(ShiftTime.Evening, wa.getTime());
    }

    @Test
    public void testFromDTO_withNullDTO_returnsNull() {
        assertNull(WeeklyAvailabilityMapper.fromDTO(null));
    }
}
