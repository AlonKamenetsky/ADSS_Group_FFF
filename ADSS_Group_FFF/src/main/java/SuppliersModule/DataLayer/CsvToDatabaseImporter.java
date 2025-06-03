package SuppliersModule.DataLayer;

import inventory.dataLayer.utils.DatabaseManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * CsvToDatabaseImporter (enhanced: also clears & rebuilds inventoryDatabase.db,
 * then populates its first 10 InventoryProduct rows from products_data.csv).
 *
 * Workflow:
 *   0) Connect to inventoryDatabase.db, drop all user tables, commit, then run DDL to recreate.
 *   0a) After DDL, insert the first 10 products from products_data.csv into (Category + InventoryProduct).
 *   1) Connect to SuppliersDatabase.db, setAutoCommit(false).
 *   2) DELETE all rows from each Suppliers table, then conn.commit().
 *   3) INSERT from Suppliers CSVs, then conn.commit().
 *   4) If any exception occurs in steps 1–3, conn.rollback() to the cleared suppliers state.
 *   5) Finally setAutoCommit(true) and close SuppliersConnection.
 */
public class CsvToDatabaseImporter {

    // ────────────────────────────────────────────────────────────────────────────
    // === CONFIGURATION: adjust paths if needed ===
    // ────────────────────────────────────────────────────────────────────────────
    private static final String SUPPLIERS_JDBC_URL    = "jdbc:sqlite:data/SuppliersDatabase.db";
    private static final String INVENTORY_JDBC_URL    = "jdbc:sqlite:data/inventoryDatabase.db";

    private static final String PRODUCTS_CSV          = "data/products_data.csv";
    private static final String SUPPLIERS_CSV         = "data/suppliers_data.csv";
    private static final String ORDERS_CSV            = "data/orders_data.csv";
    private static final String CONTRACTS_CSV         = "data/contracts_data.csv";
    // ────────────────────────────────────────────────────────────────────────────


    /**
     * Public entry point: First clears & rebuilds inventoryDatabase.db,
     * populates its first 10 products, then clears & re‐imports SuppliersDatabase.db from CSV.
     */
    public static void importAll() throws Exception {
        // ────────────────────────────────────────────────────────────────────────────
        // 0) CLEAR & REBUILD INVENTORY DATABASE
        // ────────────────────────────────────────────────────────────────────────────
        try (Connection invConn = DriverManager.getConnection(INVENTORY_JDBC_URL)) {
            invConn.setAutoCommit(false);

            // Drop every user‐defined table in inventoryDatabase.db
            dropAllUserTables(invConn);
            invConn.commit();
            System.out.println("Dropped all tables in inventoryDatabase.db");

            // Recreate tables by running DDL through DatabaseManager
            DatabaseManager.getInstance().runDDL();
            invConn.commit();
            System.out.println("Recreated all tables in inventoryDatabase.db");

            // 0a) Insert the first 10 products (and their categories) from products_data.csv
            populateFirstTenProducts(invConn, PRODUCTS_CSV);
            invConn.commit();
            System.out.println("Inserted first 10 products into inventoryDatabase.db");
        }


        // ────────────────────────────────────────────────────────────────────────────
        // 1) CLEAR & IMPORT SUPPLIERS DATABASE
        // ────────────────────────────────────────────────────────────────────────────
        Connection supConn = null;
        try {
            supConn = DriverManager.getConnection(SUPPLIERS_JDBC_URL);
            supConn.setAutoCommit(false);

            // 1.A) Clear all Suppliers tables, then commit
            clearAllSuppliersTables(supConn);
            supConn.commit();

            // 1.B) Import from CSVs
            importProducts(supConn, PRODUCTS_CSV);
            importSuppliers(supConn, SUPPLIERS_CSV);
            importOrdersAndOrderProductData(supConn, ORDERS_CSV);
            importContracts(supConn, CONTRACTS_CSV);

            // 1.C) Commit all inserts
            supConn.commit();
            System.out.println("CSV import completed successfully.");

        } catch (Exception e) {
            if (supConn != null) {
                try {
                    // Roll back to the “cleared tables” state
                    supConn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (supConn != null) {
                try {
                    supConn.setAutoCommit(true);
                    supConn.close();
                } catch (SQLException ignore) { }
            }
        }
    }

    /**
     * Drops every user‐defined table in the given connection.
     * Ignores internal sqlite_ tables.
     */
    private static void dropAllUserTables(Connection conn) throws SQLException {
        Set<String> tableNames = new HashSet<>();
        // 1) Find all user tables (skip sqlite_ internal tables)
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%';"
             )) {
            while (rs.next()) {
                tableNames.add(rs.getString("name"));
            }
        }

        // 2) Drop each
        try (Statement stmt = conn.createStatement()) {
            for (String tbl : tableNames) {
                stmt.executeUpdate("DROP TABLE IF EXISTS \"" + tbl + "\";");
            }
        }
    }

