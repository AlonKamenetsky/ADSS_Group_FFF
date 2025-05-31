//package IntegrationInventoryAndSupplier;
//
//import java.util.List;
//
//public class FakeSupplierService implements SupplierService {
//
//    @Override
//    public List<SupplierQuote> getQuotesSingleProduct(String productId) {
//        return List.of(
//                new SupplierQuote("SUP123", productId, 9.99, 2, 10),
//                new SupplierQuote("SUP456", productId, 11.25, 1, 5)
//        );
//    }
//
//    @Override
//    public OrderConfirmation placeOrderSingleProduct(SupplierOrder order) {
//        System.out.println("FakeSupplierService: Order placed -> " + order);
//        return new OrderConfirmation(true);
//    }
//}
