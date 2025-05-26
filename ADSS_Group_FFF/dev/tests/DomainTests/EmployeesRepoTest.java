package tests.DomainTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Domain.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeesRepoTest {

    private EmployeesRepo employeesRepo;
    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    void setUp() {
        employeesRepo = EmployeesRepo.getInstance();
        employeesRepo.getEmployees().clear();

        Role hrRole = new Role("HR");
        Role cashierRole = new Role("Cashier");

        employee1 = new Employee("1", List.of(hrRole), "Dana", "pass1", "Bank1", 5000f, null);
        employee2 = new Employee("2", List.of(cashierRole), "John", "pass2", "Bank2", 4000f, null);
    }

    @Test
    void testAddEmployee() {
        employeesRepo.addEmployee(employee1);
        assertEquals(1, employeesRepo.getEmployees().size());
        assertTrue(employeesRepo.getEmployees().contains(employee1));
    }

    @Test
    void testRemoveEmployee() {
        employeesRepo.addEmployee(employee1);
        employeesRepo.removeEmployee(employee1);
        assertFalse(employeesRepo.getEmployees().contains(employee1));
    }

    @Test
    void testGetEmployeeById() {
        employeesRepo.addEmployee(employee1);
        Employee found = employeesRepo.getEmployeeById("1");
        assertEquals(employee1, found);
    }

    @Test
    void testGetEmployeeByIdNotFound() {
        Employee found = employeesRepo.getEmployeeById("999");
        assertNull(found);
    }
}
