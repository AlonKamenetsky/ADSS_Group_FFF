package tests.PresentationTests;

import Domain.Employee;
import Domain.Role;
import Presentation.LoginScreen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class LoginScreenTest {
    private LoginScreen loginScreen;
    private List<Employee> users;

    @BeforeEach
    public void setUp() {
        // create one employee with a single role and password "123"
        Role cashier = new Role("Cashier");
        Employee user = new Employee(
                "1",
                List.of(cashier),
                "Dana",
                "123",
                "BA123",
                0f,
                new Date()    // employment date can be anything
        );
        users = List.of(user);
        loginScreen = new LoginScreen(users);
    }

    @Test
    public void testLoginSuccessful() {
        // ID = 1, Password = 123
        String input = String.join("\n",
                "1",    // enter ID
                "123"   // enter password
        ) + "\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        // login(...) now returns boolean
        assertTrue(loginScreen.login(scanner,1), "Login should succeed with correct credentials");

        // after success, currentUser and currentRole must be set
        assertNotNull(loginScreen.getCurrentUser(), "Current user must be populated");
        assertEquals("1", loginScreen.getCurrentUser().getId());
        assertNotNull(loginScreen.getCurrentRole(), "Current role must be populated");
        assertEquals("Cashier", loginScreen.getCurrentRole().getName());
    }

    @Test
    public void testLoginUserNotFound() {
        // simulate wrong ID, then exit to break out
        String input = String.join("\n",
                "999",   // nonexistent ID
                "foo",   // password (won't match)
                "exit"   // type 'exit' to terminate login loop
        ) + "\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        // login(...) should return false on exit after failure
        assertFalse(loginScreen.login(scanner,1), "Login should return false when user not found and then exit");
    }
}
