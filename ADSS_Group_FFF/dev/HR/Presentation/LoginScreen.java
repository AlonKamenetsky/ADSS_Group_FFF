package HR.Presentation;

import java.util.List;
import java.util.Scanner;
import HR.Domain.Employee;
import HR.Domain.Role;

public class LoginScreen {
    private final List<Employee> users;
    private Employee currentUser;
    private Role     currentRole;

    public LoginScreen(List<Employee> users) {
        this.users = users;
    }

    /**
     * Prompts until we get a valid ID/password or "exit".
     * Returns false if the user typed "exit" at either prompt.
     */
    public boolean login(Scanner scanner) {
        while (true) {
            PresentationUtils.typewriterPrint("\n--- LOGIN ---", 20
);
            PresentationUtils.typewriterPrint("Type 'exit' at any prompt to quit.", 20
);

            PresentationUtils.typewriterPrint("ID: ", 20
);
            String id = scanner.nextLine().trim();
            if (id.equalsIgnoreCase("exit")) return false;

            PresentationUtils.typewriterPrint("Password: ", 20
);
            String pw = scanner.nextLine().trim();
            if (pw.equalsIgnoreCase("exit")) return false;

            // find a matching employee
            Employee found = null;
            for (Employee e : users) {
                if (e.getId().equals(id) && pw.equals(e.getPassword())) {
                    found = e;
                    break;
                }
            }
            if (found == null) {
                PresentationUtils.typewriterPrint("Invalid ID or password. Try again.", 20
);
                continue;
            }

            this.currentUser = found;
            List<Role> roles = found.getRoles();

            // if they only have one role, auto‑select it
            if (roles.size() == 1) {
                this.currentRole = roles.get(0);
            } else {
                // otherwise force a valid numeric choice
                while (true) {
                    PresentationUtils.typewriterPrint("Select Role:", 20
);
                    for (int i = 0; i < roles.size(); i++) {
                        System.out.printf("  %d) %s%n", i+1, roles.get(i).getName());
                    }
                    PresentationUtils.typewriterPrint("", 20
);
                    String line = scanner.nextLine().trim();
                    try {
                        int idx = Integer.parseInt(line) - 1;
                        if (idx >= 0 && idx < roles.size()) {
                            this.currentRole = roles.get(idx);
                            break;
                        }
                    } catch (NumberFormatException ignored) {}
                    PresentationUtils.typewriterPrint("Invalid choice; please enter the number of your role.", 20
);
                }
            }

            System.out.printf("Logged in as %s [%s]%n",
                    currentUser.getName(), currentRole.getName());
            return true;
        }
    }

    /**
     * Drives the whole “login → dispatch to UI → switch-user?” loop.
     * Stops when user types “exit” or says “no” to switching.
     */
    public void run(Scanner scanner) {
        while (true) {
            if (!login(scanner)) break;

            // dispatch to the correct interface
            if (currentRole.getName().equals("HR")) {
                HRInterface hr = new HRInterface(currentUser.getId());
                hr.setCurrentUserRole(currentRole);
                hr.managerMainMenu(scanner);
            } else {
                EmployeeInterface ui = new EmployeeInterface(currentUser);
                ui.employeeMainMenu(scanner);
            }

            // after they return from the menu, ask if they want to switch
            PresentationUtils.typewriterPrint("Would you like to switch user? (yes/no) ", 20
);
            String again = scanner.nextLine().trim();
            while (!again.equalsIgnoreCase("yes") && !again.equalsIgnoreCase("no")) {
                PresentationUtils.typewriterPrint("Invalid choice. Please enter 'yes' or 'no': ", 20
);
                again = scanner.nextLine().trim();
            }
            if (!again.equalsIgnoreCase("yes")) break;
        }
        PresentationUtils.typewriterPrint("Exiting system. Goodbye!", 20
);
    }



    public Employee getCurrentUser() {
        return currentUser;
    }

    public Role getCurrentRole() {
        return currentRole;
    }

}
