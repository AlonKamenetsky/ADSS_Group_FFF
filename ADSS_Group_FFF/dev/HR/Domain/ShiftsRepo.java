package HR.Domain;
import java.util.*;

public interface ShiftsRepo {
    void addTemplate(ShiftTemplate r);
    void ensureUpToDate();
    List<Shift> getCurrentWeekShifts();
    List<Shift> getNextWeekShifts();
    Optional<Shift> getCurrentShift();
    Shift getShiftByID(String id);
    boolean isSameDay(Date d1, Date d2);
    WeeklyShiftsSchedule getSchedule();
    List<ShiftTemplate> getTemplates();
    List<Shift> getHistory();
}
