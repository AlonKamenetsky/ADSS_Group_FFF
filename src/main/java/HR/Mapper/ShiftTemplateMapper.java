// File: HR/Mapper/ShiftTemplateMapper.java
package HR.Mapper;

import HR.Domain.ShiftTemplate;
import HR.Domain.Role;
import HR.DTO.ShiftTemplateDTO;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class ShiftTemplateMapper {
    public static ShiftTemplateDTO toDTO(ShiftTemplate t) {
        if (t == null) return null;
        // Domain getters: getDay(), getTime(), getDefaultCounts() → Map<Role,Integer>
        Map<String, Integer> defaultCountsDto = t.getDefaultCounts().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getName(),
                        Entry::getValue
                ));

        return new ShiftTemplateDTO(
                t.getDay(),
                t.getTime(),
                defaultCountsDto
        );
    }

    public static ShiftTemplate fromDTO(ShiftTemplateDTO dto) {
        if (dto == null) return null;
        // Domain constructor: public ShiftTemplate(DayOfWeek day, Shift.ShiftTime time)
        ShiftTemplate t = new ShiftTemplate(dto.getDay(), dto.getTime());

        // Now populate defaultCounts one by one:
        for (Map.Entry<String, Integer> entry : dto.getDefaultCounts().entrySet()) {
            Role r = new Role(entry.getKey());
            t.setDefaultCount(r, entry.getValue());
        }

        return t;
    }
}
