package SuppliersModule.DataLayer;

import SuppliersModule.DomainLayer.ContactInfo;
import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;
import SuppliersModule.DomainLayer.Order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class OrderDTO extends DTO {
    public int orderID;
    public int supplierID;
    public String phoneNumber;
    public String physicalAddress;
    public String emailAddress;
    public String contactName;
    public String deliveryMethod;
    public String orderDate;     // Stored as ISO‑8601 string (yyyy‑MM‑dd)
    public String deliveryDate;  // Stored as ISO‑8601 string (yyyy‑MM‑dd)
    public double totalPrice;
    public String orderStatus;
    public String supplyMethod;

    // ========== Column names ==========
    public static String ID_COLUMN_NAME                 = "id";
    public static String SUPPLIER_ID_COLUMN_NAME        = "supplier_id";
    public static String PHONE_NUMBER_COLUMN_NAME       = "phone_number";
    public static String PHYSICAL_ADDRESS_COLUMN_NAME   = "physical_address";
    public static String EMAIL_ADDRESS_COLUMN_NAME      = "email_address";
    public static String CONTACT_NAME_COLUMN_NAME       = "contact_name";
    public static String DELIVERY_METHOD_COLUMN_NAME    = "delivery_method";
    public static String ORDER_DATE_COLUMN_NAME         = "order_date";
    public static String DELIVERY_DATE_COLUMN_NAME      = "delivery_date";
    public static String TOTAL_PRICE_COLUMN_NAME        = "total_price";
    public static String ORDER_STATUS_COLUMN_NAME       = "order_status";
    public static String SUPPLY_METHOD_COLUMN_NAME      = "supply_method";

    public OrderDTO(int orderID,
                    int supplierID,
                    String phoneNumber,
                    String physicalAddress,
                    String emailAddress,
                    String contactName,
                    String deliveryMethod,
                    String orderDate,
                    String deliveryDate,
                    double totalPrice,
                    String orderStatus,
                    String supplyMethod) {

        super(OrderControllerDTO.getInstance());

        this.orderID       = orderID;
        this.supplierID    = supplierID;
        this.phoneNumber   = phoneNumber;
        this.physicalAddress = physicalAddress;
        this.emailAddress  = emailAddress;
        this.contactName   = contactName;
        this.deliveryMethod = deliveryMethod;
        this.orderDate     = orderDate;
        this.deliveryDate  = deliveryDate;
        this.totalPrice    = totalPrice;
        this.orderStatus   = orderStatus;
        this.supplyMethod  = supplyMethod;
    }

    public void Insert() {
        OrderControllerDTO controller = (OrderControllerDTO) this.dbController;
        controller.insertOrder(this);
    }

    public void Update() {
        OrderControllerDTO controller = (OrderControllerDTO) this.dbController;
        controller.updateOrder(this);
    }

    public void Delete() {
        OrderControllerDTO controller = (OrderControllerDTO) this.dbController;
        controller.deleteOrder(this);
    }

    public Order convertDTOToEntity() {
        return new Order(this);
    }
}