package HR.Service;

import HR.DataAccess.EmployeeDAO;
import HR.DataAccess.EmployeeDAOImpl;
import HR.Domain.Employee;
import HR.Domain.User;
import Util.Database;

import java.sql.Connection;
import java.util.List;

public class UserService {
    private static UserService instance;
    private final EmployeeDAO employeeDAO;

    private UserService() {
        Connection conn = Database.getConnection();
        this.employeeDAO = new EmployeeDAOImpl(conn);
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    /**
     * Authenticate user by ID and password. Returns User if valid, or null if not.
     */
    public User authenticate(String id, String password) {
        Employee user = employeeDAO.selectById(id);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
