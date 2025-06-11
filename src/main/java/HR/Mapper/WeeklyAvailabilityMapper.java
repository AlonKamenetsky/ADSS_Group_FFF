// File: HR/Mapper/WeeklyAvailabilityMapper.java
package HR.Mapper;

import HR.Domain.WeeklyAvailability;
import HR.DTO.WeeklyAvailabilityDTO;

public class WeeklyAvailabilityMapper {
    public static WeeklyAvailabilityDTO toDTO(WeeklyAvailability wa) {
        if (wa == null) return null;
        // Domain has getters: getDay(), getTime()
        return new WeeklyAvailabilityDTO(wa.getDay(), wa.getTime());
    }

    public static WeeklyAvailability fromDTO(WeeklyAvailabilityDTO dto) {
        if (dto == null) return null;
        // Domain constructor is: public WeeklyAvailability(DayOfWeek day, Shift.ShiftTime time)
        return new WeeklyAvailability(dto.getDay(), dto.getTime());
    }
}
