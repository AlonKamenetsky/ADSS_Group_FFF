package inventoryTests;

import inventory.domainLayer.Category;
import inventory.domainLayer.InventoryProduct;
import inventory.domainLayer.ItemStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryProductTest {

    @Test
    void constructorAndGetters_shouldReturnCorrectValues() {
        Category cat = new Category("Toys", null);
        InventoryProduct product = new InventoryProduct(
                7,                    // id
                "Lego Set",           // name
                "Lego Co.",           // manufacturer
                100,                  // shelfQuantity
                200,                  // backroomQuantity
                50,                   // minThreshold
                20.0,                 // purchasePrice
                30.0,                 // salePrice
                ItemStatus.NORMAL,    // status
                cat                   // category
        );

        assertEquals(7, product.getId());
        assertEquals("Lego Set", product.getName());
        assertEquals("Lego Co.", product.getManufacturer());
        assertEquals(100, product.getShelfQuantity());
        assertEquals(200, product.getBackroomQuantity());
        assertEquals(50, product.getMinThreshold());
        assertEquals(20.0, product.getPurchasePrice());
        assertEquals(30.0, product.getSalePrice());
        assertEquals(ItemStatus.NORMAL, product.getStatus());
        assertNotNull(product.getCategory());
        assertEquals("Toys", product.getCategory().getName());
    }

    @Test
    void getTableHeader_shouldContainAllColumnTitles() {
        String header = InventoryProduct.getTableHeader();
        assertNotNull(header);
        assertTrue(header.contains("ID"));
        assertTrue(header.contains("Name"));
        assertTrue(header.contains("Manufacturer"));
        assertTrue(header.contains("Shelf"));
        assertTrue(header.contains("Backroom"));
        assertTrue(header.contains("Min"));
        assertTrue(header.contains("Status"));
        assertTrue(header.contains("Category"));
    }
}
