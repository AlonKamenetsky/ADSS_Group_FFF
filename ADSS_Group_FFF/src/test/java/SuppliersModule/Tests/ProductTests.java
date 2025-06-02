package SuppliersModule.Tests;

import SuppliersModule.DomainLayer.*;

import SuppliersModule.DomainLayer.Enums.ProductCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTests {

    @Test
    void constructor_sets_all_fields() {
        Product p = new Product(1, "Widget", "Acme", ProductCategory.DRIED);
        assertAll(
                () -> assertEquals(1, p.getProductId()),
                () -> assertEquals("Widget", p.getProductName()),
                () -> assertEquals("Acme", p.getProductCompanyName()),
                () -> assertEquals(ProductCategory.DRIED, p.getProductCategory())
        );
    }

    @Test
    void set_name_updates_property() {
        Product p = new Product(7, "Tmp", "Foo", ProductCategory.FROZEN);
        p.setProductName("Bolt");
        assertEquals("Bolt", p.getProductName());
    }

    @Test
    void set_category_updates_property() {
        Product p = new Product(7, "Tmp", "Foo", ProductCategory.FROZEN);
        p.setProductCategory(ProductCategory.MEATS);
        assertEquals(ProductCategory.MEATS, p.getProductCategory());
    }

    @Test
    void toString_has_exact_format() {
        Product p = new Product(3, "Nut", "Bar", ProductCategory.DAIRY);
        String expected = "3\tNut\tBar\tDAIRY";
        assertEquals(expected, p.toString());
    }

    @Test
    void productId_is_positive() {
        Product p = new Product(4, "Sprocket", "Foo", ProductCategory.DRINKS);
        assertTrue(p.getProductId() > 0);
    }

    @Test
    void productName_not_empty() {
        Product p = new Product(5, "Gear", "Foo", ProductCategory.MEATS);
        assertFalse(p.getProductName().isBlank());
    }

    @Test
    void productCategory_enum_non_null() {
        Product p = new Product(6, "Bolt", "Foo", ProductCategory.FROZEN);
        assertNotNull(p.getProductCategory());
    }
}
