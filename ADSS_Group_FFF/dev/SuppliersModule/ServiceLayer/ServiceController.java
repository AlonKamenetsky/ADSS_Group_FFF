package SuppliersModule.ServiceLayer;

import SuppliersModule.DomainLayer.Enums.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ServiceController {
    SupplierService supplierService;
    ProductService productService;

    public ServiceController() {
        this.supplierService = new SupplierService();
        this.productService = new ProductService();
    }


    // --------------------------- VALIDATION FUNCTIONS ---------------------------

    private boolean validateProductCategory(int productCategory) {
        return (productCategory >= 0 && productCategory < ProductCategory.values().length);
    }

    private boolean validateSupplyMethod(int supplyMethod) {
        return (supplyMethod >= 0 && supplyMethod < SupplyMethod.values().length);
    }

    private boolean validateDeliveringMethod(int deliveringMethod) {
        return (deliveringMethod >= 0 && deliveringMethod < DeliveringMethod.values().length);
    }

    private boolean validatePaymentMethod(int paymentMethod) {
        return (paymentMethod >= 0 && paymentMethod < PaymentMethod.values().length);
    }

    private boolean validateSupplierAndProduct(int supplierID, int productID) {
        return this.supplierService.getSupplierProductCategory(supplierID) == this.productService.getProductCategory(productID);
    }

    private boolean validateContractProductData(int price, int quantityForDiscount, int discountPercentage) {
        return price > 0 || quantityForDiscount > 0 || (discountPercentage > 0 && discountPercentage < 100);
    }

    private Date validateOrderDated(String supplyDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date parsedDate = sdf.parse(supplyDate);
            System.out.println("Parsed Date: " + parsedDate);
            return parsedDate;
        } catch (ParseException e) {
            System.out.println("Invalid Date Format");
        }
        return null;
    }

    private Date validateDate(String strDate){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false); // Makes sure it strictly checks the format (e.g., no 31/02/2024)
        try {
            return sdf.parse(strDate); // Returns a java.util.Date object
        } catch (ParseException e) {
            return null; // If invalid, return null
        }
    }

    private boolean validateDay(int day) {
        return day >= 1 && day <= 7;
    }

    private boolean validateDays(ArrayList<Integer> days) {
        if (days == null)
            return true;

        for (int day : days)
            if (!validateDay(day))
                return false;
        return true;
    }

    private boolean validateOrderStatus(int orderStatus) {
        return (orderStatus >= 0 && orderStatus < OrderStatus.values().length);
    }


    // --------------------------- PRODUCT FUNCTIONS ---------------------------

    public int registerNewProduct(String productName, String productCompanyName, int productCategory) {
        if (validateProductCategory(productCategory))
            return this.productService.registerNewProduct(productName, productCompanyName, ProductCategory.values()[productCategory]);
        return -1;
    }

    public boolean updateProduct(int productID, String productName, String productCompanyName) {
        return this.productService.updateProduct(productID, productName, productCompanyName);
    }

    public boolean deleteProduct(int productID) {
        return this.productService.deleteProduct(productID);
    }

    public String[] getAllProductsAsStrings() {
        return this.productService.getProductsAsString();
    }

    public String getProductAsString(int productID) {
        return this.productService.getProductAsString(productID);
    }

    // --------------------------- SUPPLIER FUNCTIONS ---------------------------

    public int registerNewSupplier(int supplyMethod, String supplierName, int productCategory, int deliveringMethod,
                                    String phoneNumber, String address, String email, String contactName,
                                    String bankAccount, int paymentMethod, ArrayList<Integer> supplyDays) {
        if (this.validateProductCategory(productCategory) && this.validateSupplyMethod(supplyMethod) && this.validateDeliveringMethod(deliveringMethod) &&
                this.validatePaymentMethod(paymentMethod) && validateDays(supplyDays))
            return this.supplierService.registerNewSupplier(supplyMethod, supplierName, productCategory, deliveringMethod, phoneNumber, address, email, contactName, bankAccount, paymentMethod, supplyDays);
        return -1;
    }

    public boolean deleteSupplier(int supplierID) {
        return supplierService.deleteSupplier(supplierID);
    }

    public boolean updateSupplierName(int supplierID, String supplierName) {
        return this.supplierService.updateSupplierName(supplierID, supplierName);
    }

    public boolean updateSupplierDeliveringMethod(int supplierID, int deliveringMethod) {
        if (this.validateDeliveringMethod(deliveringMethod))
            return this.supplierService.updateSupplierDeliveringMethod(supplierID, deliveringMethod);
        return false;
    }

    public boolean updateSupplierContactInfo(int supplierID, String phoneNumber, String address, String email, String contactName) {
        return this.supplierService.updateSupplierContactInfo(supplierID, phoneNumber, address, email, contactName);
    }

    public boolean updateSupplierPaymentInfo(int supplierId, String bankAccount, int paymentMethod) {
        if (this.validatePaymentMethod(paymentMethod))
            return this.supplierService.updateSupplierPaymentInfo(supplierId, bankAccount, paymentMethod);
        return false;
    }

    public String[] getAllSuppliersAsString() {
        return this.supplierService.getAllSuppliersAsString();
    }

    public String getSupplierAsString(int supplierID) {
        return this.supplierService.getSupplierAsString(supplierID);
    }


    // --------------------------- CONTRACT FUNCTIONS ---------------------------

    public boolean registerNewContract(int supplierID, ArrayList<int[]> dataList) {
        for (int[] data : dataList)
            if (!validateSupplierAndProduct(supplierID, data[0]) || !validateContractProductData(data[1], data[2], data[3])) {
                System.out.println("1111" + validateSupplierAndProduct(supplierID, data[0]));
                System.out.println("222" + validateContractProductData(data[1], data[2], data[3]));
                return false;
            }


        return this.supplierService.registerNewContract(supplierID, dataList);
    }

    public String[] getSupplierContractsAsString(int supplierID) {
        return this.supplierService.getSupplierContractsAsString(supplierID);
    }
    public String getContractsAsString(int contractID) {
        return supplierService.getContractToString(contractID);
    }

    public String[] getAvailableContractsForOrderAsString(int orderID) {
        return this.supplierService.getAvailableContractsForOrderAsString(orderID);
    }
    public String[] getAllContractToStrings(){
        return this.supplierService.getAllContractToStrings();
    }

    // --------------------------- ORDER FUNCTIONS ---------------------------

    public boolean registerNewOrder(ArrayList<int[]> dataList, Date creationDate, String deliveryDate) {
        Date deliveryDateAsDate = this.validateOrderDated(deliveryDate);
        if (deliveryDateAsDate == null)
            return false;
        if (deliveryDateAsDate.before(creationDate))
            return false;
        return this.supplierService.registerNewOrder(dataList, creationDate, deliveryDateAsDate);
    }

    public boolean registerNewScheduledOrder(int day, ArrayList<int[]> dataList) {
        if (!validateDay(day))
            return false;

        return this.supplierService.registerNewScheduledOrder(day, dataList);
    }

    public boolean deleteOrder(int orderID) {
        return this.supplierService.deleteOrder(orderID);
    }

    public boolean orderExists(int orderID) {
        return supplierService.orderExists(orderID);
    }

    public boolean updateOrderContactInfo(int orderId,  String phoneNumber, String address, String email, String contactName){
        return this.supplierService.updateOrderContactInfo(orderId, phoneNumber, address, email, contactName);
    }

    public boolean updateOrderSupplyDate(int orderID, String supplyDate){
        Date supplyDateAsDate = this.validateDate(supplyDate);
        if(supplyDateAsDate == null)
            return false;
        Date oldSupplyDate = supplierService.getOrderSupplyDate(orderID);
        if(supplyDateAsDate.before(oldSupplyDate))
            return false;
        return this.supplierService.updateOrderSupplyDate(orderID, supplyDateAsDate);
    }

    public HashMap<Integer, Integer> updateOrderStatus(int orderID, int orderStatus) {
        if (this.validateOrderStatus(orderID))
            return this.supplierService.updateOrderStatus(orderID, orderStatus);
        return null;
    }

    public boolean addProductsToOrder(int orderID, ArrayList<int[]> dataList) {
        return this.supplierService.addProductsToOrder(orderID, dataList);
    }

    public boolean removeProductsFromOrder(int orderID, ArrayList<Integer> dataList) {
        return this.supplierService.removeProductsFromOrder(orderID, dataList);
    }

    public String getOrderAsString(int orderID) {
        return this.supplierService.getOrderAsString(orderID);
    }

    public String[] getAllOrdersAsString() {
        return this.supplierService.getAllOrdersAsString();
    }

}
