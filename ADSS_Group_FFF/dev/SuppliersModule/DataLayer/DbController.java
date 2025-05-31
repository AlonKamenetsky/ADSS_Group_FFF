package SuppliersModule.DataLayer;


import java.sql.Connection;
import java.sql.DriverManager;

public abstract class DbController {
    private static final String DB_NAME = "SuppliersDatabase.db";
    private static final String DB_URL = "jdbc:sqlite:";
    protected Connection connection;

    public DbController() {
        try {
            // SHOULD BE CHANGED DEPENDS ON THE COMPUTER!!!!
            // TRY TO JUST DELETE THE REPLACE METHOD
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
