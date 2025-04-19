package SuppliersModule.DomainLayer;

import java.util.ArrayList;

public class SupplierController {
    OrderController orderController;
    SupplyContractController supplyContractController;

    ArrayList<Supplier> suppliersArrayList; // TEMP DATA STRUCTURE

    public SupplierController() {
        this.suppliersArrayList = new ArrayList<Supplier>();
    }
    public void RegisterNewSupplier(Supplier supplier) {
        this.suppliersArrayList.add(supplier);
    }
    public void DeleteSupplier(int supplierID) {
        this.suppliersArrayList.removeIf(supplier -> supplier.supplierId == supplierID);
    }
    public ArrayList<Supplier> GetAllSuppliers() {
        return this.suppliersArrayList;
    }
}
