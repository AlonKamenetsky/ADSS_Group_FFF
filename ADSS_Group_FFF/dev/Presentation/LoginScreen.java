package Presentation;

import java.util.List;
import java.util.Scanner;
import Domain.*;

public class LoginScreen {
    private String id;
    private String role;
    private List<UserDL> users;

    public LoginScreen(List<UserDL> users) {
        this.users = users;
    }

    public UserDL login(Scanner scanner) {
        System.out.println("Enter your ID:");
        String id = scanner.nextLine();
        for (UserDL user : users) {
            if (user.getId().equals(id)) {
                System.out.println("Select Role:");
                List<RoleDL> roles = user.getRoles();
                for (int i = 0; i < roles.size(); i++) {
                    System.out.println(i + 1 + ". " + roles.get(i).getName());
                }
                int choice = scanner.nextInt() - 1;
                scanner.nextLine();
                System.out.println("Logged in as: " + roles.get(choice).getName());
                return user;
            }
        }
        System.out.println("User not found.");
        return null;
    }
}
