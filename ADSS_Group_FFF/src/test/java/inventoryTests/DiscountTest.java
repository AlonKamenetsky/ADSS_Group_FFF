package inventoryTests;

import inventory.domainLayer.Category;
import inventory.domainLayer.Discount;
import inventory.domainLayer.InventoryProduct;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DiscountTest {

    @Test
    void constructorAndGetters_withCategoryAndItem_shouldExposeAllFields() {
        Category cat = new Category("Books", null);
        InventoryProduct item = new InventoryProduct(
                42,
                "Effective Java",
                "Pearson",
                10,
                20,
                5,
                30.0,
                45.0,
                null,
                cat
        );
        Date start = new Date(1_600_000_000_000L);
        Date end = new Date(1_600_086_400_000L);
        Discount discount = new Discount("DISC1", 15.0, start, end, cat, item);

        assertEquals("DISC1", discount.getId());
        assertEquals(15.0, discount.getPercent());
        assertEquals(start, discount.getStartDate());
        assertEquals(end, discount.getEndDate());
        assertEquals("Books", discount.getAppliesToCategory().getName());
        assertEquals("Effective Java", discount.getAppliesToItem().getName());
    }

    @Test
    void toString_containsAllKeyFieldsOrNone() {
        Discount d = new Discount("XOFF", 25.0, new Date(1_600_000_000_000L),
                new Date(1_600_086_400_000L), null, null);

        String repr = d.toString();
        assertTrue(repr.contains("id='XOFF'"));
        assertTrue(repr.contains("percent=25.0"));
        assertTrue(repr.contains("category=none"));
        assertTrue(repr.contains("item=none"));
    }
}

