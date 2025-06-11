package Integration4Modules.Interfaces;

import IntegrationInventoryAndSupplier.MutualProduct;

import java.util.List;

public interface SupplierInterface {

//    static ServiceController GetInstance() {
//        return null;
//    }

    // What about Add product?

    List<MutualProduct> getAllAvailableProduct();
    List<MutualProduct> getAllAvailableProductForOrder();

    // with this method inventory module will ask supplier module to order a single product that is out of stock
    void placeUrgentOrderSingleProduct(int ItemID, int quantity);
}
