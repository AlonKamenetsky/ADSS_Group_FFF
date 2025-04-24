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

    public SupplierService() {
        supplierController = new SupplierController();
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


    // אין סיבה כי מחלקות הסרביס לייר ומעלה לא אמורות להכיר את האובייקטים האלו
//    public ContactInfo getSupplierContactInfo(int supplierID) {
//        return supplierController.getSupplierContactInfo(supplierID);
//    }
//    private SupplyContract getSupplierSupplyContracts(int supplierID) {
//        return this.supplierController.getSupp;
//    }

    public ProductCategory getSupplierProductCategory(int supplierID) {
        return this.supplierController.getSupplierProductCategory(supplierID);
    }

    // --------------------------- CONTRACT FUNCTIONS ---------------------------

    public boolean registerNewContract(int supplierID, ArrayList<int[]> dataList) {
        return this.supplierController.registerNewContract(supplierID, dataList);
    }

    // --------------------------- ORDER FUNCTIONS ---------------------------

    public boolean registerNewOrder(int supplierId, ArrayList<int[]> dataList, Date creationDate, Date deliveryDate) {
        return this.supplierController.registerNewOrder(supplierId, dataList, creationDate, deliveryDate);
    }

    public boolean updateOrderContactInfo(int orderId, String phoneNumber, String address, String email, String contactName){
        return this.supplierController.updateOrderContactInfo(orderId, phoneNumber, address, email, contactName);
    }

    public boolean updateOrderSupplyDate(int orderID, Date supplyDate){
        return this.supplierController.updateOrderSupplyDate(orderID, supplyDate);
    }

    public void updateOrderSupplyMethod() {

    }

    public void removeProductsFromOrder() {

    }

    public void addProductsToOrder() {

    }

    public boolean updateOrderSupplyMethod(int orderID, int supplyMethod){
        return this.supplierController.updateOrderSupplyMethod(orderID, supplyMethod);
    }

    public boolean deleteOrder(int orderID) {
        return this.supplierController.deleteOrder(orderID);
    }

    public String getContractToString(int contractID) {
        return this.supplierController.getContractToString(contractID);
    }

    public Date getOrderSupplyDate(int orderID){
        return this.supplierController.getOrderSupplyDate(orderID);
    }

    public String getOrderAsString(int orderID) {
        return this.supplierController.getOrderAsString(orderID);
    }

    public String[] getAllOrdersAsString() {
        return this.supplierController.getAllOrdersAsString();
    }
}
