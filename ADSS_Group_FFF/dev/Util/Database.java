package Util;

import java.sql.*;

public final class Database {
    private static final String DB_URL = "jdbc:sqlite:SuperLee.db";
    private final static Connection conn;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);

            try (Statement st = conn.createStatement()) {
                // Transportation Tables
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS trucks(
                        truck_id        INTEGER PRIMARY KEY AUTOINCREMENT,
                        truck_type      TEXT      NOT NULL,
                        license_number  TEXT      NOT NULL,
                        model           TEXT      NOT NULL,
                        net_weight      REAL      NOT NULL,
                        max_weight      REAL      NOT NULL,
                        is_free         BOOLEAN   NOT NULL
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS zones (
                        zone_id    INTEGER PRIMARY KEY AUTOINCREMENT,
                        zone_name  TEXT NOT NULL
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS sites (
                        site_id       INTEGER PRIMARY KEY AUTOINCREMENT,
                        address       TEXT    NOT NULL,
                        contact_name  TEXT    NOT NULL,
                        phone_number  TEXT    NOT NULL,
                        zone_id       INTEGER NOT NULL,
                        FOREIGN KEY (zone_id) REFERENCES zones(zone_id)
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS items (
                        item_id   INTEGER PRIMARY KEY AUTOINCREMENT,
                        item_name TEXT    NOT NULL,
                        weight    REAL    NOT NULL
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS items_lists (
                        list_id INTEGER PRIMARY KEY AUTOINCREMENT
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS items_in_list (
                        list_id   INTEGER NOT NULL,
                        item_id   INTEGER NOT NULL,
                        quantity  INTEGER NOT NULL,
                        PRIMARY KEY (list_id, item_id),
                        FOREIGN KEY (list_id) REFERENCES items_lists(list_id),
                        FOREIGN KEY (item_id) REFERENCES items(item_id)
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS transportation_tasks (
                        task_id               INTEGER PRIMARY KEY AUTOINCREMENT,
                        truck_id              INTEGER,
                        task_date             TEXT    NOT NULL,
                        departure_time        TEXT    NOT NULL,
                        source_site_id        INTEGER NOT NULL,
                        weight_before_leaving REAL    NOT NULL,
                        driver_id             TEXT,
                        FOREIGN KEY (source_site_id) REFERENCES sites(site_id),
                        FOREIGN KEY (truck_id) REFERENCES trucks(truck_id)
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS transportation_docs (
                        doc_id           INTEGER PRIMARY KEY AUTOINCREMENT,
                        task_id          INTEGER NOT NULL,
                        destination_site INTEGER NOT NULL,
                        item_list_id     INTEGER NOT NULL,
                        FOREIGN KEY (task_id) REFERENCES transportation_tasks(task_id),
                        FOREIGN KEY (destination_site) REFERENCES sites(site_id),
                        FOREIGN KEY (item_list_id) REFERENCES items_lists(list_id)
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS transportation_task_destinations (
                        task_id      INTEGER NOT NULL,
                        site_id      INTEGER NOT NULL,
                        PRIMARY KEY (task_id, site_id),
                        FOREIGN KEY (task_id) REFERENCES transportation_tasks(task_id),
                        FOREIGN KEY (site_id) REFERENCES sites(site_id)
                    );
                """);

                // HR Tables:
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS employees (
                        id TEXT PRIMARY KEY,
                        name TEXT NOT NULL,
                        password TEXT NOT NULL,
                        bank_account TEXT,
                        salary REAL,
                        employment_date DATE
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS roles (
                        name TEXT PRIMARY KEY
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS employee_roles (
                        employee_id TEXT NOT NULL,
                        role_name TEXT NOT NULL,
                        PRIMARY KEY (employee_id, role_name),
                        FOREIGN KEY (employee_id) REFERENCES employees(id),
                        FOREIGN KEY (role_name) REFERENCES roles(name)
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS shifts (
                        id TEXT PRIMARY KEY,
                        date DATE NOT NULL,
                        time TEXT NOT NULL
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS shift_assignments (
                        shift_id TEXT NOT NULL,
                        employee_id TEXT NOT NULL,
                        role_name TEXT NOT NULL,
                        PRIMARY KEY (shift_id, employee_id, role_name),
                        FOREIGN KEY (shift_id) REFERENCES shifts(id),
                        FOREIGN KEY (employee_id) REFERENCES employees(id),
                        FOREIGN KEY (role_name) REFERENCES roles(name)
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS weekly_availability (
                        employee_id TEXT NOT NULL,
                        available_date DATE NOT NULL,
                        shift_time TEXT NOT NULL,
                        PRIMARY KEY (employee_id, available_date, shift_time),
                        FOREIGN KEY (employee_id) REFERENCES employees(id)
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS swap_requests (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        requester_id TEXT NOT NULL,
                        shift_id TEXT NOT NULL,
                        role_name TEXT NOT NULL,
                        FOREIGN KEY (requester_id) REFERENCES employees(id),
                        FOREIGN KEY (shift_id) REFERENCES shifts(id),
                        FOREIGN KEY (role_name) REFERENCES roles(name)
                    );
                """);
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS driver_info (
                        employee_id TEXT PRIMARY KEY,
                        license_type TEXT NOT NULL,
                        FOREIGN KEY (employee_id) REFERENCES employees(id)
                    );
                """);

                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS shift_templates (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        day_of_week TEXT NOT NULL,
                        shift_time TEXT NOT NULL
                    );
                """);

                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS shift_template_roles (
                        template_id INTEGER NOT NULL,
                        role_name TEXT NOT NULL,
                        count INTEGER NOT NULL,
                        PRIMARY KEY (template_id, role_name),
                        FOREIGN KEY (template_id) REFERENCES shift_templates(id),
                        FOREIGN KEY (role_name) REFERENCES roles(name)
                    );
                """);


            }
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Database() {
    }

    public static Connection getConnection() {
        return conn;
    }
}
