package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import Domain.*;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    private Employee employee;
    private Role hrRole;
    private Role cashierRole;

    @BeforeEach
    void setUp() throws ParseException {
        hrRole = new Role("HR");
        cashierRole = new Role("Cashier");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date employmentDate = dateFormat.parse("01-01-2020");

        employee = new Employee("1",
                new LinkedList<>(Arrays.asList(hrRole, cashierRole)),
                "Dana",
                "123",
                "IL123BANK",
                5000f,
                employmentDate);
    }

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
    void testGetEmploymentDate() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date expectedDate = dateFormat.parse("01-01-2020");
        assertEquals(expectedDate, employee.getEmploymentDate());
    }

    @Test
    void testRoles() {
        assertTrue(employee.getRoles().contains(hrRole));
        assertTrue(employee.getRoles().contains(cashierRole));
    }
}
