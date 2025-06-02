package SuppliersModule.DataLayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * CsvToDatabaseImporter (corrected commit/rollback logic).
 *
 * Workflow:
 *   1) Open Connection, setAutoCommit(false)
 *   2) DELETE all rows from each table, then conn.commit()
 *   3) INSERT from CSVs, then conn.commit()
 *   4) If any exception occurs in steps 2–3, conn.rollback() to the cleared state
 *   5) Finally setAutoCommit(true) and close
 */
public class CsvToDatabaseImporter {

    // ────────────────────────────────────────────────────────────────────────────
    // === CONFIGURATION: adjust paths if needed ===
    // ────────────────────────────────────────────────────────────────────────────
    private static final String JDBC_URL = "jdbc:sqlite:data/SuppliersDatabase.db";

    private static final String PRODUCTS_CSV  = "data/products_data.csv";
    private static final String SUPPLIERS_CSV = "data/suppliers_data.csv";
    private static final String ORDERS_CSV    = "data/orders_data.csv";
    private static final String CONTRACTS_CSV = "data/contracts_data.csv";
    // ────────────────────────────────────────────────────────────────────────────


    /**
     * Public entry point: clears tables and re‐imports CSVs in a single transaction.
     * Throws Exception on any failure.
     */
    public static void importAll() throws Exception {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(JDBC_URL);
            // 1) Switch into manual‐commit mode
            conn.setAutoCommit(false);

            // 2) Clear all tables, then commit that deletion
            clearAllTables(conn);
            conn.commit();

            // 3) Import each CSV; if any insert fails, rollback below will revert to the "all cleared" state
            importProducts(conn, PRODUCTS_CSV);
            importSuppliers(conn, SUPPLIERS_CSV);
            importOrdersAndOrderProductData(conn, ORDERS_CSV);
            importContracts(conn, CONTRACTS_CSV);

            // 4) Commit all inserted rows
            conn.commit();
            System.out.println("CSV import completed successfully.");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    // Roll back to the state after clearAllTables (i.e. empty tables)
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignore) { }
            }
        }
    }

    /**
     * Deletes all rows from every table, in dependency order.
     */
    private static void clearAllTables(Connection conn) throws SQLException {
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

            System.out.println("Cleared all existing data from tables.");
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // 1) PRODUCTS
    // ────────────────────────────────────────────────────────────────────────────
    private static void importProducts(Connection conn, String csvPath) throws SQLException, IOException {
        String insertProductSql = """
            INSERT INTO products (id, name, company_name, product_category)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(insertProductSql);
             BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {

            // Skip header line
            String line = reader.readLine();

            int nextId = 1;
            while ((line = reader.readLine()) != null) {
                // CSV columns: [0]=productName, [1]=productCompanyName, [2]=productCategory
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
            System.out.println("Imported products from " + csvPath);
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // 2) SUPPLIERS
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

            int nextId = 1;
            while ((line = reader.readLine()) != null) {
                // CSV columns (exact order):
                // [0] supplyMethod
                // [1] supplierName
                // [2] productCategory
                // [3] supplierDeliveringMethod
                // [4] phoneNumber
                // [5] address
                // [6] name           ← contactName
                // [7] email
                // [8] supplierBankAccount
                // [9] supplierPaymentMethod
                String[] parts = line.split(",", -1);
                if (parts.length < 10) continue;

                String supplyMethodLiteral      = parts[0].trim();
                String supplierName             = parts[1].trim();
                String productCategoryLiteral   = parts[2].trim();
                String deliveringMethodLiteral  = parts[3].trim();
                String phoneNumber              = parts[4].trim();
                String address                  = parts[5].trim();
                String contactName              = parts[6].trim();   // <—
                String email                    = parts[7].trim();   // <—
                String bankAccount              = parts[8].trim();
                String paymentMethodLiteral     = parts[9].trim();

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
    // 3) ORDERS + ORDER_PRODUCT_DATA
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
                // CSV columns (exact order):
                // [0] supplierId
                // [1] orderDate (dd/MM/yyyy)
                // [2] supplyDate (dd/MM/yyyy)
                // [3] deliveringMethod
                // [4] supplyMethod
                // [5] totalPrice
                // [6] products (semicolon-separated "productID:quantity")
                String[] parts = line.split(",", -1);
                if (parts.length < 7) continue;

                int    supplierId       = Integer.parseInt(parts[0].trim());
                String orderDate        = parts[1].trim();
                String supplyDate       = parts[2].trim();
                String deliveringMethod = parts[3].trim();
                String supplyMethod     = parts[4].trim();
                double totalPrice       = Double.parseDouble(parts[5].trim());
                String productsList     = parts[6].trim();

                // No CSV fields for phone_number, physical_address, email_address, contact_name
                String phoneNumber      = "";
                String physicalAddress  = "";
                String emailAddress     = "";
                String contactName      = "";
                // Must match your OrderStatus enum:
                String defaultOrderStatus = "IN_PROCESS";

                // 3.A Insert into orders
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

                // 3.B Parse productsList (e.g. "1:4;16:2;7:4;16:2")
                if (!productsList.isEmpty()) {
                    String[] pairs = productsList.split(";");
                    for (String pair : pairs) {
                        String[] kv = pair.split(":", 2);
                        if (kv.length < 2) continue;

                        int productId   = Integer.parseInt(kv[0].trim());
                        int quantity    = Integer.parseInt(kv[1].trim());
                        double unitPrice = 0.0; // CSV does not supply item‐price

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
    // 4) CONTRACTS + SUPPLY_CONTRACT_PRODUCT_DATA
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
                // CSV columns (exact order):
                // [0] supplierID
                // [1] productID
                // [2] productPrice
                // [3] quantityForDiscount
                // [4] discountPercentage
                // [5] contractID
                String[] parts = line.split(",", -1);
                if (parts.length < 6) continue;

                int    supplierId       = Integer.parseInt(parts[0].trim());
                int    productId        = Integer.parseInt(parts[1].trim());
                double productPrice     = Double.parseDouble(parts[2].trim());
                int    quantityForDisc  = Integer.parseInt(parts[3].trim());
                double discountPct      = Double.parseDouble(parts[4].trim());
                int    contractId       = Integer.parseInt(parts[5].trim());

                // 4.A Insert into supply_contracts once per contractId
                if (!seenContracts.contains(contractId)) {
                    psContract.setInt(1, contractId);
                    psContract.setInt(2, supplierId);
                    psContract.executeUpdate();
                    seenContracts.add(contractId);
                }

                // 4.B Insert into supply_contract_product_data
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

    /**
     * main() delegates to importAll() so you can run this class on its own:
     *   java SuppliersModule.DataLayer.CsvToDatabaseImporter
     */
    public static void main(String[] args) {
        try {
            importAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
