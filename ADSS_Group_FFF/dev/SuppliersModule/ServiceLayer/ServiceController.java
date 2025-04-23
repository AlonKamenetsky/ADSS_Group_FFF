package SuppliersModule.ServiceLayer;

import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.PaymentMethod;
import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;

import java.util.ArrayList;

public class ServiceController {
    SupplierService supplierService;
    ProductService productService;

    public ServiceController() {
        this.supplierService = new SupplierService();
        this.productService = new ProductService();
    }

    // --------------------------- VALIDATION FUNCTIONS ---------------------------

    private boolean ValidateProductCategory(int productCategory) {
        return (productCategory > 0 && productCategory < ProductCategory.values().length);
    }

    private boolean ValidateSupplyMethod(int supplyMethod) {
        return (supplyMethod > 0 && supplyMethod < SupplyMethod.values().length);
    }

    private boolean ValidateDeliveringMethod(int deliveringMethod) {
        return (deliveringMethod > 0 && deliveringMethod < DeliveringMethod.values().length);
    }

    private boolean ValidatePaymentMethod(int paymentMethod) {
        return (paymentMethod > 0 && paymentMethod < PaymentMethod.values().length);
    }

    private boolean ValidateSupplierAndProduct(int supplierID, int productID) {
        return this.supplierService.GetSupplierProductCategory(supplierID) == this.productService.GetProductCategory(productID);
    }

    private boolean ValidateContractProductData(int price, int quantityForDiscount, int discountPercentage) {
        return price <= 0 || quantityForDiscount <= 0 || !(discountPercentage > 0 && discountPercentage < 100);
    }

    // --------------------------- PRODUCT FUNCTIONS ---------------------------

    public int RegisterNewProduct(String productName, String productCompanyName, int productCategory) {
        if (ValidateProductCategory(productCategory))
            return this.productService.RegisterNewProduct(productName, productCompanyName, ProductCategory.values()[productCategory]);
        return -1;
    }

    public boolean UpdateProduct(int productID, String productName, String productCompanyName, int productCategory) {
        if (ValidateProductCategory(productCategory))
            return this.productService.UpdateProduct(productID, productName, productCompanyName, ProductCategory.values()[productCategory]);
        return false;
    }

    public boolean DeleteProduct(int productID) {
        return this.productService.DeleteProduct(productID);
    }

    public String[] GetAllProductsAsStrings() {
        return this.productService.GetProductsAsString();
    }

    public String GetProductAsString(int productID) {
        return this.productService.GetProductAsString(productID);
    }

    // --------------------------- SUPPLIER FUNCTIONS ---------------------------

    public int RegisterNewSupplier(int supplyMethod, String supplierName, int productCategory, int deliveringMethod,
                                    String phoneNumber, String address, String email, String contactName,
                                    String bankAccount, int paymentMethod) {
        if (this.ValidateProductCategory(productCategory) && this.ValidateSupplyMethod(supplyMethod) && this.ValidateDeliveringMethod(deliveringMethod) && this.ValidatePaymentMethod(paymentMethod))
            return this.supplierService.RegisterNewSupplier(supplyMethod, supplierName, productCategory, deliveringMethod, phoneNumber, address, email, contactName, bankAccount, paymentMethod);
        return -1;
    }

    public boolean UpdateSupplierName(int supplierID, String supplierName) {
        return this.supplierService.UpdateSupplierName(supplierID, supplierName);
    }

    public boolean UpdateSupplierDeliveringMethod(int supplierID, int deliveringMethod) {
        if (this.ValidateDeliveringMethod(deliveringMethod))
            return this.supplierService.UpdateSupplierDeliveringMethod(supplierID, deliveringMethod);
        return false;
    }

    public boolean UpdateSupplierContactInfo(int supplierID, String phoneNumber, String address, String email, String contactName) {
        return this.supplierService.UpdateSupplierContactInfo(supplierID, phoneNumber, address, email, contactName);
    }

    public boolean UpdateSupplierPaymentInfo(int supplierId, String bankAccount, int paymentMethod) {
        if (this.ValidatePaymentMethod(paymentMethod))
            return this.supplierService.UpdateSupplierPaymentInfo(supplierId, bankAccount, paymentMethod);
        return false;
    }

    public boolean RegisterNewContract(int supplierID, ArrayList<int[]> dataList) {
        for (int[] data : dataList)
            if (!ValidateSupplierAndProduct(supplierID, data[0]) || !ValidateContractProductData(data[1], data[2], data[3]))
                return false;

        this.supplierService.RegisterNewContract(supplierID, dataList);
        return true;
    }

    public boolean DeleteSupplier(int supplierID) {
        return supplierService.DeleteSupplier(supplierID);
    }

    public String[] GetAllSuppliersAsString() {
        return this.supplierService.GetAllSuppliersAsString();
    }

    public String GetSupplierAsString(int supplierID) {
        return this.supplierService.GetSupplierAsString(supplierID);
    }

//    public void PrintSupplierContracts(int supplierID) {
//        supplierService.PrintAllSupplierContracts(supplierID);
//    }
//
//    public void registerNewSupplierContract(){
//
//    }

}
