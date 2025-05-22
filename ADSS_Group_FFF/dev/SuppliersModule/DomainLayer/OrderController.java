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
        this.orderID = 0;
        this.ordersArrayList = new ArrayList<>();
    }

    public void ReadOrdersFromCSVFile() {
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

                Order order = new Order(orderID, supplierId, buildProductDataArray(productList, null), totalPrice, orderDate, supplyDate, deliveringMethod, supplyMethod, contactInfo);
                ordersArrayList.add(order);
                this.orderID++;

            }
        } catch (Exception e) {
            System.err.println("Error reading orders CSV: " + e.getMessage());
        }
    }

    private static boolean ValidateProductInContracts(SupplyContract supplyContract, int productID) {
        return supplyContract.CheckIfProductInData(productID);
    }

    public static ArrayList<OrderProductData> buildProductDataArray(ArrayList<int[]> dataList, ArrayList<SupplyContract> supplyContracts) {

        ArrayList<OrderProductData> productDataList = new ArrayList<>();

        for (int[] entry : dataList) {

            int productId = entry[0];

            SupplyContract supplyContract = null;
            for (SupplyContract contract : supplyContracts)
                if (ValidateProductInContracts(contract, productId)) {
                    supplyContract = contract;
                    break;
                }
            if (supplyContract == null)
                return null;

            SupplyContractProductData data = supplyContract.getSupplyContractProductDataOfProduct(productId);

            int quantity = entry[1];
            double productPrice = data.getProductPrice();

            if (quantity >= data.getQuantityForDiscount())
                productPrice = productPrice * ((100 - data.getDiscountPercentage()) / 100);


            productDataList.add(new OrderProductData(productId, quantity, productPrice));
        }

        return productDataList;
    }

    private double calculateTotalPrice(ArrayList<OrderProductData> dataList) {
        double totalPrice = 0;
        for (OrderProductData data : dataList)
            totalPrice += data.getTotalPrice();

        return totalPrice;
    }

    public boolean registerNewOrder(int supplierId, ArrayList<int[]> dataList, ArrayList<SupplyContract> supplyContracts, Date creationDate, Date deliveryDate, DeliveringMethod deliveringMethod, SupplyMethod supplyMethod, ContactInfo supplierContactInfo) {
        ArrayList<OrderProductData> orderProductDataList = buildProductDataArray(dataList, supplyContracts);
        if (orderProductDataList == null)
            return false;

        for (OrderProductData orderProductData : orderProductDataList)
            orderProductData.setOrderID(orderID);

        double totalOrderValue = calculateTotalPrice(orderProductDataList);

        Order order = new Order(orderID, supplierId, orderProductDataList, totalOrderValue, creationDate, deliveryDate, deliveringMethod, supplyMethod, supplierContactInfo);
        ordersArrayList.add(order);

        this.orderID++;


        return true;
    }

    private Order getOrderByID(int orderID) {
        for (Order order : ordersArrayList)
            if(order.orderID == orderID)
                return order;

        return null;
    }

    public boolean deleteOrder(int orderID){
        return this.ordersArrayList.removeIf(order -> order.orderID == orderID);
    }

    public boolean removeAllSupplierOrders(int supplierID) {
        return this.ordersArrayList.removeIf(order -> order.supplierID == supplierID);
    }

    public boolean orderExists(int orderID) {
        Order order = getOrderByID(orderID);
        return order != null;
    }

    // ********** UPDATE FUNCTIONS **********

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
        Order order = getOrderByID(orderID);
        if (order != null) {
            order.orderStatus = orderStatus;
            return true;
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

    public boolean addProductsToOrder(int orderID, ArrayList<SupplyContract> supplyContracts ,ArrayList<int[]> dataList) {
        ArrayList<OrderProductData> products = this.getOrderProducts(orderID);
        if(products == null)
            return false;

        ArrayList<OrderProductData> newProducts = buildProductDataArray(dataList, supplyContracts);
        products.addAll(newProducts);

        double totalPrice = calculateTotalPrice(products);

        this.setOrderProducts(orderID, products);
        this.setOrderPrice(orderID, totalPrice);
        return true;
    }

    public boolean removeProductsFromOrder(int orderID, ArrayList<Integer> dataList) {
        ArrayList<OrderProductData> products = this.getOrderProducts(orderID);
        if(products == null)
            return false;

        for (int productID : dataList)
            products.removeIf(orderProductData -> orderProductData.getProductID() == productID);

        if (products.isEmpty()){
            this.deleteOrder(orderID);
            return true;
        }

        double totalPrice = calculateTotalPrice(products);

        this.setOrderProducts(orderID, products);
        this.setOrderPrice(orderID, totalPrice);

        return true;
    }

    // ********** GETTERS FUNCTIONS **********

    public int getOrderSupplierID(int orderID){
        Order order = getOrderByID(orderID);
        if (order != null)
            return order.getSupplierID();

        return -1;
    }

    public ArrayList<OrderProductData> getOrderProducts(int orderID){
        Order order = getOrderByID(orderID);
        if (order != null)
            return order.getProductArrayList();

        return null;
    }

    // ********** SETTERS FUNCTIONS **********

    private boolean setOrderProducts(int orderID, ArrayList<OrderProductData> productArrayList){
        Order order = getOrderByID(orderID);
        if (order == null)
            return false;

        order.setProductArrayList(productArrayList);
        return true;
    }

    private boolean setOrderPrice(int orderID, double price){
        Order order = getOrderByID(orderID);
        if(order == null)
            return false;

        order.setTotalPrice(price);
        return true;
    }

    // ********** OUTPUT FUNCTIONS **********

    public String getOrderAsString(int orderID){
        Order order = getOrderByID(orderID);
        if (order != null)
            return order.toString();

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