    /**
     * Reads the first ten data rows from products_data.csv and inserts them into:
     *   1) Category (if not already present)
     *   2) InventoryProduct
     *
     * Uses GENERATED dummy values for quantities/prices/status:
     *   shelfQuantity = 10, backroomQuantity = 5, minThreshold = 3,
     *   purchasePrice = 1.00, salePrice = 1.50, status = "OK".
     *
     * @param conn     An open Connection to inventoryDatabase.db (in autoCommit = false mode).
     * @param csvPath  Path to the products_data.csv file.
     */
    private static void populateFirstTenProducts(Connection conn, String csvPath)
            throws SQLException, IOException
    {
        // 1) Ensure we have a prepared statement for inserting into Category:
        String insertCategorySql = "INSERT OR IGNORE INTO Category(name, parent_name) VALUES (?, NULL)";
        PreparedStatement catStmt = conn.prepareStatement(insertCategorySql);

        // 2) Prepare statement for inserting into InventoryProduct:
        String insertProductSql = """
            INSERT INTO InventoryProduct (
                id, name, manufacturer,
                shelfQuantity, backroomQuantity, minThreshold,
                purchasePrice, salePrice, status, category_name
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        PreparedStatement prodStmt = conn.prepareStatement(insertProductSql);

        // 3) Read the CSV, skip header, insert first 10 lines
        try (BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {
            String header = reader.readLine(); // skip header
            String line;
            int count = 0;

            while (count < 10 && (line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 3) {
                    continue; // skip malformed lines
                }

                // CSV columns: [0] = productName, [1] = productCompanyName, [2] = productCategory
                String name           = parts[0].trim();
                String manufacturer   = parts[1].trim();
                String categoryName   = parts[2].trim();

                // Parse the CSV-ordered ID: we assume the CSV’s first data row corresponds to ID = 0,
                // second row ID = 1, etc. (or change logic if IDs are actually stored explicitly).
                // Here we simply use 'count' as the ID. If your CSV has explicit IDs, parse that instead.
                int id = count;

                // 3.A) Insert category (if not already present)
                catStmt.setString(1, categoryName);
                catStmt.executeUpdate();

                // 3.B) Insert InventoryProduct with dummy values:
                prodStmt.setInt    (1, id);
                prodStmt.setString (2, name);
                prodStmt.setString (3, manufacturer);
                prodStmt.setInt    (4, 10);       // shelfQuantity
                prodStmt.setInt    (5, 5);        // backroomQuantity
                prodStmt.setInt    (6, 3);        // minThreshold
                prodStmt.setDouble (7, 1.00);     // purchasePrice
                prodStmt.setDouble (8, 1.50);     // salePrice
                prodStmt.setString (9, "DAMAGED");     // status
                prodStmt.setString (10, categoryName);
                prodStmt.executeUpdate();

                count++;
            }
        } finally {
            catStmt.close();
            prodStmt.close();
        }
    }

    /**
     * Deletes all rows from each Suppliers‐related table, in dependency order.
     */
    private static void clearAllSuppliersTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // 1) order_product_data references orders
            stmt.executeUpdate("DELETE FROM order_product_data");
            // 2) orders references suppliers
            stmt.executeUpdate("DELETE FROM orders");
            // 3) supply_contract_product_data references supply_contracts
            stmt.executeUpdate("DELETE FROM supply_contract_product_data");
            // 4) supply_contracts references suppliers
            stmt.executeUpdate("DELETE FROM supply_contracts");
            // 5) suppliers
            stmt.executeUpdate("DELETE FROM suppliers");
            // 6) products
            stmt.executeUpdate("DELETE FROM products");
            System.out.println("Cleared all existing data from Suppliers tables.");
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // 1) SUPPLIERS: PRODUCTS
    // ────────────────────────────────────────────────────────────────────────────
    private static void importProducts(Connection conn, String csvPath) throws SQLException, IOException {
        String insertProductSql = """
            INSERT INTO products (id, name, company_name, product_category)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(insertProductSql);
             BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {

            // Skip header
            String line = reader.readLine();
            int nextId = 0;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 3) continue;

                String name            = parts[0].trim();
                String companyName     = parts[1].trim();
                String categoryLiteral = parts[2].trim();

                ps.setInt   (1, nextId);
                ps.setString(2, name);
                ps.setString(3, companyName);
                ps.setString(4, categoryLiteral);
                ps.executeUpdate();

                nextId++;
            }
            System.out.println("Imported suppliers’ products from " + csvPath);
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // 2) SUPPLIERS: SUPPLIERS
    // ────────────────────────────────────────────────────────────────────────────
    private static void importSuppliers(Connection conn, String csvPath) throws SQLException, IOException {
        String insertSupplierSql = """
            INSERT INTO suppliers (
                id, name, product_category,
                contact_name, phone_number, email, address,
                delivery_method, bank_account, payment_method, supply_method
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(insertSupplierSql);
             BufferedReader reader = new BufferedReader(
                     new FileReader(new File(csvPath), java.nio.charset.StandardCharsets.ISO_8859_1)
             )) {
            // Skip header
            String line = reader.readLine();
            int nextId = 0;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 10) continue;

                String supplyMethodLiteral     = parts[0].trim();
                String supplierName            = parts[1].trim();
                String productCategoryLiteral  = parts[2].trim();
                String deliveringMethodLiteral = parts[3].trim();
                String phoneNumber             = parts[4].trim();
                String address                 = parts[5].trim();
                String contactName             = parts[6].trim();
                String email                   = parts[7].trim();
                String bankAccount             = parts[8].trim();
                String paymentMethodLiteral    = parts[9].trim();

                ps.setInt   (1, nextId);
                ps.setString(2, supplierName);
                ps.setString(3, productCategoryLiteral);
                ps.setString(4, contactName);
                ps.setString(5, phoneNumber);
                ps.setString(6, email);
                ps.setString(7, address);
                ps.setString(8, deliveringMethodLiteral);
                ps.setString(9, bankAccount);
                ps.setString(10, paymentMethodLiteral);
                ps.setString(11, supplyMethodLiteral);
                ps.executeUpdate();

                nextId++;
            }
            System.out.println("Imported suppliers from " + csvPath);
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // 3) SUPPLIERS: ORDERS + ORDER_PRODUCT_DATA
    // ────────────────────────────────────────────────────────────────────────────
    private static void importOrdersAndOrderProductData(Connection conn, String csvPath) throws SQLException, IOException {
        String insertOrderSql = """
            INSERT INTO orders (
                id, supplier_id, phone_number, physical_address,
                email_address, contact_name, delivery_method,
                order_date, delivery_date, total_price,
                order_status, supply_method
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        String insertOrderProductSql = """
            INSERT INTO order_product_data (
                order_id, product_id, product_quantity, product_price
            ) VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement psOrder       = conn.prepareStatement(insertOrderSql);
             PreparedStatement psOrderProduct = conn.prepareStatement(insertOrderProductSql);
             BufferedReader reader            = new BufferedReader(new FileReader(csvPath))) {

            // Skip header
            reader.readLine();

            int nextOrderId = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 7) continue;

                int    supplierId       = Integer.parseInt(parts[0].trim());
                String orderDate        = parts[1].trim();
                String supplyDate       = parts[2].trim();
                String deliveringMethod = parts[3].trim();
                String supplyMethod     = parts[4].trim();
                double totalPrice       = Double.parseDouble(parts[5].trim());
                String productsList     = parts[6].trim();

                String phoneNumber      = "";
                String physicalAddress  = "";
                String emailAddress     = "";
                String contactName      = "";
                String defaultOrderStatus = "IN_PROCESS";

                // Insert into orders
                psOrder.setInt   (1, nextOrderId);
                psOrder.setInt   (2, supplierId);
                psOrder.setString(3, phoneNumber);
                psOrder.setString(4, physicalAddress);
                psOrder.setString(5, emailAddress);
                psOrder.setString(6, contactName);
                psOrder.setString(7, deliveringMethod);
                psOrder.setString(8, orderDate);
                psOrder.setString(9, supplyDate);
                psOrder.setDouble(10, totalPrice);
                psOrder.setString(11, defaultOrderStatus);
                psOrder.setString(12, supplyMethod);
                psOrder.executeUpdate();

                // Insert order_product_data
                if (!productsList.isEmpty()) {
                    String[] pairs = productsList.split(";");
                    for (String pair : pairs) {
                        String[] kv = pair.split(":", 2);
                        if (kv.length < 2) continue;

                        int productId   = Integer.parseInt(kv[0].trim());
                        int quantity    = Integer.parseInt(kv[1].trim());
                        double unitPrice = 0.0;

                        psOrderProduct.setInt   (1, nextOrderId);
                        psOrderProduct.setInt   (2, productId);
                        psOrderProduct.setInt   (3, quantity);
                        psOrderProduct.setDouble(4, unitPrice);
                        psOrderProduct.executeUpdate();
                    }
                }
                nextOrderId++;
            }
            System.out.println("Imported orders (and order_product_data) from " + csvPath);
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // 4) SUPPLIERS: CONTRACTS + SUPPLY_CONTRACT_PRODUCT_DATA
    // ────────────────────────────────────────────────────────────────────────────
    private static void importContracts(Connection conn, String csvPath) throws SQLException, IOException {
        String insertContractSql = """
            INSERT INTO supply_contracts (id, supplier_id)
            VALUES (?, ?)
        """;

        String insertContractProductSql = """
            INSERT INTO supply_contract_product_data (
                contract_id, product_id, product_price,
                quantity_for_discount, discount_percentage
            ) VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement psContract        = conn.prepareStatement(insertContractSql);
             PreparedStatement psContractProduct = conn.prepareStatement(insertContractProductSql);
             BufferedReader reader               = new BufferedReader(new FileReader(csvPath))) {

            // Skip header
            reader.readLine();

            Set<Integer> seenContracts = new HashSet<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 6) continue;

                int    supplierId       = Integer.parseInt(parts[0].trim());
                int    productId        = Integer.parseInt(parts[1].trim());
                double productPrice     = Double.parseDouble(parts[2].trim());
                int    quantityForDisc  = Integer.parseInt(parts[3].trim());
                double discountPct      = Double.parseDouble(parts[4].trim());
                int    contractId       = Integer.parseInt(parts[5].trim());

                // Insert into supply_contracts if not done already
                if (!seenContracts.contains(contractId)) {
                    psContract.setInt(1, contractId);
                    psContract.setInt(2, supplierId);
                    psContract.executeUpdate();
                    seenContracts.add(contractId);
                }

                // Insert into supply_contract_product_data
                psContractProduct.setInt   (1, contractId);
                psContractProduct.setInt   (2, productId);
                psContractProduct.setDouble(3, productPrice);
                psContractProduct.setInt   (4, quantityForDisc);
                psContractProduct.setDouble(5, discountPct);
                psContractProduct.executeUpdate();
            }
            System.out.println("Imported contracts (and supply_contract_product_data) from " + csvPath);
        }
    }

}
