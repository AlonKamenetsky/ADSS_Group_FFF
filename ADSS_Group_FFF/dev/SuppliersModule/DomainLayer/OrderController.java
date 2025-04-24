package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;

import java.util.ArrayList;
import java.util.Date;

public class OrderController {
    int orderID;
    ArrayList<Order> ordersArrayList; // TEMP DATA STRUCTURE

    public OrderController() {
        orderID = 0;
        ordersArrayList = new ArrayList<>();

    }

    public void registerOrder(int supplierId, ArrayList<int[]> dataList, double totalOrderValue, Date creationDate, Date deliveryDate, DeliveringMethod deliveringMethod, ContactInfo supplierContactInfo) {
        Order order = new Order(orderID, supplierId, dataList, totalOrderValue, creationDate, deliveryDate, deliveringMethod, supplierContactInfo);
        ordersArrayList.add(order);
        this.orderID++;
    }

    private Order getOrder(int orderID) {
        for (Order order : ordersArrayList) {
            if(order.orderID == orderID) {
                return order;
            }
        }
        return null;
    }

    public boolean updateOrderContactInfo(int orderID, String phoneNumber, String address, String email, String contactName){
        for (Order order : ordersArrayList) {
            if(order.orderID == orderID){
                ContactInfo contactInfo = order.getOrderContactInfo();
                contactInfo.setPhoneNumber(phoneNumber);
                contactInfo.setAddress(address);
                contactInfo.setEmail(email);
                contactInfo.setName(contactName);
                return true;
            }
        }
        return false;
    }

    public boolean updateOrderSupplyDate(int orderID, Date supplyDate){
        for (Order order : ordersArrayList) {
            if(order.orderID == orderID){
                order.supplyDate = supplyDate;
                return true;
            }
        }
        return false;
    }

    public boolean updateOrderSupplyMethod(int orderID, int supplyMethod){
        SupplyMethod method;
        if(supplyMethod == 1){
             method = SupplyMethod.SCHEDULED;
        }
        else if(supplyMethod == 2){
             method = SupplyMethod.ON_DEMAND;
        }
        else{
            return false;
        }
        Order order = getOrder(orderID);
        if(order != null){
            order.setSupplyMethod(method);
            return true;
        }
        return false;

    }

    public boolean deleteOrder(int orderID){
        for (Order order : ordersArrayList) {
            if(order.orderID == orderID){
                ordersArrayList.remove(order);
                return true;
            }
        }
        return false;
    }

    public Date getOrderSupplyDate(int orderID){
        for (Order order : ordersArrayList) {
            if(order.orderID == orderID){
                return order.getSupplyDate();
            }
        }
        return null;
    }

    public String getOrderAsString(int orderID){
        Order order = getOrder(orderID);
        if (order != null){
            return order.toString();
        }
        return null;
    }

    public String[] getAllOrdersAsString() {
        String[] ordersAsString = new String[ordersArrayList.size()];
        for (int i = 0; i < ordersArrayList.size(); i++) {
            ordersAsString[i] = ordersArrayList.get(i).toString();
        }
        return ordersAsString;
    }
}
