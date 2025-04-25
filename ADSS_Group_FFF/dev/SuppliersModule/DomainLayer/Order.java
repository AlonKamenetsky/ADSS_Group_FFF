package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.OrderStatus;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;

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

    ArrayList<int[]> productArrayList;

    OrderStatus orderStatus;

    public Order(int orderID, int supplierID, ArrayList<int[]> dataList, double totalOrderValue, Date creationDate, Date deliveryDate, DeliveringMethod deliveringMethod, SupplyMethod supplyMethod, ContactInfo supplierContactInfo) {
        this.orderID = orderID;
        this.supplierID = supplierID;
        this.supplierContactInfo = supplierContactInfo;
        this.deliveringMethod = deliveringMethod;
        this.orderDate = creationDate;
        this.supplyDate = deliveryDate;
        this.totalPrice = totalOrderValue;
        this.productArrayList = dataList;
        this.supplyMethod = supplyMethod;

        this.orderStatus = OrderStatus.RECEIVED;
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
    public ArrayList<int[]> getProductArrayList() {
        return productArrayList;
    }
    public void setProductArrayList(ArrayList<int[]> productArrayList){
        this.productArrayList = productArrayList;
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
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void addProductToOrder(int productID) {
        for (int i = 0; i < productArrayList.size(); i++) {
            int[] product = productArrayList.get(i);
            if (product[0] == productID) { // product[0] is ID
                productArrayList.add(product);
                break; // stop after first match
            }
        }
    }

    public void removeProductFromOrder(int productID) {
        for (int i = 0; i < productArrayList.size(); i++) {
            int[] product = productArrayList.get(i);
            if (product[0] == productID) { // product[0] is ID
                productArrayList.remove(i);
                break; // stop after first match
            }
        }
    }

    public boolean orderIsEmpty(){
        return this.productArrayList.isEmpty();
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
        for (int[] product : productArrayList) {
            sb.append("Product ID: ").append(Arrays.toString(product)).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
