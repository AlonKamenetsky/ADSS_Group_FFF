
package HR.tests.ServiceTests;

import HR.DTO.ShiftDTO;
import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.ShiftAssignment;
import HR.Service.ShiftService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftServiceTest {

    private ShiftService shiftService;

    @BeforeEach
    public void setUp() {
        shiftService = ShiftService.getInstance();
    }

    @Test
    public void testCreateAndRetrieveShift() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        shiftService.createShift(date, Shift.ShiftTime.Morning);
        ShiftDTO dto = shiftService.getShift(date, Shift.ShiftTime.Morning);
        assertNotNull(dto);
        assertEquals(date, dto.getDate());
        assertEquals(Shift.ShiftTime.Morning, dto.getTime());
    }

    @Test
    public void testAssignEmployeeToShift() {
        LocalDate date = LocalDate.of(2025, 1, 2);
        shiftService.createShift(date, Shift.ShiftTime.Evening);
        String empId = "emp1001";
        Role role = new Role("Cook");
        shiftService.setRequiredCount(date, Shift.ShiftTime.Evening, role, 1);
        boolean assigned = shiftService.assignEmployee(date, Shift.ShiftTime.Evening, empId, role);
        assertTrue(assigned);
    }

    @Test
    public void testGetAllShifts() {
        List<ShiftDTO> allShifts = shiftService.getAllShifts();
        assertNotNull(allShifts);
        assertTrue(allShifts.size() >= 0);
    }
}
