package communicationInventoryAndSupplier;

import java.time.DayOfWeek;

public class SimulationClock {
    private DayOfWeek currentDay = DayOfWeek.MONDAY;

    public DayOfWeek getCurrentDay() {
        return currentDay;
    }

    public void advanceDay() {
        currentDay = currentDay.plus(1);  // Automatically wraps from SUNDAY to MONDAY
        System.out.println("Day advanced. Now it is " + currentDay);
    }

}
