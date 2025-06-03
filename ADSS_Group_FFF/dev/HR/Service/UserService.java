package HR.Service;

import HR.DataAccess.EmployeeDAO;
import HR.Domain.Employee;
import HR.Domain.User;

public class UserService {
    private final EmployeeDAO employeeDAO;

    public UserService(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    /**
     * Authenticate user by ID and password. Returns User if valid (password matches),
     * or null otherwise.
     */
    public User authenticate(String id, String password) {
        if (id == null || id.isBlank() || password == null) {
            return null;
        }

        Employee user = employeeDAO.selectById(id);
        if (user != null && password.equals(user.getPassword())) {
            return user;
        }
        return null;
    }
}
