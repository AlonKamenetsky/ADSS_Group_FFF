package HR.tests.MapperTests;

import HR.Domain.ShiftTemplate;
import HR.Domain.Role;
import HR.Domain.Shift.ShiftTime;
import HR.DTO.ShiftTemplateDTO;

import HR.Mapper.ShiftTemplateMapper;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftTemplateMapperTest {

    @Test
    public void testToDTO_withValidDomain_convertsCorrectly() {
        // Given a domain ShiftTemplate for MONDAY MORNING
        ShiftTemplate template = new ShiftTemplate(DayOfWeek.MONDAY, ShiftTime.Morning);
        // Populate defaultCounts in domain: e.g., Role "Driver" -> 2, Role "Loader" -> 1
        Role driverRole = new Role("Driver");
        Role loaderRole = new Role("Loader");
        template.setDefaultCount(driverRole, 2);
        template.setDefaultCount(loaderRole, 1);

        // When mapping to DTO
        ShiftTemplateDTO dto = ShiftTemplateMapper.toDTO(template);

        // Then fields should match
        assertNotNull(dto);
        assertEquals(DayOfWeek.MONDAY, dto.getDay());
        assertEquals(ShiftTime.Morning, dto.getTime());

        Map<String, Integer> defaultCountsDto = dto.getDefaultCounts();
        assertNotNull(defaultCountsDto);
        // Should contain "Driver" -> 2 and "Loader" -> 1
        assertEquals(2, defaultCountsDto.get("Driver"));
        assertEquals(1, defaultCountsDto.get("Loader"));
        assertEquals(2, defaultCountsDto.size());
    }

    @Test
    public void testToDTO_withNullDomain_returnsNull() {
        assertNull(ShiftTemplateMapper.toDTO(null));
    }

    @Test
    public void testFromDTO_withValidDTO_convertsCorrectly() {
        // Given a DTO for TUESDAY EVENING with defaultCounts
        Map<String, Integer> dtoMap = Map.of(
                "Manager", 1,
                "Clerk",   3
        );
        ShiftTemplateDTO dto = new ShiftTemplateDTO(DayOfWeek.TUESDAY, ShiftTime.Evening, dtoMap);

        // When mapping to domain
        ShiftTemplate domain = ShiftTemplateMapper.fromDTO(dto);

        // Then fields should match
        assertNotNull(domain);
        assertEquals(DayOfWeek.TUESDAY, domain.getDay());
        assertEquals(ShiftTime.Evening, domain.getTime());

        Map<Role, Integer> defaultCountsDomain = domain.getDefaultCounts();
        assertNotNull(defaultCountsDomain);
        // Should contain a Role("Manager")->1 and Role("Clerk")->3
        assertEquals(1, defaultCountsDomain.get(new Role("Manager")));
        assertEquals(3, defaultCountsDomain.get(new Role("Clerk")));
        assertEquals(2, defaultCountsDomain.size());
    }

    @Test
    public void testFromDTO_withNullDTO_returnsNull() {
        assertNull(ShiftTemplateMapper.fromDTO(null));
    }
}
