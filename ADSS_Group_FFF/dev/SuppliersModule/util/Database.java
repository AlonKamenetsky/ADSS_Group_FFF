package SuppliersModule.util;

import java.io.File;
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
            new File("data").mkdirs();
            conn = DriverManager.getConnection(DB_URL);
            log.info("Connected to SQLite at " + DB_URL);
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS products (
                                id INTEGER PRIMARY KEY,
                                name TEXT NOT NULL,
                                company_name TEXT,
                                producs_category TEXT
                            );
                        """);
                // Create orders table
                stmt.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS orders (
                                id INTEGER PRIMARY KEY,
                                supplier_id INTEGER NOT NULL,
                                contact_info_id INTEGER,
                                delivering_method TEXT NOT NULL,
                                order_date TEXT NOT NULL,
                                supply_date TEXT NOT NULL,
                                total_price REAL NOT NULL,
                                supply_method TEXT NOT NULL,
                                order_status TEXT NOT NULL,
                                FOREIGN KEY (contact_info_id) REFERENCES contact_info(id),
                                FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
                            );
                        """);

                // Create order_product_data table
                stmt.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS order_product_data (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                order_id INTEGER NOT NULL,
                                product_id INTEGER NOT NULL,
                                quantity INTEGER NOT NULL,
                                price REAL NOT NULL,
                                FOREIGN KEY (order_id) REFERENCES orders(id),
                                FOREIGN KEY (product_id) REFERENCES products(id)
                            );
                        """);
                stmt.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS contact_info (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                name TEXT NOT NULL,
                                phone_number TEXT,
                                address TEXT,
                                email TEXT
                            );
                        """);
                stmt.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS payment_info (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                bank_account TEXT NOT NULL,
                                payment_method TEXT NOT NULL  -- store enum name as string
                            );
                        """);
                stmt.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS supply_contracts (
                                id INTEGER PRIMARY KEY,
                                supplier_id INTEGER NOT NULL,
                                FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
                            );
                        """);
                stmt.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS suppliers (
                                id INTEGER PRIMARY KEY,
                                name TEXT NOT NULL,
                                product_category TEXT NOT NULL,
                                delivering_method TEXT NOT NULL,
                                contact_info_id INTEGER NOT NULL,
                                payment_info_id INTEGER NOT NULL,
                        
                                FOREIGN KEY (contact_info_id) REFERENCES contact_info(id),
                                FOREIGN KEY (payment_info_id) REFERENCES payment_info(id)
                            );
                        """);


            } catch (SQLException e) {


            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Failed to connect to the database", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        return conn;
    }
}
