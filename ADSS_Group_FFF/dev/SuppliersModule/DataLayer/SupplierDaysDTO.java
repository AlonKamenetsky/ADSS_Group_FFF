package SuppliersModule.DataLayer;

import java.io.IOException;
import java.sql.SQLException;

public class SupplierDaysDTO extends DTO {
    public int supplierID;
    public String day;
    public int productID;
    public int productQuantity;
    public double productPrice;

    public static String ID_COLUMN_NAME = "id";
    public static String DAY_COLUMN_NAME = "day";
    public static String PRODUCT_ID_COLUMN_NAME = "product_id";
    public static String PRODUCT_QUANTITY_COLUMN_NAME = "product_quantity";
    public static String PRODUCT_PRICE_COLUMN_NAME = "product_price";

    public SupplierDaysDTO(int supplierID, String day, int productID, int productQuantity, double productPrice)  {
        super(SupplierControllerDTO.getInstance());

        this.supplierID = supplierID;
        this.day = day;
        this.productID = productID;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
    }

    public void Insert() {
        SupplierControllerDTO controller = (SupplierControllerDTO) this.dbController;
        controller.insertSupplierDays(this);
    }

    public void Delete() {
        SupplierControllerDTO controller = (SupplierControllerDTO) this.dbController;
        controller.deleteSupplierDays(this);
    }
}
