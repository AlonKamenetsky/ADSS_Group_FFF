package inventory.dataLayer.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseManager is responsible for:
 *  1) Opening (and returning) a singleton java.sql.Connection to a local SQLite file.
 *  2) Running any “CREATE TABLE IF NOT EXISTS” statements at startup.
 *
 * Usage:
 *    Connection conn = DatabaseManager.getInstance().getConnection();
 */
public class DatabaseManager {
    private static final String JDBC_URL = "jdbc:sqlite:inventory.db";
    private static DatabaseManager instance = null;
    private final Connection connection;

    private DatabaseManager() throws SQLException {
        // 1) Load the SQLite JDBC driver (optional, since recent JDBC does this automatically):
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            // If the driver class is not found, you must add the SQLite JDBC JAR to classpath.
            throw new SQLException("SQLite JDBC driver not found.", e);
        }

        // 2) Establish connection (will create the file if it doesn't exist).
        this.connection = DriverManager.getConnection(JDBC_URL);

        // 3) Create tables if they do not exist:
        runDDL();
    }

    private void runDDL() throws SQLException {
        String createCategoryTable =
                "CREATE TABLE IF NOT EXISTS Category (" +
                        "  name TEXT PRIMARY KEY," +
                        "  parent_name TEXT," +
                        "  FOREIGN KEY(parent_name) REFERENCES Category(name)" +
                        ");";

        String createProductTable =
                "CREATE TABLE IF NOT EXISTS InventoryProduct (" +
                        "  id INTEGER PRIMARY KEY," +
                        "  name TEXT NOT NULL," +
                        "  manufacturer TEXT," +
                        "  shelfQuantity INTEGER NOT NULL," +
                        "  backroomQuantity INTEGER NOT NULL," +
                        "  minThreshold INTEGER NOT NULL," +
                        "  purchasePrice REAL NOT NULL," +
                        "  salePrice REAL NOT NULL," +
                        "  status TEXT NOT NULL," +
                        "  category_name TEXT," +
                        "  FOREIGN KEY(category_name) REFERENCES Category(name)" +
                        ");";

        String createDiscountTable =
                "CREATE TABLE IF NOT EXISTS Discount (" +
                        "  id TEXT PRIMARY KEY," +
                        "  percent REAL NOT NULL," +
                        "  startDate INTEGER NOT NULL," +    // store as UNIX timestamp (ms)
                        "  endDate INTEGER NOT NULL," +
                        "  appliesToCategory TEXT," +
                        "  appliesToItemId INTEGER," +
                        "  FOREIGN KEY(appliesToCategory) REFERENCES Category(name)," +
                        "  FOREIGN KEY(appliesToItemId) REFERENCES InventoryProduct(id)" +
                        ");";

        String createReportTable =
                "CREATE TABLE IF NOT EXISTS InventoryReport (" +
                        "  id TEXT PRIMARY KEY," +
                        "  dateGenerated INTEGER NOT NULL" +  // store as UNIX timestamp (ms)
                        ");";

        String createReportItemsTable =
                "CREATE TABLE IF NOT EXISTS InventoryReportItems (" +
                        "  report_id TEXT NOT NULL," +
                        "  product_id INTEGER NOT NULL," +
                        "  FOREIGN KEY(report_id) REFERENCES InventoryReport(id)," +
                        "  FOREIGN KEY(product_id) REFERENCES InventoryProduct(id)" +
                        ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createCategoryTable);
            stmt.execute(createProductTable);
            stmt.execute(createDiscountTable);
            stmt.execute(createReportTable);
            stmt.execute(createReportItemsTable);
        }
    }

    public static DatabaseManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return this.connection;
    }
}
