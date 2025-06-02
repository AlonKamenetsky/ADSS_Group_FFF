package SuppliersModule.DataLayer;

import java.sql.Connection;
import java.sql.DriverManager;

public abstract class DbController {
    private static final String DB_NAME = "SuppliersDatabase.db";
    private static final String DB_URL = "jdbc:sqlite:";

    protected Connection connection;

    public DbController() {
        try {
            this.connection = DriverManager.getConnection(DB_URL + DB_NAME);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
