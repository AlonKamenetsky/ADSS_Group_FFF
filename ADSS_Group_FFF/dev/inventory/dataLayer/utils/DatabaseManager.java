package inventory.dataLayer.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Inventory DatabaseManager:
 *  1) Opens (and returns) a singleton java.sql.Connection to an inventory SQLite file.
 *  2) Runs all INVENTORY‐specific “CREATE TABLE IF NOT EXISTS” statements at startup.
 *
 * Usage (from anywhere in inventory code):
 *    Connection c = DatabaseManager.getInstance().getConnection();
 */
public class DatabaseManager {
    // This will create the file "inventory.db" in dev/inventory when you first connect.
    private static final String JDBC_URL = "jdbc:sqlite:data/inventoryDatabase.db";

    private static DatabaseManager instance = null;
    private final Connection connection;

    private DatabaseManager() throws SQLException {
        // 1) Load the SQLite JDBC driver (optional but safe)
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found.", e);
        }

        // 2) Open (or create) the inventory.db file
        this.connection = DriverManager.getConnection(JDBC_URL);

        // 3) Create only the inventory tables if they do not exist
        runDDL();
    }

    public void runDDL() throws SQLException {
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
                        "  startDate INTEGER NOT NULL," +   // store as UNIX timestamp (ms)
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

    /**
     * Returns the singleton DatabaseManager (initializing if necessary).
     */
    public static synchronized DatabaseManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Retrieve the shared JDBC Connection.
     * Any DAO in inventory should call:
     *   Connection c = DatabaseManager.getInstance().getConnection();
     */
    public Connection getConnection() {
        return this.connection;
    }
}
