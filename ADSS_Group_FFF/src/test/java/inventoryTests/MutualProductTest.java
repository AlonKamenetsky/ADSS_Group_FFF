package inventoryTests;

import IntegrationInventoryAndSupplier.MutualProduct;
import SuppliersModule.DomainLayer.Enums.ProductCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MutualProductTest {

    @Test
    void constructorAndGetters_shouldReturnCorrectValues() {
        ProductCategory cat = ProductCategory.FRUITS_AND_VEGETABLES;
        MutualProduct mp = new MutualProduct(100, "Gadget", "GadgetCorp", cat);

        assertEquals("Gadget", mp.getName());
        assertEquals("GadgetCorp", mp.getManufacturer());
        assertEquals(cat, mp.getProductCategory());
    }
}
