package SuppliersModule.DomainLayer;

import SuppliersModule.DataLayer.OrderDTO;
import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.OrderStatus;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class Order {
    int orderID;
    int supplierID;
    ContactInfo supplierContactInfo;
    DeliveringMethod deliveringMethod;

    Date orderDate;
    Date supplyDate;

    double totalPrice;

    SupplyMethod supplyMethod;

    ArrayList<OrderProductData> productArrayList;

    OrderStatus orderStatus;

    OrderDTO orderDTO;

    // -------------------------------------------------------------------------
    // Constructor used when creating a brand‐new Order
    // -------------------------------------------------------------------------
    public Order(int orderID,
                 int supplierID,
                 ArrayList<OrderProductData> dataList,
                 double totalOrderValue,
                 Date creationDate,
                 Date deliveryDate,
                 DeliveringMethod deliveringMethod,
                 SupplyMethod supplyMethod,
                 ContactInfo supplierContactInfo) {

        this.orderID = orderID;
        this.supplierID = supplierID;
        this.supplierContactInfo = supplierContactInfo;
        this.deliveringMethod = deliveringMethod;
        this.orderDate = creationDate;
        this.supplyDate = deliveryDate;
        this.totalPrice = totalOrderValue;
        this.productArrayList = dataList;
        this.supplyMethod = supplyMethod;
        this.orderStatus = OrderStatus.IN_PROCESS;

        // Build the DTO by converting these fields back into Strings.
        // Note: Date.toString() will produce something like "Mon Jun 02 00:00:00 IDT 2025",
        // but when the DTO is saved to the DB (via your importer) you will store "dd/MM/yyyy" instead.
        this.orderDTO = new OrderDTO(
                orderID,
                supplierID,
                supplierContactInfo.phoneNumber,
                supplierContactInfo.address,
                supplierContactInfo.email,
                supplierContactInfo.name,
                deliveringMethod.toString(),
                // We’ll convert these Dates into "dd/MM/yyyy":
                formatDate(orderDate),
                formatDate(supplyDate),
                totalPrice,
                orderStatus.toString(),
                supplyMethod.toString()
        );
    }

    // -------------------------------------------------------------------------
    // Constructor used when reading an existing OrderDTO from the database
    // -------------------------------------------------------------------------
    public Order(OrderDTO orderDTO) {
        this.orderID = orderDTO.orderID;
        this.supplierID = orderDTO.supplierID;

        ContactInfo contactInfo = new ContactInfo(
                orderDTO.phoneNumber,
                orderDTO.physicalAddress,
                orderDTO.emailAddress,
                orderDTO.contactName
        );
        this.supplierContactInfo = contactInfo;

        this.deliveringMethod = DeliveringMethod.valueOf(orderDTO.deliveryMethod);

        // Now parse the "dd/MM/yyyy" strings back into Date
        this.orderDate = ParaseDate(orderDTO.orderDate);
        this.supplyDate = ParaseDate(orderDTO.deliveryDate);

        this.totalPrice = orderDTO.totalPrice;

        this.productArrayList = new ArrayList<>();

        this.supplyMethod = SupplyMethod.valueOf(orderDTO.supplyMethod);

        this.orderStatus = OrderStatus.valueOf(orderDTO.orderStatus);

        this.orderDTO = orderDTO;
    }

    // -------------------------------------------------------------------------
    // 1) Helper to format a java.util.Date into "dd/MM/yyyy"
    //    (used when building a new OrderDTO)
    // -------------------------------------------------------------------------
    private static String formatDate(Date date) {
        // Convert java.util.Date → LocalDate at system default zone,
        // then format as "dd/MM/yyyy".
        LocalDate ld = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return ld.format(fmt);
    }

    // -------------------------------------------------------------------------
    // 2) Parse a "dd/MM/yyyy" string into java.util.Date at midnight Asia/Jerusalem
    // -------------------------------------------------------------------------
    private Date ParaseDate(String date) {
        // date is expected to be "dd/MM/yyyy", e.g. "07/04/2025".
        DateTimeFormatter dmy = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate ld = LocalDate.parse(date, dmy);

        // Attach Israel time‐zone at start of day:
        ZoneId israel = ZoneId.of("Asia/Jerusalem");
        ZonedDateTime zdt = ld.atStartOfDay(israel);
        return Date.from(zdt.toInstant());
    }

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------
    public int getOrderID() { return orderID; }
    public int getSupplierID() { return supplierID; }
    public ContactInfo getOrderContactInfo() { return supplierContactInfo; }
    public Date getOrderDate() { return orderDate; }
    public Date getSupplyDate() { return supplyDate; }
    public double getTotalPrice() { return totalPrice; }
    public SupplyMethod getSupplyMethod() { return supplyMethod; }
    public ArrayList<OrderProductData> getProductArrayList() { return productArrayList; }
    public OrderStatus getOrderStatus() { return orderStatus; }

    public void setProductArrayList(ArrayList<OrderProductData> productArrayList) {
        this.productArrayList = productArrayList;
    }
    public void addOrderProductData(OrderProductData orderProductData) {
        if (productArrayList == null) {
            productArrayList = new ArrayList<>();
        }
        this.productArrayList.add(orderProductData);
    }

    public void setSupplierContactInfo(ContactInfo supplierContactInfo) {
        this.supplierContactInfo = supplierContactInfo;
        this.orderDTO.phoneNumber = supplierContactInfo.phoneNumber;
        this.orderDTO.physicalAddress = supplierContactInfo.address;
        this.orderDTO.emailAddress = supplierContactInfo.email;
        this.orderDTO.contactName = supplierContactInfo.name;
    }

    public void setSupplyMethod(SupplyMethod supplyMethod) {
        this.supplyMethod = supplyMethod;
        this.orderDTO.supplyMethod = supplyMethod.toString();
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
        this.orderDTO.orderDate = formatDate(orderDate);
    }

    public void setSupplyDate(Date supplyDate) {
        this.supplyDate = supplyDate;
        this.orderDTO.deliveryDate = formatDate(supplyDate);
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
        this.orderDTO.totalPrice = totalPrice;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        this.orderDTO.orderStatus = orderStatus.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order {\n");
        sb.append("  Order ID: ").append(orderID).append("\n");
        sb.append("  Supplier ID: ").append(supplierID).append("\n");
        sb.append("  Supplier Contact: ").append(supplierContactInfo).append("\n");
        sb.append("  Order Date: ").append(orderDate).append("\n");
        sb.append("  Supply Date: ").append(supplyDate).append("\n");
        sb.append("  Supply Method: ").append(supplyMethod).append("\n");
        sb.append("  Total Price: ").append(String.format("%.2f", totalPrice)).append("\n");
        sb.append("  Order Status: ").append(orderStatus).append("\n");
        sb.append("  Products:\n");
        for (OrderProductData productData : productArrayList) {
            sb.append("    ").append(productData).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
