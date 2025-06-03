package HR.tests.MapperTests;

import HR.Domain.WeeklyShiftsSchedule;
import HR.Domain.Shift;
import HR.DTO.WeeklyShiftsScheduleDTO;
import HR.DTO.ShiftDTO;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WeeklyShiftsScheduleMapperTest {

    @Test
    public void testToDTO_withValidDomain_convertsListsCorrectly() {
        // Prepare two mock domain Shifts
        Shift s1 = mock(Shift.class);
        Shift s2 = mock(Shift.class);

        // Prepare corresponding ShiftDTOs
        ShiftDTO dto1 = new ShiftDTO();
        ShiftDTO dto2 = new ShiftDTO();

        // Mock ShiftMapper.toDTO(...) to return our DTOs
        try (MockedStatic<ShiftMapper> shiftMapperMock = mockStatic(ShiftMapper.class)) {
            shiftMapperMock.when(() -> ShiftMapper.toDTO(s1)).thenReturn(dto1);
            shiftMapperMock.when(() -> ShiftMapper.toDTO(s2)).thenReturn(dto2);

            // Create a domain WeeklyShiftsSchedule and populate its lists
            WeeklyShiftsSchedule domain = new WeeklyShiftsSchedule();
            domain.getCurrentWeek().add(s1);
            domain.getNextWeek().add(s2);

            // When mapping to DTO
            WeeklyShiftsScheduleDTO resultDto = WeeklyShiftsScheduleMapper.toDTO(domain);

            assertNotNull(resultDto);

            // The currentWeek list in DTO should contain dto1
            List<ShiftDTO> currentWeekDtos = resultDto.getCurrentWeek();
            assertNotNull(currentWeekDtos);
            assertEquals(1, currentWeekDtos.size());
            assertSame(dto1, currentWeekDtos.get(0));

            // The nextWeek list in DTO should contain dto2
            List<ShiftDTO> nextWeekDtos = resultDto.getNextWeek();
            assertNotNull(nextWeekDtos);
            assertEquals(1, nextWeekDtos.size());
            assertSame(dto2, nextWeekDtos.get(0));
        }
    }

    @Test
    public void testToDTO_withNullDomain_returnsNull() {
        assertNull(WeeklyShiftsScheduleMapper.toDTO(null));
    }

    @Test
    public void testFromDTO_withValidDTO_convertsListsCorrectly() {
        // Prepare two ShiftDTOs
        ShiftDTO dto1 = new ShiftDTO();
        ShiftDTO dto2 = new ShiftDTO();

        // Prepare two mock domain Shifts to be returned by ShiftMapper.fromDTO
        Shift s1 = mock(Shift.class);
        Shift s2 = mock(Shift.class);

        // Mock ShiftMapper.fromDTO(...) to return our domain Shifts
        try (MockedStatic<ShiftMapper> shiftMapperMock = mockStatic(ShiftMapper.class)) {
            shiftMapperMock.when(() -> ShiftMapper.fromDTO(dto1)).thenReturn(s1);
            shiftMapperMock.when(() -> ShiftMapper.fromDTO(dto2)).thenReturn(s2);

            // Create a DTO WeeklyShiftsScheduleDTO and populate its lists
            WeeklyShiftsScheduleDTO dto = new WeeklyShiftsScheduleDTO(
                    List.of(dto1),
                    List.of(dto2)
            );

            // When mapping to domain
            WeeklyShiftsSchedule domain = WeeklyShiftsScheduleMapper.fromDTO(dto);

            assertNotNull(domain);

            // The currentWeek list in domain should contain s1
            List<Shift> currentWeekDomain = domain.getCurrentWeek();
            assertNotNull(currentWeekDomain);
            assertEquals(1, currentWeekDomain.size());
            assertSame(s1, currentWeekDomain.get(0));

            // The nextWeek list in domain should contain s2
            List<Shift> nextWeekDomain = domain.getNextWeek();
            assertNotNull(nextWeekDomain);
            assertEquals(1, nextWeekDomain.size());
            assertSame(s2, nextWeekDomain.get(0));
        }
    }

    @Test
    public void testFromDTO_withNullDTO_returnsNull() {
        assertNull(WeeklyShiftsScheduleMapper.fromDTO(null));
    }
}
