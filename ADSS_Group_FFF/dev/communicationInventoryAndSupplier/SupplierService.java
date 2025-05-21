package communicationInventoryAndSupplier;

import java.util.List;

public interface SupplierService {
    /**
     * Get a list of available supplier quotes for a given product.
     */
    List<SupplierQuote> getQuotesSingleProduct(String productId);

    /**
     * Place an order with a supplier and receive a confirmation.
     */
    OrderConfirmation placeOrderSingleProduct(SupplierOrder order);
}
