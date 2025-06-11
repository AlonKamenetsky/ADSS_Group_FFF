package HR.tests.MapperTests;

import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.ShiftTemplate;
import HR.DTO.ShiftTemplateDTO;
import HR.Mapper.ShiftTemplateMapper;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftTemplateMapperTest {

    @Test
    public void toDTO_nullReturnsNull() {
        assertNull(ShiftTemplateMapper.toDTO(null));
    }

    @Test
    public void fromDTO_nullReturnsNull() {
        assertNull(ShiftTemplateMapper.fromDTO(null));
    }

    @Test
    public void toDTO_and_fromDTO_roundTrip() {
        // Arrange: create a ShiftTemplate for Wednesday, Evening, with defaults
        DayOfWeek day = DayOfWeek.WEDNESDAY;
        Shift.ShiftTime time = Shift.ShiftTime.Evening;
        ShiftTemplate template = new ShiftTemplate(day, time);
        template.setDefaultCount(new Role("Cashier"), 3);
        template.setDefaultCount(new Role("Cleaner"), 2);

        // Act: map to DTO, then back to domain
        ShiftTemplateDTO dto = ShiftTemplateMapper.toDTO(template);
        ShiftTemplate reconstructed = ShiftTemplateMapper.fromDTO(dto);

        // Assert: DTO fields
        assertNotNull(dto);
        assertEquals(day, dto.getDay());
        assertEquals(time, dto.getTime());
        assertEquals(3, dto.getDefaultCounts().get("Cashier"));
        assertEquals(2, dto.getDefaultCounts().get("Cleaner"));

        // Round-trip: reconstructed domain
        assertNotNull(reconstructed);
        assertEquals(day, reconstructed.getDay());
        assertEquals(time, reconstructed.getTime());

        // defaultCounts in reconstructed must match
        Map<Role, Integer> recDefaults = reconstructed.getDefaultCounts();
        assertEquals(Integer.valueOf(3), recDefaults.get(new Role("Cashier")));
        assertEquals(Integer.valueOf(2), recDefaults.get(new Role("Cleaner")));
    }
}
