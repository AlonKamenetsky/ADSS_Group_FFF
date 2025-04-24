package SuppliersModule.ServiceLayer;

import SuppliersModule.DomainLayer.*;

import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.PaymentMethod;
import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;

import java.util.ArrayList;
import java.util.Date;

public class SupplierService {
    SupplierController supplierController;
    OrderController orderController;
    SupplyContractController supplyContractController;

    public SupplierService() {

        supplierController = new SupplierController();
        orderController = new OrderController();
        supplyContractController = new SupplyContractController();
    }

    public int registerNewSupplier(int supplyMethod, String supplierName, int productCategory, int deliveringMethod,
                                   String phoneNumber, String address, String email, String contactName,
                                   String bankAccount, int paymentMethod) {
        SupplyMethod sm = SupplyMethod.values()[supplyMethod];
        ProductCategory pc = ProductCategory.values()[productCategory];
        DeliveringMethod dm = DeliveringMethod.values()[deliveringMethod];
        PaymentMethod pm = PaymentMethod.values()[paymentMethod];
        return this.supplierController.registerNewSupplier(sm, supplierName, pc, dm, phoneNumber, address, email, contactName, bankAccount, pm);
    }

    public boolean updateSupplierName(int supplierID, String supplierName) {
        return this.supplierController.updateSupplierName(supplierID, supplierName);
    }

    public boolean updateSupplierDeliveringMethod(int supplierID, int deliveringMethod) {
        DeliveringMethod dm = DeliveringMethod.values()[deliveringMethod];
        return this.supplierController.updateSupplierDeliveringMethod(supplierID, dm);
    }

    public boolean updateSupplierContactInfo(int supplierID, String phoneNumber, String address, String email, String contactName) {
        return this.supplierController.updateSupplierContactInfo(supplierID, phoneNumber, address, email, contactName);
    }

    public boolean updateSupplierPaymentInfo(int supplierId, String bankAccount, int paymentMethod) {
        PaymentMethod pm = PaymentMethod.values()[paymentMethod];
        return this.supplierController.updateSupplierPaymentInfo(supplierId, bankAccount, pm);
    }

    public void registerNewContract(int supplierID, ArrayList<int[]> dataList) {
        SupplyMethod supplyMethod = supplierController.getSupplierSupplyMethod(supplierID);
        SupplyContract contract = supplyContractController.registerNewContract(supplierID, dataList, supplyMethod);
        supplierController.addNewContractToSupplier(supplierID, contract);
    }

    public boolean deleteSupplier(int supplierID) {
        return this.supplierController.deleteSupplier(supplierID);
    }

    public String[] getAllSuppliersAsString() {
        return this.supplierController.getAllSuppliersAsString();
    }

    public String getSupplierAsString(int supplierID) {
        return this.supplierController.getSupplierAsString(supplierID);
    }

    public DeliveringMethod getSupplierDeliveringMethod(int supplierID) {
        return supplierController.getSupplierDeliveringMethod(supplierID);
    }

    public ContactInfo getSupplierContactInfo(int supplierID) {
        return supplierController.getSupplierContactInfo(supplierID);
    }
//    private SupplyContract getSupplierSupplyContract(int supplierID) {
//
//    }

    public ProductCategory getSupplierProductCategory(int supplierID) {
        return this.supplierController.getSupplierProductCategory(supplierID);
    }

    // --------------------------- ORDER FUNCTIONS ---------------------------
    public boolean registerNewOrder(int supplierId, ArrayList<int[]> dataList, Date creationDate, Date deliveryDate) {
        int contractId = supplierController.getSupplierContractId(supplierId);
        DeliveringMethod deliveringMethod = supplierController.getSupplierDeliveringMethod(supplierId);
        ContactInfo contactInfo = supplierController.getSupplierContactInfo(supplierId);
        ArrayList<SupplyContractProductData> list = supplyContractController.getSupplyContractProductDataArrayList(contractId);
        if (list == null) {
            return false;
        }
        for (int[] entry : dataList) {
            int productId = entry[0];
            boolean found = false;

            for (SupplyContractProductData data : list) {
                if (data.getProductID() == productId) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                return false;
            }
        }
        double totalPrice = 0;
        for(int[] entry : dataList) {
            SupplyContractProductData data = null;
            int productId = entry[0];
            int quantity = entry[1];
            double productPrice = data.getProductPrice();
            for (SupplyContractProductData data2 : list) {
                if (data2.getProductID() == productId) {
                    data = data2;
                }
            }
            if(quantity >=  data.getQuantityForDiscount()){
                 productPrice = data.getProductPrice();
                productPrice = productPrice*((100 - data.getDiscountPercentage())/100);
                totalPrice +=  productPrice*quantity;
            }
            else {
                totalPrice += productPrice*quantity;
            }
            }
        orderController.registerOrder(supplierId, dataList, totalPrice, creationDate, deliveryDate, deliveringMethod, contactInfo);
        return true;
        }

    public boolean updateOrderContactInfo(int orderId, String phoneNumber, String address, String email, String contactName){
        return orderController.updateOrderContactInfo(orderId, phoneNumber, address, email, contactName);
    }
    public boolean updateOrderSupplyDate(int orderID, Date supplyDate){
        return orderController.updateOrderSupplyDate(orderID, supplyDate);
    }
    public void updateOrderSupplyMethod(){

    }
    public void removeProductsFromOrder(){

    }
    public void addProductsToOrder(){

    }
    public boolean updateOrderSupplyMethod(int orderID, int supplyMethod){
        return orderController.updateOrderSupplyMethod(orderID, supplyMethod);
    }



    public boolean deleteOrder(int orderID) {
        return orderController.deleteOrder(orderID);
    }

    public boolean printOrder(int orderID) {
        return orderController.printOrder(orderID);
    }

    public void printAllOrders() {
        orderController.printAllOrders();
    }

    public String getContractToString(int contractID) {
        return supplyContractController.getContractToString(contractID);
    }
    public Date getOrderSupplyDate(int orderID){
        return orderController.getOrderSupplyDate(orderID);
    }
    public Date getSupplyDate(int orderID){
        return orderController.getSupplyDate(orderID);
    }

}
