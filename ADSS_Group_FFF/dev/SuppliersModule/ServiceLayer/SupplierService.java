package SuppliersModule.ServiceLayer;

import SuppliersModule.DomainLayer.SupplierController;

import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.PaymentMethod;
import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;

import java.util.ArrayList;

public class SupplierService {
    SupplierController supplierController;

    public SupplierService() {
        supplierController = new SupplierController();
    }

    public int RegisterNewSupplier(int supplyMethod, String supplierName, int productCategory, int deliveringMethod,
                                    String phoneNumber, String address, String email, String contactName,
                                    String bankAccount, int paymentMethod) {
        SupplyMethod sm = SupplyMethod.values()[supplyMethod];
        ProductCategory pc = ProductCategory.values()[productCategory];
        DeliveringMethod dm = DeliveringMethod.values()[deliveringMethod];
        PaymentMethod pm = PaymentMethod.values()[paymentMethod];
        return this.supplierController.RegisterNewSupplier(sm, supplierName, pc, dm, phoneNumber, address, email, contactName, bankAccount, pm);
    }

    public boolean UpdateSupplierName(int supplierID, String supplierName) {
        return this.supplierController.UpdateSupplierName(supplierID, supplierName);
    }

    public boolean UpdateSupplierDeliveringMethod(int supplierID, int deliveringMethod) {
        DeliveringMethod dm = DeliveringMethod.values()[deliveringMethod];
        return  this.supplierController.UpdateSupplierDeliveringMethod(supplierID, dm);
    }

    public boolean UpdateSupplierContactInfo(int supplierID, String phoneNumber, String address, String email, String contactName) {
        return this.supplierController.UpdateSupplierContactInfo(supplierID, phoneNumber, address, email, contactName);
    }

    public boolean UpdateSupplierPaymentInfo(int supplierId, String bankAccount, int paymentMethod) {
        PaymentMethod pm = PaymentMethod.values()[paymentMethod];
        return this.supplierController.UpdateSupplierPaymentInfo(supplierId, bankAccount, pm);
    }

    public void RegisterNewContract(int supplierID, ArrayList<int[]> dataList) {
        this.supplierController.RegisterNewContract(supplierID, dataList);
    }

    public boolean DeleteSupplier(int supplierID) {
        return this.supplierController.DeleteSupplier(supplierID);
    }

    public String[] GetAllSuppliersAsString() {
        return this.supplierController.GetAllSuppliersAsString();
    }

    public String GetSupplierAsString(int supplierID) {
        return this.supplierController.GetSupplierAsString(supplierID);
    }

    public ProductCategory GetSupplierProductCategory(int supplierID) {
        return this.supplierController.GetSupplierProductCategory(supplierID);
    }
}
