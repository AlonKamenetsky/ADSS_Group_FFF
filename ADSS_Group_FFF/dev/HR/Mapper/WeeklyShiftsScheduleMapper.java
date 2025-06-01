// File: HR/Mapper/WeeklyShiftsScheduleMapper.java
package HR.Mapper;

import HR.DTO.ShiftDTO;
import HR.Domain.WeeklyShiftsSchedule;
import HR.Domain.Shift;
import HR.DTO.WeeklyShiftsScheduleDTO;

import java.util.List;
import java.util.stream.Collectors;

public class WeeklyShiftsScheduleMapper {
    public static WeeklyShiftsScheduleDTO toDTO(WeeklyShiftsSchedule sched) {
        if (sched == null) return null;

        List<ShiftDTO> currentWeekDtos = sched.getCurrentWeek().stream()
                .map(ShiftMapper::toDTO)
                .collect(Collectors.toList());

        List<ShiftDTO> nextWeekDtos = sched.getNextWeek().stream()
                .map(ShiftMapper::toDTO)
                .collect(Collectors.toList());

        return new WeeklyShiftsScheduleDTO(currentWeekDtos, nextWeekDtos);
    }

    public static WeeklyShiftsSchedule fromDTO(WeeklyShiftsScheduleDTO dto) {
        if (dto == null) return null;
        WeeklyShiftsSchedule sched = new WeeklyShiftsSchedule();

        // Domain getters on WeeklyShiftsSchedule return mutable lists:
        for (HR.DTO.ShiftDTO shiftDto : dto.getCurrentWeek()) {
            Shift s = ShiftMapper.fromDTO(shiftDto);
            sched.getCurrentWeek().add(s);
        }
        for (HR.DTO.ShiftDTO shiftDto : dto.getNextWeek()) {
            Shift s = ShiftMapper.fromDTO(shiftDto);
            sched.getNextWeek().add(s);
        }

        return sched;
    }
}
