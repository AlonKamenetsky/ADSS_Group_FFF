package SuppliersModule.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Database {
    private static final Logger log = Logger.getLogger(Database.class.getName());
    private static final String DB_URL = "jdbc:sqlite:data/suppliers.db";
    private static Connection conn;

    static {
        try {
            conn = DriverManager.getConnection(DB_URL);
            log.info("Connected to SQLite at " + DB_URL);
            try(Statement stmt = conn.createStatement()){

            }catch (SQLException e){


            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Failed to connect to the database", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private Database() {}

    public static Connection getConnection() throws SQLException {
        return conn;
    }
}
