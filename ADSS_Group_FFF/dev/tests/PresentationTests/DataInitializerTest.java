package tests.PresentationTests;

import Domain.EmployeesRepo;
import Domain.RolesRepo;
import Domain.ShiftsRepo;
import Presentation.DemoDataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataInitializerTest {

    @BeforeEach
    void setUp() throws Exception {
        // Clear repositories before each test.
        RolesRepo.getInstance().getRoles().clear();
        EmployeesRepo.getInstance().getEmployees().clear();
        ShiftsRepo.getInstance().getCurrentWeekShifts().clear();
    }

    @Test
    void testInitializeExampleData() throws Exception {
        DemoDataLoader.initializeExampleData(0);
        // Expect some roles (HR and others) to be loaded.
        assertFalse(RolesRepo.getInstance().getRoles().isEmpty(), "Roles should be loaded");
        // Expect at least two employees.
        assertTrue(EmployeesRepo.getInstance().getEmployees().size() >= 2, "At least two employees should be loaded");
        // Expect two shifts to be loaded.
        assertEquals(14, ShiftsRepo.getInstance().getCurrentWeekShifts().size(), "Two shifts should be created for each day of the week");
    }
}
