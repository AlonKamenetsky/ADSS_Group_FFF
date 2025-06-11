package SuppliersModule.Tests;

import SuppliersModule.DomainLayer.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ContractTests {

    /* ---------- SupplyContractProductData ---------- */

    @Test
    void scProduct_constructor_sets_all_fields() {
        SupplyContractProductData d =
                new SupplyContractProductData(11, 200, 7.5, 100, 0.10);
        assertAll(
                () -> assertEquals(11,  d.getContractID()),
                () -> assertEquals(200, d.getProductID()),
                () -> assertEquals(7.5, d.getProductPrice(), 1e-9),
                () -> assertEquals(100, d.getQuantityForDiscount()),
                () -> assertEquals(0.10, d.getDiscountPercentage(), 1e-9)
        );
    }

    @Test
    void scProduct_setters_modify_fields() {
        SupplyContractProductData d =
                new SupplyContractProductData(1, 2, 3.0, 4, 0);
        d.setProductPrice(9.9);
        d.setQuantityForDiscount(20);
        assertAll(
                () -> assertEquals(9.9, d.getProductPrice(), 1e-9),
                () -> assertEquals(20, d.getQuantityForDiscount())
        );
    }

    @Test
    void scProduct_toString_exact() {
        SupplyContractProductData d =
                new SupplyContractProductData(1, 2, 3, 4, 0);
        String expected = "ContractID: 1 productID: 2 Price: 3.00 Quantity For Discount: 4 Discount Percentage: 0.00";
        assertEquals(expected, d.toString());
    }

    /* ---------- SupplyContract ---------- */

    @Test
    void supplyContract_add_product_increases_list() {
        SupplyContract c = new SupplyContract(3, 42);
        c.addSupplyContractProductData(
                new SupplyContractProductData(42, 10, 1, 10, 0));
        assertEquals(1, c.getSupplyContractProductData().size());
    }

    @Test
    void supplyContract_product_list_non_null() {
        SupplyContract c = new SupplyContract(3, 99);
        ArrayList<SupplyContractProductData> list = c.getSupplyContractProductData();
        assertNotNull(list);
    }

    @Test
    void supplyContract_toString_contains_contract_id() {
        SupplyContract c = new SupplyContract(1, 77);
        assertTrue(c.toString().contains("Contract ID: 77"));
    }

    @Test
    void supplyContract_supplier_id_matches_constructor_arg() {
        SupplyContract c = new SupplyContract(5, 101);
        assertEquals(5, c.getSupplierID());
    }
}
