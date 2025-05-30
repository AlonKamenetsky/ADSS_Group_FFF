package SuppliersModule.DataLayer;

import SuppliersModule.DomainLayer.OrderProductData;

public class OrderProductDataDTO extends DTO {
    public int orderID;
    public int productID;
    public int productQuantity;
    public double productPrice;

    // ========== Column names ==========
    public static String ORDER_ID_COLUMN_NAME        = "order_id";
    public static String PRODUCT_ID_COLUMN_NAME      = "product_id";
    public static String PRODUCT_QUANTITY_COLUMN_NAME = "product_quantity";
    public static String PRODUCT_PRICE_COLUMN_NAME    = "product_price";

    public OrderProductDataDTO(int orderID, int productID, int productQuantity, double productPrice) {
        super(OrderControllerDTO.getInstance());
        this.orderID         = orderID;
        this.productID       = productID;
        this.productQuantity = productQuantity;
        this.productPrice    = productPrice;
    }

    public void Insert() {
        OrderControllerDTO controller = (OrderControllerDTO) this.dbController;
        controller.insertOrderProductData(this);
    }

    public OrderProductData convertDTOToEntity() {
        return new OrderProductData(orderID, productID, productQuantity, productPrice);
    }
}