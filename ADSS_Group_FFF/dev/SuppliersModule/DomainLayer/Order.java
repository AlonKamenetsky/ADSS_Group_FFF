package SuppliersModule.DomainLayer;

import SuppliersModule.DataLayer.OrderDTO;
import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.OrderStatus;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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

    ArrayList<OrderProductData> productArrayList; // [ProductID, Product_Amount

    OrderStatus orderStatus;

    OrderDTO orderDTO;

    public Order(int orderID, int supplierID, ArrayList<OrderProductData> dataList, double totalOrderValue, Date creationDate, Date deliveryDate, DeliveringMethod deliveringMethod, SupplyMethod supplyMethod, ContactInfo supplierContactInfo) {
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

        this.orderDTO = new OrderDTO(orderID, supplierID, supplierContactInfo.phoneNumber, supplierContactInfo.address, supplierContactInfo.email, supplierContactInfo.name,
                                        deliveringMethod.toString(), orderDate.toString(), deliveryDate.toString(), totalPrice ,orderStatus.toString(), supplyMethod.toString());
    }

    public Order(OrderDTO orderDTO) {
        this.orderID = orderDTO.orderID;
        this.supplierID = orderDTO.supplierID;

        ContactInfo contactInfo = new ContactInfo(orderDTO.phoneNumber, orderDTO.physicalAddress, orderDTO.emailAddress, orderDTO.contactName);
        this.supplierContactInfo = contactInfo;

        this.deliveringMethod = DeliveringMethod.valueOf(orderDTO.deliveryMethod);

        LocalDate localDate = LocalDate.parse(orderDTO.orderDate, DateTimeFormatter.ISO_LOCAL_DATE);
        this.orderDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        localDate = LocalDate.parse(orderDTO.deliveryDate, DateTimeFormatter.ISO_LOCAL_DATE);
        this.supplyDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());;

        this.totalPrice = orderDTO.totalPrice;

        this.productArrayList = new ArrayList<>();

        this.supplyMethod = SupplyMethod.valueOf(orderDTO.supplyMethod);

        this.orderStatus = OrderStatus.valueOf(orderDTO.orderStatus);

        this.orderDTO = orderDTO;
    }

    public int getSupplierID(){
        return orderID;
    }
    public ContactInfo getOrderContactInfo() {
        return supplierContactInfo;
    }
    public Date getOrderDate() {
        return orderDate;
    }
    public Date getSupplyDate() {
        return supplyDate;
    }
    public double getTotalPrice() {
        return totalPrice;
    }
    public SupplyMethod getSupplyMethod() {
        return supplyMethod;
    }
    public ArrayList<OrderProductData> getProductArrayList() {
        return productArrayList;
    }
    public void setProductArrayList(ArrayList<OrderProductData> productArrayList){
        this.productArrayList = productArrayList;
    }
    public void addOrderProductData(OrderProductData orderProductData){
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
        this.orderDTO.orderDate = orderDate.toString();
    }
    public void setSupplyDate(Date supplyDate) {
        this.supplyDate = supplyDate;
        this.orderDTO.deliveryDate = supplyDate.toString();
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
        sb.append("  Products:\n");
        for (OrderProductData productData : productArrayList) {
            sb.append(productData);
        }
        sb.append("}");
        return sb.toString();
    }
}
