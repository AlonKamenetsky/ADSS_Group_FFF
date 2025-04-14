package tests.PresentationTests;

import Domain.Role;
import Domain.User;
import Presentation.LoginScreen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class LoginScreenTest {
    private LoginScreen loginScreen;
    private List<User> users;

    @BeforeEach
    public void setUp() {
        // Create a user with one role.
        Role cashier = new Role("Cashier");
        User user = new User("1", "Dana", "123", List.of(cashier));
        users = List.of(user);
        loginScreen = new LoginScreen(users);
    }

    @Test
    public void testLoginSuccessful() {
        // Simulate input: first line: "1" (ID), second line: "1" (select first role)
        String input = "1\n1\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);
        User loggedIn = loginScreen.login(scanner);
        assertNotNull(loggedIn);
        assertEquals("1", loggedIn.getId());
    }

    @Test
    public void testLoginUserNotFound() {
        // Simulate input with a non-existent ID.
        String input = "999\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);
        User loggedIn = loginScreen.login(scanner);
        assertNull(loggedIn);
    }
}
