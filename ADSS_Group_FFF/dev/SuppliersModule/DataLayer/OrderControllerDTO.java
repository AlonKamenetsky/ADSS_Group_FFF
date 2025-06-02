package SuppliersModule.DataLayer;

import SuppliersModule.DomainLayer.ScheduledOrder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class OrderControllerDTO extends DbController {
    private static OrderControllerDTO single_instance = null;

    private final String ordersTableName              = "orders";
    private final String orderProductDataTableName    = "order_product_data";

    private ArrayList<OrderDTO> orders;
    private ArrayList<OrderProductDataDTO> orderProductDataList;

    private OrderControllerDTO() {
        super();
        this.orders = new ArrayList<>();
        this.orderProductDataList = new ArrayList<>();
    }

    public static OrderControllerDTO getInstance() {
        if (single_instance == null)
            single_instance = new OrderControllerDTO();
        return single_instance;
    }

    // =====================  Orders  =====================
    public ArrayList<OrderDTO> getAllOrders() {
        String sql = "SELECT * FROM " + this.ordersTableName;

        try (Statement stmt = this.connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                OrderDTO order = new OrderDTO(
                        rs.getInt(OrderDTO.ID_COLUMN_NAME),
                        rs.getInt(OrderDTO.SUPPLIER_ID_COLUMN_NAME),
                        rs.getString(OrderDTO.PHONE_NUMBER_COLUMN_NAME),
                        rs.getString(OrderDTO.PHYSICAL_ADDRESS_COLUMN_NAME),
                        rs.getString(OrderDTO.EMAIL_ADDRESS_COLUMN_NAME),
                        rs.getString(OrderDTO.CONTACT_NAME_COLUMN_NAME),
                        rs.getString(OrderDTO.DELIVERY_METHOD_COLUMN_NAME),
                        rs.getString(OrderDTO.ORDER_DATE_COLUMN_NAME),
                        rs.getString(OrderDTO.DELIVERY_DATE_COLUMN_NAME),
                        rs.getDouble(OrderDTO.TOTAL_PRICE_COLUMN_NAME),
                        rs.getString(OrderDTO.ORDER_STATUS_COLUMN_NAME),
                        rs.getString(OrderDTO.SUPPLY_METHOD_COLUMN_NAME)
                );
                this.orders.add(order);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return this.orders;
    }

    public void insertOrder(OrderDTO order) {
        String sql = String.format(
                "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                this.ordersTableName,
                OrderDTO.ID_COLUMN_NAME,
                OrderDTO.SUPPLIER_ID_COLUMN_NAME,
                OrderDTO.PHONE_NUMBER_COLUMN_NAME,
                OrderDTO.PHYSICAL_ADDRESS_COLUMN_NAME,
                OrderDTO.EMAIL_ADDRESS_COLUMN_NAME,
                OrderDTO.CONTACT_NAME_COLUMN_NAME,
                OrderDTO.DELIVERY_METHOD_COLUMN_NAME,
                OrderDTO.ORDER_DATE_COLUMN_NAME,
                OrderDTO.DELIVERY_DATE_COLUMN_NAME,
                OrderDTO.TOTAL_PRICE_COLUMN_NAME,
                OrderDTO.ORDER_STATUS_COLUMN_NAME,
                OrderDTO.SUPPLY_METHOD_COLUMN_NAME
        );

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1,  order.orderID);
            pstmt.setInt(2,  order.supplierID);
            pstmt.setString(3, order.phoneNumber);
            pstmt.setString(4, order.physicalAddress);
            pstmt.setString(5, order.emailAddress);
            pstmt.setString(6, order.contactName);
            pstmt.setString(7, order.deliveryMethod);
            pstmt.setString(8, order.orderDate);
            pstmt.setString(9, order.deliveryDate);
            pstmt.setDouble(10, order.totalPrice);
            pstmt.setString(11, order.orderStatus);
            pstmt.setString(12, order.supplyMethod);

            int result = pstmt.executeUpdate();
            if (result != 1)
                throw new SQLException("Failed inserting order");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        this.orders.add(order);
    }

    public boolean updateOrder(OrderDTO dto) {
        String sql = "UPDATE " + this.ordersTableName + " SET " +
                OrderDTO.SUPPLIER_ID_COLUMN_NAME      + " = ?, " +
                OrderDTO.PHONE_NUMBER_COLUMN_NAME     + " = ?, " +
                OrderDTO.PHYSICAL_ADDRESS_COLUMN_NAME + " = ?, " +
                OrderDTO.EMAIL_ADDRESS_COLUMN_NAME    + " = ?, " +
                OrderDTO.CONTACT_NAME_COLUMN_NAME     + " = ?, " +
                OrderDTO.DELIVERY_METHOD_COLUMN_NAME  + " = ?, " +
                OrderDTO.ORDER_DATE_COLUMN_NAME       + " = ?, " +
                OrderDTO.DELIVERY_DATE_COLUMN_NAME    + " = ?, " +
                OrderDTO.TOTAL_PRICE_COLUMN_NAME      + " = ?, " +
                OrderDTO.ORDER_STATUS_COLUMN_NAME     + " = ?, " +
                OrderDTO.SUPPLY_METHOD_COLUMN_NAME    + " = ? "  +
                "WHERE " + OrderDTO.ID_COLUMN_NAME + " = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt   (1,  dto.supplierID);
            ps.setString(2,  dto.phoneNumber);
            ps.setString(3,  dto.physicalAddress);
            ps.setString(4,  dto.emailAddress);
            ps.setString(5,  dto.contactName);
            ps.setString(6,  dto.deliveryMethod);
            ps.setString(7,  dto.orderDate);
            ps.setString(8,  dto.deliveryDate);
            ps.setDouble(9,  dto.totalPrice);
            ps.setString(10, dto.orderStatus);
            ps.setString(11, dto.supplyMethod);
            ps.setInt   (12, dto.orderID);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


    public boolean deleteOrder(OrderDTO orderDTO) {
        String deleteProductsSQL = "DELETE FROM " + orderProductDataTableName + " WHERE " + OrderProductDataDTO.ORDER_ID_COLUMN_NAME + " = ?";
        String deleteOrderSQL    = "DELETE FROM " + ordersTableName + " WHERE " + OrderDTO.ID_COLUMN_NAME + " = ?";
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement ps1 = connection.prepareStatement(deleteProductsSQL)) {
                ps1.setInt(1, orderDTO.orderID);
                ps1.executeUpdate(); // delete zero or more product rows
            }
            int rowsAffected;
            try (PreparedStatement ps2 = connection.prepareStatement(deleteOrderSQL)) {
                ps2.setInt(1, orderDTO.orderID);
                rowsAffected = ps2.executeUpdate(); // should be exactly 1
            }
            connection.commit();
            return rowsAffected == 1;
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ignored) {}
            System.out.println(e.getMessage());
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    // =====================  Order‑Product Data  =====================

    public ArrayList<OrderProductDataDTO> getOrderProductDataByOrderID(OrderDTO orderDTO) {
        ArrayList<OrderProductDataDTO> result = new ArrayList<>();

        String sql = String.format("SELECT * FROM %s WHERE %s = ?", this.orderProductDataTableName, OrderProductDataDTO.ORDER_ID_COLUMN_NAME);

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, orderDTO.orderID);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderProductDataDTO data = new OrderProductDataDTO(
                            rs.getInt(OrderProductDataDTO.ORDER_ID_COLUMN_NAME),
                            rs.getInt(OrderProductDataDTO.PRODUCT_ID_COLUMN_NAME),
                            rs.getInt(OrderProductDataDTO.PRODUCT_QUANTITY_COLUMN_NAME),
                            rs.getDouble(OrderProductDataDTO.PRODUCT_PRICE_COLUMN_NAME)
                    );
                    result.add(data);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    public void insertOrderProductData(OrderProductDataDTO orderProductDataDTO) {
        String sql = String.format(
                "INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
                this.orderProductDataTableName,
                OrderProductDataDTO.ORDER_ID_COLUMN_NAME,
                OrderProductDataDTO.PRODUCT_ID_COLUMN_NAME,
                OrderProductDataDTO.PRODUCT_QUANTITY_COLUMN_NAME,
                OrderProductDataDTO.PRODUCT_PRICE_COLUMN_NAME
        );

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, orderProductDataDTO.orderID);
            pstmt.setInt(2, orderProductDataDTO.productID);
            pstmt.setInt(3, orderProductDataDTO.productQuantity);
            pstmt.setDouble(4, orderProductDataDTO.productPrice);

            int result = pstmt.executeUpdate();
            if (result != 1)
                throw new SQLException("Failed inserting order product data");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        this.orderProductDataList.add(orderProductDataDTO);
    }

    public int deleteOrderProductData(OrderProductDataDTO orderProductDataDTO) {
        String sql = "DELETE FROM " + this.orderProductDataTableName +
                " WHERE " + OrderProductDataDTO.ORDER_ID_COLUMN_NAME + " = ? AND " +
                OrderProductDataDTO.PRODUCT_ID_COLUMN_NAME + " = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderProductDataDTO.orderID);
            ps.setInt(2, orderProductDataDTO.productID);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }


    public int deleteAllOrderProductData(int orderID) {
        String sql = "DELETE FROM " + this.orderProductDataTableName + " WHERE " + OrderProductDataDTO.ORDER_ID_COLUMN_NAME + " = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            return ps.executeUpdate(); // returns number of rows removed
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }
}