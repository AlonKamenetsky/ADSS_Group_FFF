package SuppliersModule.DataLayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DbController {
    private static final String DB_NAME = "data/SuppliersDatabase.db";
    private static final String DB_URL = "jdbc:sqlite:";

    protected Connection connection;

    public DbController() {
        try {
            this.connection = DriverManager.getConnection(DB_URL + DB_NAME);
            buildTablesIfNotExists();
        }
        catch (Exception e) {
            System.out.println(e.getMessage() + "111");
        }
    }

    private void buildTablesIfNotExists() throws SQLException {
        String createOrderProductDataTableSql = """
        CREATE TABLE IF NOT EXISTS "order_product_data" (
            "order_id"         INTEGER NOT NULL,
            "product_id"       INTEGER NOT NULL,
            "product_quantity" INTEGER NOT NULL,
            "product_price"    REAL    NOT NULL
        )
        """;

        String createOrdersTableSql = """
        CREATE TABLE IF NOT EXISTS "orders" (
            "id"               INTEGER NOT NULL,
            "supplier_id"      INTEGER NOT NULL,
            "phone_number"     TEXT    NOT NULL,
            "physical_address" TEXT    NOT NULL,
            "email_address"    TEXT    NOT NULL,
            "contact_name"     TEXT    NOT NULL,
            "delivery_method"  TEXT    NOT NULL,
            "order_date"       TEXT    NOT NULL,
            "delivery_date"    TEXT    NOT NULL,
            "total_price"      REAL    NOT NULL,
            "order_status"     TEXT    NOT NULL,
            "supply_method"    TEXT    NOT NULL
        )
        """;

        String createProductsTableSql = """
        CREATE TABLE IF NOT EXISTS "products" (
            "id"               INTEGER NOT NULL,
            "name"             TEXT    NOT NULL,
            "company_name"     TEXT    NOT NULL,
            "product_category" TEXT    NOT NULL
        )
        """;

        String createScheduledOrderDataTableSql = """
        CREATE TABLE IF NOT EXISTS "scheduled_order_data" (
            "supplier_id"      INTEGER NOT NULL,
            "day"              TEXT    NOT NULL,
            "product_id"       INTEGER NOT NULL,
            "product_quantity" INTEGER NOT NULL
        )
        """;

        String createSuppliersTableSql = """
        CREATE TABLE IF NOT EXISTS "suppliers" (
            "id"               INTEGER NOT NULL,
            "name"             TEXT    NOT NULL,
            "product_category" TEXT    NOT NULL,
            "contact_name"     TEXT    NOT NULL,
            "phone_number"     TEXT    NOT NULL,
            "email"            TEXT    NOT NULL,
            "address"          TEXT    NOT NULL,
            "delivery_method"  TEXT    NOT NULL,
            "bank_account"     TEXT    NOT NULL,
            "payment_method"   TEXT    NOT NULL,
            "supply_method"    TEXT    NOT NULL,
            PRIMARY KEY("id")
        )
        """;

        String createSuppliersDaysTableSql = """
        CREATE TABLE IF NOT EXISTS "suppliers_days" (
            "id"  INTEGER NOT NULL,
            "day" TEXT    NOT NULL
        )
        """;

        String createSupplyContractProductDataTableSql = """
        CREATE TABLE IF NOT EXISTS "supply_contract_product_data" (
            "contract_id"           INTEGER NOT NULL,
            "product_id"            INTEGER NOT NULL,
            "product_price"         REAL    NOT NULL,
            "quantity_for_discount" INTEGER NOT NULL,
            "discount_percentage"   REAL    NOT NULL
        )
        """;

        String createSupplyContractsTableSql = """
        CREATE TABLE IF NOT EXISTS "supply_contracts" (
            "id"          INTEGER NOT NULL,
            "supplier_id" INTEGER NOT NULL,
            PRIMARY KEY("id")
        )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createOrderProductDataTableSql);
            stmt.execute(createOrdersTableSql);
            stmt.execute(createProductsTableSql);
            stmt.execute(createScheduledOrderDataTableSql);
            stmt.execute(createSuppliersTableSql);
            stmt.execute(createSuppliersDaysTableSql);
            stmt.execute(createSupplyContractProductDataTableSql);
            stmt.execute(createSupplyContractsTableSql);
        }
    }


}
