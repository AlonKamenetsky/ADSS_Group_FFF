package SuppliersModule.DataLayer;


import java.sql.Connection;
import java.sql.DriverManager;

public abstract class DbController {
    private static final String DB_NAME = "Database.db";
    private static final String DB_URL = "jdbc:sqlite:";
    protected Connection connection;

    public DbController() {
        try {
            String dbFilePath = getClass().getClassLoader().getResource(DB_NAME).getPath().replace("%20", " ");
            this.connection = DriverManager.getConnection(DB_URL + dbFilePath);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void CreateDBFile() {

    }
}
