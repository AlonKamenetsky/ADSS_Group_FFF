package SuppliersModule.DataLayer;

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
        this.orders               = new ArrayList<>();
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

    // =====================  Order‑Product Data  =====================

    public ArrayList<OrderProductDataDTO> getOrderProductDataByOrderID(OrderDTO orderDTO) {
        ArrayList<OrderProductDataDTO> result = new ArrayList<>();

        String sql = String.format("SELECT * FROM %s WHERE %s = ?",
                this.orderProductDataTableName,
                OrderProductDataDTO.ORDER_ID_COLUMN_NAME);

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
}