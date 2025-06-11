package SuppliersModule.DataLayer;

import java.io.IOException;
import java.sql.SQLException;

public class SupplierDTO extends DTO {
    public int supplierID;
    public String supplierName;
    public String productCategory;
    public String deliveryMethod;
    public String contactName;
    public String emailAddress;
    public String phoneNumber;
    public String address;
    public String bankAccount;
    public String paymentMethod;
    public String supplyMethod;

    public static String ID_COLUMN_NAME = "id";
    public static String NAME_COLUMN_NAME = "name";
    public static String PRODUCT_CATEGORY_COLUMN_NAME = "product_category";
    public static String DELIVERY_METHOD_COLUMN_NAME = "delivery_method";
    public static String CONTACT_NAME_COLUMN_NAME = "contact_name";
    public static String EMAIL_ADDRESS_COLUMN_NAME = "email";
    public static String PHONE_NUMBER_COLUMN_NAME = "phone_number";
    public static String ADDRESS_COLUMN_NAME = "address";
    public static String BANK_ACCOUNT_COLUMN_NAME = "bank_account";
    public static String PAYMENT_METHOD_COLUMN_NAME = "payment_method";
    public static String SUPPLY_METHOD_COLUMN_NAME = "supply_method";

    public SupplierDTO(int SupplierID, String SupplierName, String productCategory, String deliveryMethod,
                       String contactName, String emailAddress, String phoneNumber, String address,
                       String bankAccount, String paymentMethod, String supplyMethod) {

        super(SupplierControllerDTO.getInstance());

        this.supplierID = SupplierID;
        this.supplierName = SupplierName;
        this.productCategory = productCategory;
        this.deliveryMethod = deliveryMethod;
        this.contactName = contactName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.bankAccount = bankAccount;
        this.paymentMethod = paymentMethod;
        this.supplyMethod = supplyMethod;
    }

    public void Insert(){
        SupplierControllerDTO controller = (SupplierControllerDTO) this.dbController;
        controller.insertSupplier(this);
    }

    public void Delete(){
        SupplierControllerDTO controller = (SupplierControllerDTO) this.dbController;
        controller.deleteSupplier(this);
    }
}
