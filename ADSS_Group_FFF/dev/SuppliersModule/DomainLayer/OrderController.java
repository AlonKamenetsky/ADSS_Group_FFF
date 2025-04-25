package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.OrderStatus;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderController {
    int orderID;
    ArrayList<Order> ordersArrayList; // TEMP DATA STRUCTURE

    public OrderController() {
        orderID = 0;
        ordersArrayList = new ArrayList<>();
        readOrdersFromCSVFile();

    }

    public void readOrdersFromCSVFile() {
        InputStream in = OrderController.class.getResourceAsStream("/orders_data.csv");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            boolean isFirstLine = true;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split(",", 11); // first 10 fields + products
                int supplierId = Integer.parseInt(parts[0]);
                Date orderDate = sdf.parse(parts[1]);
                Date supplyDate = sdf.parse(parts[2]);

                DeliveringMethod deliveringMethod = DeliveringMethod.valueOf(parts[3].toUpperCase());
                SupplyMethod supplyMethod = SupplyMethod.valueOf(parts[4].toUpperCase());

                String contactName = parts[5];
                String phone = parts[6];
                String address = parts[7];
                String email = parts[8];

                double totalPrice = Double.parseDouble(parts[9]);

                // Parse products
                ArrayList<int[]> productList = new ArrayList<>();
                String[] productEntries = parts[10].split(";");
                for (String productEntry : productEntries) {
                    String[] prod = productEntry.split(":");
                    int productId = Integer.parseInt(prod[0]);
                    int quantity = Integer.parseInt(prod[1]);
                    productList.add(new int[]{productId, quantity});
                }

                ContactInfo contactInfo = new ContactInfo(contactName, email, phone, address);

                this.registerOrder(supplierId, productList, totalPrice, orderDate, supplyDate, deliveringMethod, supplyMethod, contactInfo);
            }
        } catch (Exception e) {
            System.err.println("Error reading orders CSV: " + e.getMessage());
        }
    }

    public void registerOrder(int supplierId, ArrayList<int[]> dataList, double totalOrderValue, Date creationDate, Date deliveryDate, DeliveringMethod deliveringMethod, SupplyMethod supplyMethod, ContactInfo supplierContactInfo) {
        Order order = new Order(orderID, supplierId, dataList, totalOrderValue, creationDate, deliveryDate, deliveringMethod, supplyMethod, supplierContactInfo);
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

    public boolean updateOrderStatus(int orderID, OrderStatus orderStatus){
        Order order = getOrder(orderID);
        if (order != null) {
            order.orderStatus = orderStatus;
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

    public int getOrderSupplierID(int orderID){
        Order order = getOrder(orderID);
        if (order != null) {
            return order.getSupplierID();
        }
        return -1;
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


    public ArrayList<int[]>  getOrderProducts(int orderID){
        for (Order order : ordersArrayList) {
            if(order.orderID == orderID){
                return order.getProductArrayList();
            }
        }
        return null;
    }

    public boolean setOrderProducts(int orderID, ArrayList<int[]> productArrayList){
        Order order = getOrder(orderID);
        if(order == null)
            return false;
        order.setProductArrayList(productArrayList);
        return true;
    }
    public boolean setOrderPrice(int orderID, double price){
        Order order = getOrder(orderID);
        if(order == null)
            return false;
        order.setTotalPrice(price);
        return true;
    }

    public boolean orderExists(int orderID) {
        Order order = getOrder(orderID);
        return order != null;
    }
}
