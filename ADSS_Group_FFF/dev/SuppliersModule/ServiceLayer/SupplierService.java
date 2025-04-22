package SuppliersModule.ServiceLayer;

import SuppliersModule.DomainLayer.*;
import SuppliersModule.DomainLayer.Enums.DeliveringMethod;

import java.util.ArrayList;

public class SupplierService {
    SupplierController supplierController;

    public SupplierService() {
        supplierController = new SupplierController();
    }

    public void RegisterNewSupplier(Supplier supplier){
        this.supplierController.RegisterNewSupplier(supplier);
    }

    public void UpdateSupplier(int supplierID, String supplierName, PaymentInfo paymentInfo , DeliveringMethod deliveringMethod, ContactInfo contactInfo){
        this.supplierController.UpdateSupplier(supplierID, supplierName, paymentInfo , deliveringMethod, contactInfo);
    }

    public void DeleteSupplier(int supplierID){
        this.supplierController.DeleteSupplier(supplierID);
    }

    public ArrayList<Supplier> GetAllSuppliers() {
        return this.supplierController.GetAllSuppliers();
    }

    public Supplier GetSupplier(int supplierID) {
        return this.supplierController.GetSupplier(supplierID);
    }

    public void PrintAllSupplierContracts(int supplierID) {
        supplierController.PrintSupplierContracts(supplierID);
    }

    public void RegisterNewSupplierContract(int suppplierID, SupplyContract supplyContract) {
        this.supplierController.RegisterNewSupplierContract(suppplierID, supplyContract);
    }
}
