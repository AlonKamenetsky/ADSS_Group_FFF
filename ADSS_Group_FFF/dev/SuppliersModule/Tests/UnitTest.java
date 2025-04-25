package SuppliersModule.Tests;

import SuppliersModule.DomainLayer.*;
import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.PaymentMethod;
import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.PresentationLayer.CLI;
import org.junit.jupiter.api.Test;  // ← JUnit 5

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class UnitTest {
    //-------------------Product--------------------
    @Test
    public void testProduct() {
        Product product = new Product(0, "bamba", "Osem", ProductCategory.DRIED);
        assertEquals("bamba", product.getProductName());
        assertEquals(0, product.getProductId());
        assertEquals("Osem", product.getProductCompanyName());
        assertEquals(ProductCategory.DRIED, product.getProductCategory());
    }
    @Test
    public void testProductSetters(){
        Product product = new Product(0, "bamba", "Osem", ProductCategory.DRIED);
        product.setProductName("bisli");
        product.setProductId(1);
        product.setProductCategory(ProductCategory.DAIRY);
        product.setProductCompanyName("tnuva");
        assertEquals("bisli", product.getProductName());
        assertEquals(1, product.getProductId());
        assertEquals(ProductCategory.DAIRY, product.getProductCategory());
        assertEquals("tnuva", product.getProductCompanyName());
    }


    //-------------------Supplier--------------------
    @Test
    public void testScheduledSupplier(){
        ContactInfo contactInfo= new ContactInfo("054", "yehuda", "@", "alon");
        PaymentInfo paymentInfo = new PaymentInfo("888", PaymentMethod.BANK_TRANSACTION);
        Supplier supplier = new ScheduledSupplier(0, "osem", ProductCategory.DAIRY, DeliveringMethod.SELF_DELIVERING, contactInfo, paymentInfo);
        assertEquals("osem", supplier.getSupplierName());
        assertEquals(ProductCategory.DAIRY, supplier.getSupplierProductCategory());
        assertEquals(DeliveringMethod.SELF_DELIVERING, supplier.getSupplierDeliveringMethod());

    }
    @Test
    public void testOnDemandSupplier(){
        ContactInfo contactInfo= new ContactInfo("054", "yehuda", "@", "alon");
        PaymentInfo paymentInfo = new PaymentInfo("888", PaymentMethod.BANK_TRANSACTION);
        Supplier supplier = new OnDemandSupplier(0, "osem", ProductCategory.DAIRY, DeliveringMethod.SELF_DELIVERING, contactInfo, paymentInfo);
    }

    //-------------------SupplyContract--------------------
    @Test
    void testAddSupplyContractProductData() {
        SupplyContract contract = new SupplyContract(1, 1001);
        SupplyContractProductData data = new SupplyContractProductData(10, 5.5, 3, 10.0);

        contract.addSupplyContractProductData(data);

        assertEquals(1, contract.getSupplyContractProductData().size());
        assertEquals(10, contract.getSupplyContractProductData().get(0).getProductID());
    }

    @Test
    void testGetSupplyContractProductDataOfProduct_Found() {
        SupplyContract contract = new SupplyContract(1, 1001);
        SupplyContractProductData data = new SupplyContractProductData(10, 5.5, 3, 10.0);
        contract.addSupplyContractProductData(data);

        SupplyContractProductData result = contract.getSupplyContractProductDataOfProduct(10);

        assertNotNull(result);
        assertEquals(10, result.getProductID());
    }

    @Test
    void testGetSupplyContractProductDataOfProduct_NotFound() {
        SupplyContract contract = new SupplyContract(1, 1001);

        SupplyContractProductData result = contract.getSupplyContractProductDataOfProduct(999);

        assertNull(result);
    }

    @Test
    void testCheckIfProductInData() {
        SupplyContract contract = new SupplyContract(1, 1001);
        contract.addSupplyContractProductData(new SupplyContractProductData(1, 10.0, 5, 5.0));

        assertTrue(contract.CheckIfProductInData(1));
        assertFalse(contract.CheckIfProductInData(999));
    }
    //-------------------Order--------------------
    //-------------------ContactInfo--------------------
    @Test
    public void testContactInfo(){
        ContactInfo contactInfo = new ContactInfo("054", "yehuda halevi", "@gmail.com", "alon");
        assertEquals("054", contactInfo.getPhoneNumber());
        assertEquals("yehuda halevi", contactInfo.getAddress());
        assertEquals("@gmail.com", contactInfo.getEmail());
        assertEquals("alon", contactInfo.getName());
        contactInfo.setAddress("yehuda");
        contactInfo.setEmail("@");
        contactInfo.setName("eran");
        contactInfo.setPhoneNumber("052");
        assertEquals("yehuda", contactInfo.getAddress());
        assertEquals("@", contactInfo.getEmail());
        assertEquals("eran", contactInfo.getName());
        assertEquals("052", contactInfo.getPhoneNumber());
    }
    //-------------------PaymentInfo--------------------
    @Test
    public void testPaymentInfo(){
        PaymentInfo paymentInfo= new PaymentInfo("888", PaymentMethod.BANK_TRANSACTION);
        assertEquals("888", paymentInfo.getSupplierBankAccount());
        assertEquals(PaymentMethod.BANK_TRANSACTION, paymentInfo.getSupplierPaymentMethod());
        paymentInfo.setSupplierPaymentMethod(PaymentMethod.CASH);
        paymentInfo.setSupplierBankAccount("777");
        assertEquals("777", paymentInfo.getSupplierBankAccount());
        assertEquals(PaymentMethod.CASH, paymentInfo.getSupplierPaymentMethod());
    }@Test
    public void testRegisternewproduct() {
        String simulatedInput = String.join("\n", "Milk\nTnuva\n0");
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(inputStream);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        CLI cli = new CLI();
        cli.registerNewProduct();

        String output = outContent.toString();
        assertFalse(output.isEmpty());
    }

    @Test
    public void testDeleteproduct() {
        String simulatedInput = String.join("\n", "1");
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(inputStream);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        CLI cli = new CLI();
        cli.deleteProduct();

        String output = outContent.toString();
        assertFalse(output.isEmpty());
    }

    @Test
    public void testPrintproduct() {
        String simulatedInput = String.join("\n", "1");
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(inputStream);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        CLI cli = new CLI();
        cli.printProduct();

        String output = outContent.toString();
        assertFalse(output.isEmpty());
    }

    @Test
    public void testPrintallproducts() {
        String simulatedInput = String.join("\n", "");
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(inputStream);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        CLI cli = new CLI();
        cli.printAllProducts();

        String output = outContent.toString();
        assertFalse(output.isEmpty());
    }

    @Test
    public void testRegisternewsupplier() {
        String simulatedInput = String.join("\n",
                "Tnuva",           // name
                "0",               // category
                "123456",          // bank account
                "1",               // payment method
                "0",               // delivery method
                "0531234567",      // phone
                "Tel Aviv",        // address
                "email@example.com", // email
                "David",           // contact name
                "0",               // supply type
                "-1"               // end contract
        );
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(inputStream);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        CLI cli = new CLI();
        cli.registerNewSupplier();

        String output = outContent.toString();
        assertFalse(output.isEmpty());
    }

}
