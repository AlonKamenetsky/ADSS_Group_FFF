package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class Order {
    int orderID;
    int supplierID;
    ContactInfo supplierContactInfo;

    Date orderDate;
    Date supplyDate;

    double totalPrice;

    SupplyMethod supplyMethod;

    ArrayList<Product> productArrayList;

    //Functions
    public Order(int orderID, int supplierId, ArrayList<int[]> dataList, double totalOrderValue, Date creationDate, Date deliveryDate, DeliveringMethod deliveringMethod, ContactInfo supplierContactInfo) {

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

    public void setSupplyMethod(SupplyMethod supplyMethod) {
        this.supplyMethod = supplyMethod;
    }
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    public void setSupplyDate(Date supplyDate) {
        this.supplyDate = supplyDate;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
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
        for (Product product : productArrayList) {
            sb.append("    ").append(product).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
