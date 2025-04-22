package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.DeliveringMethod;

import java.util.ArrayList;

public class SupplierController {
    OrderController orderController;

    ArrayList<Supplier> suppliersArrayList; // TEMP DATA STRUCTURE

    public SupplierController() {
        this.suppliersArrayList = new ArrayList<Supplier>();
    }

    public void RegisterNewSupplier(Supplier supplier) {
        this.suppliersArrayList.add(supplier);
    }

    public void UpdateSupplier(int supplierID, String supplierName, PaymentInfo paymentInfo, DeliveringMethod deliveringMethod, ContactInfo contactInfo) {
        Supplier supplier = suppliersArrayList.get(supplierID);
        if (supplierName != null)
            supplier.setSupplierName(supplierName);
        if (paymentInfo != null)
            supplier.setSupplierPaymentInfo(paymentInfo);
        if (deliveringMethod != null)
            supplier.setSupplierDeliveringMethod(deliveringMethod);
        if (contactInfo != null)
            supplier.setSupplierContactInfo(contactInfo);

        this.suppliersArrayList.set(supplierID, supplier);
    }

    public void DeleteSupplier(int supplierID) {
        this.suppliersArrayList.removeIf(supplier -> supplier.supplierId == supplierID);
    }

    public ArrayList<Supplier> GetAllSuppliers() {
        return this.suppliersArrayList;
    }

    public Supplier GetSupplier(int supplierID) {
        return this.suppliersArrayList.get(supplierID);
    }

    public void PrintSupplierContracts(int supplierID) {
        Supplier supplier = this.GetSupplier(supplierID);
        for (SupplyContract contract : supplier.getSupplierContracts()) {
            System.out.println(contract.toString());
        }
    }
    public void RegisterNewSupplierContract(int supplierID, SupplyContract supplyContract) {
        this.suppliersArrayList.get(supplierID).AddNewContract(supplyContract);
    }

}
