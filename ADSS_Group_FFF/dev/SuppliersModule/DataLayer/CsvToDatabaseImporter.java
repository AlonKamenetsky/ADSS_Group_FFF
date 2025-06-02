package SuppliersModule.DataLayer;

import inventory.dataLayer.utils.DatabaseManager;

import java.io.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class CsvToDatabaseImporter {

    private static final String INVENTORY_JDBC_URL  = "jdbc:sqlite:data/inventoryDatabase.db";
    private static final String SUPPLIERS_JDBC_URL  = "jdbc:sqlite:data/SuppliersDatabase.db";

    private static final String PRODUCTS_CSV      = "data/products_data.csv";
    private static final String SUPPLIERS_CSV     = "data/suppliers_data.csv";
    private static final String ORDERS_CSV        = "data/orders_data.csv";
    private static final String CONTRACTS_CSV     = "data/contracts_data.csv";

    public static void importAll() throws Exception {
        try (Connection invConn = DriverManager.getConnection(INVENTORY_JDBC_URL)) {
            invConn.setAutoCommit(false);
            dropAllUserTables(invConn);
            invConn.commit();
            System.out.println("Dropped all tables in inventoryDatabase.db");
            DatabaseManager.getInstance().runDDL();
            System.out.println("Recreated all tables in inventoryDatabase.db");
        }

        Connection suppliersConn = null;
        try {
            suppliersConn = DriverManager.getConnection(SUPPLIERS_JDBC_URL);
            suppliersConn.setAutoCommit(false);

            clearAllSuppliersTables(suppliersConn);
            suppliersConn.commit();

            importProducts(suppliersConn, PRODUCTS_CSV);
            importSuppliers(suppliersConn, SUPPLIERS_CSV);
            importOrdersAndOrderProductData(suppliersConn, ORDERS_CSV);
            importContracts(suppliersConn, CONTRACTS_CSV);

            suppliersConn.commit();
            System.out.println("Suppliers CSV import completed successfully.");

        } catch (Exception e) {
            if (suppliersConn != null) suppliersConn.rollback();
            throw e;
        } finally {
            if (suppliersConn != null) {
                suppliersConn.setAutoCommit(true);
                suppliersConn.close();
            }
        }
    }

    private static void dropAllUserTables(Connection conn) throws SQLException {
        Set<String> tableNames = new HashSet<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%';")) {
            while (rs.next()) {
                tableNames.add(rs.getString("name"));
            }
        }
        try (Statement stmt = conn.createStatement()) {
            for (String tbl : tableNames) {
                stmt.executeUpdate("DROP TABLE IF EXISTS \"" + tbl + "\";");
            }
        }
    }

    private static void clearAllSuppliersTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM order_product_data");
            stmt.executeUpdate("DELETE FROM orders");
            stmt.executeUpdate("DELETE FROM supply_contract_product_data");
            stmt.executeUpdate("DELETE FROM supply_contracts");
            stmt.executeUpdate("DELETE FROM suppliers");
            stmt.executeUpdate("DELETE FROM products");
            System.out.println("Cleared all existing data from Suppliers tables.");
        }
    }

    private static void importProducts(Connection conn, String csvPath) throws SQLException, IOException {
        String sql = "INSERT INTO products (name, company_name, product_category) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {

            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 3) continue;
                ps.setString(1, parts[0].trim());
                ps.setString(2, parts[1].trim());
                ps.setString(3, parts[2].trim());
                ps.executeUpdate();
            }
            System.out.println("Imported products from " + csvPath);
        }
    }

    private static void importSuppliers(Connection conn, String csvPath) throws SQLException, IOException {
        String sql = """
            INSERT INTO suppliers (
                name, product_category, contact_name, phone_number, email,
                address, delivery_method, bank_account, payment_method, supply_method
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             BufferedReader reader = new BufferedReader(new InputStreamReader(
                     new FileInputStream(csvPath), java.nio.charset.StandardCharsets.ISO_8859_1))) {

            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 10) continue;
                ps.setString(1, parts[1].trim());
                ps.setString(2, parts[2].trim());
                ps.setString(3, parts[6].trim());
                ps.setString(4, parts[4].trim());
                ps.setString(5, parts[7].trim());
                ps.setString(6, parts[5].trim());
                ps.setString(7, parts[3].trim());
                ps.setString(8, parts[8].trim());
                ps.setString(9, parts[9].trim());
                ps.setString(10, parts[0].trim());
                ps.executeUpdate();
            }
            System.out.println("Imported suppliers from " + csvPath);
        }
    }

    private static void importOrdersAndOrderProductData(Connection conn, String csvPath) throws SQLException, IOException {
        String orderSql = """
            INSERT INTO orders (
                supplier_id, phone_number, physical_address, email_address,
                contact_name, delivery_method, order_date, delivery_date,
                total_price, order_status, supply_method
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        String productSql = """
            INSERT INTO order_product_data (
                order_id, product_id, product_quantity, product_price
            ) VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement psOrder = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psProduct = conn.prepareStatement(productSql);
             BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {

            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 7) continue;

                psOrder.setInt(1, Integer.parseInt(parts[0].trim()));
                psOrder.setString(2, "");
                psOrder.setString(3, "");
                psOrder.setString(4, "");
                psOrder.setString(5, "");
                psOrder.setString(6, parts[3].trim());
                psOrder.setString(7, parts[1].trim());
                psOrder.setString(8, parts[2].trim());
                psOrder.setDouble(9, Double.parseDouble(parts[5].trim()));
                psOrder.setString(10, "IN_PROCESS");
                psOrder.setString(11, parts[4].trim());
                psOrder.executeUpdate();

                int orderId;
                try (ResultSet keys = psOrder.getGeneratedKeys()) {
                    if (keys.next()) orderId = keys.getInt(1);
                    else throw new SQLException("Failed to retrieve generated order ID");
                }

                String[] products = parts[6].split(";");
                for (String pair : products) {
                    String[] kv = pair.split(":", 2);
                    if (kv.length != 2) continue;
                    psProduct.setInt(1, orderId);
                    psProduct.setInt(2, Integer.parseInt(kv[0].trim()));
                    psProduct.setInt(3, Integer.parseInt(kv[1].trim()));
                    psProduct.setDouble(4, 0.0); // no price in CSV
                    psProduct.executeUpdate();
                }
            }
            System.out.println("Imported orders and order_product_data from " + csvPath);
        }
    }

    private static void importContracts(Connection conn, String csvPath) throws SQLException, IOException {
        String contractSql = "INSERT INTO supply_contracts (id, supplier_id) VALUES (?, ?)";
        String productSql = """
            INSERT INTO supply_contract_product_data (
                contract_id, product_id, product_price,
                quantity_for_discount, discount_percentage
            ) VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement psContract = conn.prepareStatement(contractSql);
             PreparedStatement psProduct = conn.prepareStatement(productSql);
             BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {

            reader.readLine(); // skip header
            Set<Integer> seenContracts = new HashSet<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 6) continue;

                int supplierId = Integer.parseInt(parts[0].trim());
                int productId = Integer.parseInt(parts[1].trim());
                double price = Double.parseDouble(parts[2].trim());
                int qtyForDisc = Integer.parseInt(parts[3].trim());
                double discountPct = Double.parseDouble(parts[4].trim());
                int contractId = Integer.parseInt(parts[5].trim());

                if (!seenContracts.contains(contractId)) {
                    psContract.setInt(1, contractId);
                    psContract.setInt(2, supplierId);
                    psContract.executeUpdate();
                    seenContracts.add(contractId);
                }

                psProduct.setInt(1, contractId);
                psProduct.setInt(2, productId);
                psProduct.setDouble(3, price);
                psProduct.setInt(4, qtyForDisc);
                psProduct.setDouble(5, discountPct);
                psProduct.executeUpdate();
            }
            System.out.println("Imported contracts and supply_contract_product_data from " + csvPath);
        }
    }

    public static void main(String[] args) {
        try {
            importAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
