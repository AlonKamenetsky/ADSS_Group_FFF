package IntegrationInventoryAndSupplier;

import SuppliersModule.ServiceLayer.SupplierService;

import java.util.List;

public interface SupplierInterface {


     public SupplierService GetInstance();
    // with this method inventory module will ask supplier module to order a single product that is out of stock
    public void placeUrgentOrderSingleProduct(int ItemID, int quantity);

    public List<MutualProduct> getAllAvailableProduct();

    SupplierInterface getInstance();
}
