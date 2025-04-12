package Domain;

import java.util.ArrayList;
import java.util.List;

public class ShiftsRepo {

    private static ShiftsRepo instance = null;

    private final List<Shift> shifts;

    private ShiftsRepo() {
        this.shifts = new ArrayList<>();
    }

    public static ShiftsRepo getInstance() {
        if (instance == null) {
            instance = new ShiftsRepo();
        }
        return instance;
    }

    public void addShift(Shift shift) {
        shifts.add(shift);
    }

    // Get all shifts
    public List<Shift> getShifts() {
        return shifts;
    }

    // Get shift by ID
    public Shift getShiftById(String id) {
        return shifts.stream()
                .filter(s -> s.getID().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void removeShift(Shift shift) {
        if (shifts.contains(shift)) {
            shifts.remove(shift);
        } else {
            System.out.println("Shift not found.");
        }
    }
}
