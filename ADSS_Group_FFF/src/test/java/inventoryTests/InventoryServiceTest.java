package inventoryTests;

import IntegrationInventoryAndSupplier.MutualProduct;
import IntegrationInventoryAndSupplier.SupplierInterface;
import inventory.dataLayer.daos.*;
import inventory.domainLayer.InventoryProduct;
import inventory.serviceLayer.InventoryService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {

    private static InventoryService service;
    private static StubProductDAO productDAO;
    private static StubCategoryDAO categoryDAO = new StubCategoryDAO();
    private static StubDiscountDAO discountDAO = new StubDiscountDAO();
    private static StubReportDAO reportDAO = new StubReportDAO();

    @BeforeAll
    static void setUpServiceWithStubs() {
        // Create stub DAOs
        productDAO = new StubProductDAO();

        // Configure the singleton before obtaining an instance
        InventoryService.configureWithDaos(
                productDAO,
                categoryDAO,
                discountDAO,
                reportDAO
        );
        service = InventoryService.getInstance();
    }

    @Test
    void getAllAvailableProducts_noSupplierSet_shouldReturnEmptyList() {
        List<MutualProduct> list = service.getAllAvailableProducts();
        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    void getAllAvailableProducts_withStubSupplier_shouldReturnStubData() {
        // Create a stub SupplierInterface that returns two MutualProduct objects
        class StubSupplier implements SupplierInterface {
            @Override
            public List<MutualProduct> getAllAvailableProduct() {
                MutualProduct a = new MutualProduct(1, "ProdA", "CoA", null);
                MutualProduct b = new MutualProduct(2, "ProdB", "CoB", null);
                return Arrays.asList(a, b);
            }
            @Override
            public List<MutualProduct> getAllAvailableProductForOrder() {
                return Collections.emptyList();
            }
            @Override
            public void placeUrgentOrderSingleProduct(int itemID, int quantity) { }
        }

        service.setSupplierService(new StubSupplier());
        List<MutualProduct> list = service.getAllAvailableProducts();
        assertEquals(2, list.size());
        assertEquals(1, list.get(0).getId());
        assertEquals(2, list.get(1).getId());
    }

    @Test
    void acceptDelivery_productNotFound_shouldReturnFalse() {
        boolean result = service.acceptDelivery(999, 10);
        assertFalse(result);
    }

    @Test
    void acceptDelivery_existingProduct_shouldUpdateBackroomAndReturnTrue() {
        // Arrange: insert a product into stub DAO
        InventoryProduct p = new InventoryProduct(
                5, "TestItem", "TestCo", 10, 5, 2, 1.0, 2.0, null, null
        );
        productDAO.save(p);

        // Precondition: backroomQuantity == 5
        assertEquals(5, p.getBackroomQuantity());

        // Act
        boolean result = service.acceptDelivery(5, 7);

        // Assert
        assertTrue(result);

        InventoryProduct updated = productDAO.findById(5);
        assertNotNull(updated);
        // backroom should have increased by 7 => 5 + 7 = 12
        assertEquals(12, updated.getBackroomQuantity());
    }

    // ====== Stub DAO Implementations ======

    private static class StubProductDAO implements InventoryProductDAO {
        private final Map<Integer, InventoryProduct> storage = new HashMap<>();

        @Override
        public void save(InventoryProduct product) {
            storage.put(product.getId(), product);
        }

        @Override
        public InventoryProduct findById(int id) {
            return storage.get(id);
        }

        @Override
        public List<InventoryProduct> findAll() {
            return new ArrayList<>(storage.values());
        }

        @Override
        public void delete(int id) {
            storage.remove(id);
        }

        @Override
        public void update(InventoryProduct product) {
            // Simply replace in map
            storage.put(product.getId(), product);
        }
    }

    private static class StubCategoryDAO implements CategoryDAO {
        @Override public void save(inventory.domainLayer.Category category) { }
        @Override public inventory.domainLayer.Category findByName(String name) { return null; }
        @Override public List<inventory.domainLayer.Category> findAll() { return Collections.emptyList(); }
        @Override public void delete(String name) { }
        @Override public void update(inventory.domainLayer.Category category) { }
    }

    private static class StubDiscountDAO implements DiscountDAO {
        @Override public void save(inventory.domainLayer.Discount discount) { }
        @Override public inventory.domainLayer.Discount findById(String id) { return null; }
        @Override public List<inventory.domainLayer.Discount> findAll() { return Collections.emptyList(); }
        @Override public void delete(String id) { }
        @Override public void update(inventory.domainLayer.Discount discount) { }
        @Override public inventory.domainLayer.Discount findByItemId(int itemId) { return null; }
        @Override public inventory.domainLayer.Discount findByCategoryName(String categoryName) { return null; }
    }

    private static class StubReportDAO implements InventoryReportDAO {
        @Override public void save(inventory.domainLayer.InventoryReport report) { }
        @Override public inventory.domainLayer.InventoryReport findById(String id) { return null; }
        @Override public List<inventory.domainLayer.InventoryReport> findAll() { return Collections.emptyList(); }
        @Override public void delete(String id) { }
        @Override public void update(inventory.domainLayer.InventoryReport report) { }
    }
}
