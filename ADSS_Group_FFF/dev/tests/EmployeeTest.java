package tests;

import Domain.Availability;
import Domain.Employee;
import Domain.Role;
import Domain.Shift;
import Domain.ShiftsRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    private Employee employee;
    private Role hrRole;
    private Role cashierRole;
    private SimpleDateFormat dateFormat;

    @BeforeEach
    void setUp() throws Exception {
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date employmentDate = dateFormat.parse("01-01-2020");
        hrRole = new Role("HR");
        cashierRole = new Role("Cashier");
        employee = new Employee("1",
                new LinkedList<>(Arrays.asList(hrRole, cashierRole)),
                "Dana",
                "123",
                "IL123BANK",
                5000f,
                employmentDate);
        employee.setPassword("123");
    }

    // Basic getters and setters tests
    @Test
    void testGetId() {
        assertEquals("1", employee.getId());
    }

    @Test
    void testGetName() {
        assertEquals("Dana", employee.getName());
    }

    @Test
    void testGetPassword() {
        assertEquals("123", employee.getPassword());
    }

    @Test
    void testGetBankAccount() {
        assertEquals("IL123BANK", employee.getBankAccount());
    }

    @Test
    void testSetBankAccount() {
        employee.setBankAccount("NEWBANK123");
        assertEquals("NEWBANK123", employee.getBankAccount());
    }

    @Test
    void testGetSalary() {
        assertEquals(5000f, employee.getSalary());
    }

    @Test
    void testSetSalary() {
        employee.setSalary(7000f);
        assertEquals(7000f, employee.getSalary());
    }

    @Test
    void testGetEmploymentDate() throws Exception {
        Date expectedDate = dateFormat.parse("01-01-2020");
        assertEquals(expectedDate, employee.getEmploymentDate());
    }

    @Test
    void testRoles() {
        assertTrue(employee.getRoles().contains(hrRole));
        assertTrue(employee.getRoles().contains(cashierRole));
    }

    // Tests for Weekly Availability functionality

    @Test
    void testIsAvailableInitiallyFalse() throws Exception {
        // With an empty weekly availability list, isAvailable() should return false.
        Date testDate = dateFormat.parse("10-05-2025");
        assertFalse(employee.isAvailable(testDate, Shift.ShiftTime.Morning));
    }

    @Test
    void testAvailabilityAfterAdding() throws Exception {
        Date testDate = dateFormat.parse("10-05-2025");
        employee.addAvailability(testDate, Shift.ShiftTime.Morning);
        assertTrue(employee.isAvailable(testDate, Shift.ShiftTime.Morning));
    }

    // Tests for Vacation (Holidays) functionality

    @Test
    void testVacationsInitiallyEmpty() {
        assertTrue(employee.getHolidays().isEmpty());
    }

    @Test
    void testAddVacation() throws Exception {
        Date vacationDate1 = dateFormat.parse("15-08-2025");
        Date vacationDate2 = dateFormat.parse("16-08-2025");
        employee.getHolidays().add(vacationDate1);
        employee.getHolidays().add(vacationDate2);
        assertEquals(2, employee.getHolidays().size());
        assertTrue(employee.getHolidays().contains(vacationDate1));
        assertTrue(employee.getHolidays().contains(vacationDate2));
    }
}
