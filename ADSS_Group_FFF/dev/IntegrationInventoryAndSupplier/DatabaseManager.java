package IntegrationInventoryAndSupplier;

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
    private static final String JDBC_URL = "jdbc:sqlite:integratedDatabase.db";
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







//-------------------------------------------------------------------------------------------
// This should work but doesnt:
//package IntegrationInventoryAndSupplier;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.sql.Statement;
//
///**
// * DatabaseManager is responsible for:
// *  1) Opening (and returning) a singleton java.sql.Connection to a local SQLite file.
// *  2) Running all “CREATE TABLE IF NOT EXISTS” statements (Inventory + Supplier) at startup.
// *
// * Usage:
// *    Connection conn = DatabaseManager.getInstance().getConnection();
// */
//public class DatabaseManager {
//    private static final String JDBC_URL = "jdbc:sqlite:integratedDatabase.db";
//    private static DatabaseManager instance = null;
//    private final Connection connection;
//
//    private DatabaseManager() throws SQLException {
//        // 1) Load the SQLite JDBC driver (optional, but safe)
//        try {
//            Class.forName("org.sqlite.JDBC");
//        } catch (ClassNotFoundException e) {
//            throw new SQLException("SQLite JDBC driver not found.", e);
//        }
//
//        // 2) Open (or create) the unified SQLite file
//        this.connection = DriverManager.getConnection(JDBC_URL);
//
//        // 3) Create all tables (Inventory + Supplier) if they do not exist
//        runDDL();
//    }
//
//    private void runDDL() throws SQLException {
//        try (Statement stmt = connection.createStatement()) {
//            // ─── INVENTORY MODULE TABLES ──────────────────────────────────────
//
//            String createCategoryTable =
//                    "CREATE TABLE IF NOT EXISTS Category (" +
//                            "  name TEXT PRIMARY KEY," +
//                            "  parent_name TEXT," +
//                            "  FOREIGN KEY(parent_name) REFERENCES Category(name)" +
//                            ");";
//            stmt.execute(createCategoryTable);
//
//            String createProductTable =
//                    "CREATE TABLE IF NOT EXISTS InventoryProduct (" +
//                            "  id INTEGER PRIMARY KEY," +
//                            "  name TEXT NOT NULL," +
//                            "  manufacturer TEXT," +
//                            "  shelfQuantity INTEGER NOT NULL," +
//                            "  backroomQuantity INTEGER NOT NULL," +
//                            "  minThreshold INTEGER NOT NULL," +
//                            "  purchasePrice REAL NOT NULL," +
//                            "  salePrice REAL NOT NULL," +
//                            "  status TEXT NOT NULL," +
//                            "  category_name TEXT," +
//                            "  FOREIGN KEY(category_name) REFERENCES Category(name)" +
//                            ");";
//            stmt.execute(createProductTable);
//
//            String createDiscountTable =
//                    "CREATE TABLE IF NOT EXISTS Discount (" +
//                            "  id TEXT PRIMARY KEY," +
//                            "  percent REAL NOT NULL," +
//                            "  startDate INTEGER NOT NULL," +    // store as UNIX timestamp (ms)
//                            "  endDate INTEGER NOT NULL," +
//                            "  appliesToCategory TEXT," +
//                            "  appliesToItemId INTEGER," +
//                            "  FOREIGN KEY(appliesToCategory) REFERENCES Category(name)," +
//                            "  FOREIGN KEY(appliesToItemId) REFERENCES InventoryProduct(id)" +
//                            ");";
//            stmt.execute(createDiscountTable);
//
//            String createReportTable =
//                    "CREATE TABLE IF NOT EXISTS InventoryReport (" +
//                            "  id TEXT PRIMARY KEY," +
//                            "  dateGenerated INTEGER NOT NULL" +  // store as UNIX timestamp (ms)
//                            ");";
//            stmt.execute(createReportTable);
//
//            String createReportItemsTable =
//                    "CREATE TABLE IF NOT EXISTS InventoryReportItems (" +
//                            "  report_id TEXT NOT NULL," +
//                            "  product_id INTEGER NOT NULL," +
//                            "  FOREIGN KEY(report_id) REFERENCES InventoryReport(id)," +
//                            "  FOREIGN KEY(product_id) REFERENCES InventoryProduct(id)" +
//                            ");";
//            stmt.execute(createReportItemsTable);
//
//            // ─── SUPPLIERS MODULE TABLES ─────────────────────────────────────
//
//            String createProductsTable =
//                    "CREATE TABLE IF NOT EXISTS products (" +
//                            "  id INTEGER PRIMARY KEY," +
//                            "  name TEXT NOT NULL," +
//                            "  company_name TEXT," +
//                            "  producs_category TEXT" +
//                            ");";
//            stmt.execute(createProductsTable);
//
//            String createOrdersTable =
//                    "CREATE TABLE IF NOT EXISTS orders (" +
//                            "  id INTEGER PRIMARY KEY," +
//                            "  supplier_id INTEGER NOT NULL," +
//                            "  delivering_method TEXT NOT NULL," +
//                            "  order_date TEXT NOT NULL," +
//                            "  supply_date TEXT NOT NULL," +
//                            "  total_price REAL NOT NULL," +
//                            "  supply_method TEXT NOT NULL," +
//                            "  order_status TEXT NOT NULL," +
//                            // Removed the bogus contact_info_id FK line:
//                            // "  FOREIGN KEY (contact_info_id) REFERENCES contact_info(id)," +
//                            "  FOREIGN KEY (supplier_id) REFERENCES suppliers(id)" +
//                            ");";
//            stmt.execute(createOrdersTable);
//
//            String createOrderProductDataTable =
//                    "CREATE TABLE IF NOT EXISTS order_product_data (" +
//                            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                            "  order_id INTEGER NOT NULL," +
//                            "  product_id INTEGER NOT NULL," +
//                            "  quantity INTEGER NOT NULL," +
//                            "  price REAL NOT NULL," +
//                            "  FOREIGN KEY (order_id) REFERENCES orders(id)," +
//                            "  FOREIGN KEY (product_id) REFERENCES products(id)" +
//                            ");";
//            stmt.execute(createOrderProductDataTable);
//
//            String createSuppliersTable =
//                    "CREATE TABLE IF NOT EXISTS suppliers (" +
//                            "  id INTEGER PRIMARY KEY," +
//                            "  name TEXT NOT NULL," +
//                            "  product_category TEXT NOT NULL," +
//                            "  delivering_method TEXT NOT NULL," +
//                            "  contact_name TEXT NOT NULL," +
//                            "  phone_number TEXT NOT NULL," +
//                            "  address TEXT NOT NULL," +
//                            "  email TEXT NOT NULL," +
//                            "  bank_account TEXT NOT NULL," +
//                            "  payment_method TEXT NOT NULL," +  // enum stored as string
//                            "  supply_method TEXT NOT NULL" +
//                            ");";
//            stmt.execute(createSuppliersTable);
//
//            String createSuppliersDaysTable =
//                    "CREATE TABLE IF NOT EXISTS suppliers_days (" +
//                            "  id INTEGER," +
//                            "  day TEXT NOT NULL" +
//                            ");";
//            stmt.execute(createSuppliersDaysTable);
//
//            String createSupplyContractsTable =
//                    "CREATE TABLE IF NOT EXISTS supply_contracts (" +
//                            "  id INTEGER PRIMARY KEY," +
//                            "  supplier_id INTEGER NOT NULL," +
//                            "  FOREIGN KEY (supplier_id) REFERENCES suppliers(id)" +
//                            ");";
//            stmt.execute(createSupplyContractsTable);
//
//            String createSupplyContractProductDataTable =
//                    "CREATE TABLE IF NOT EXISTS supply_contract_product_data (" +
//                            "  contract_id INTEGER NOT NULL," +
//                            "  product_id INTEGER NOT NULL," +
//                            "  product_price REAL NOT NULL," +
//                            "  quantity_for_discount INTEGER NOT NULL," +
//                            "  discount_percentage REAL NOT NULL," +
//                            "  FOREIGN KEY (contract_id) REFERENCES supply_contracts(id)" +
//                            ");";
//            stmt.execute(createSupplyContractProductDataTable);
//
//            // (If you need more Supplier tables—e.g. contact_info—you must define them here
//            //  before any FK references to contact_info.)
//        }
//    }
//
//    /**
//     * Returns the singleton DatabaseManager instance (creating it and its tables if needed).
//     */
//    public static synchronized DatabaseManager getInstance() throws SQLException {
//        if (instance == null) {
//            instance = new DatabaseManager();
//        }
//        return instance;
//    }
//
//    /**
//     * Retrieve the shared JDBC Connection. Every DAO (Inventory or Supplier) should call:
//     *    Connection conn = DatabaseManager.getInstance().getConnection();
//     */
//    public Connection getConnection() {
//        return this.connection;
//    }
//}


