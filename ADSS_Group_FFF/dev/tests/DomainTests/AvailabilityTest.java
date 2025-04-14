package tests.DomainTests;

import org.junit.jupiter.api.Test;
import Domain.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilityTest {

    @Test
    void testGetDateAndType() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = dateFormat.parse("01-05-2025");
        Shift.ShiftTime shiftTime = Shift.ShiftTime.Morning;

        Availability availability = new Availability(date, shiftTime);

        assertEquals(date, availability.getDate());
        assertEquals(shiftTime, availability.getType());
    }
}
