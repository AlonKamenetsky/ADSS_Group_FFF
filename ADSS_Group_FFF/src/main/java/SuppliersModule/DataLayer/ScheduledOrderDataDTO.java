package SuppliersModule.DataLayer;

public class ScheduledOrderDataDTO extends DTO{
    public int supplierID;
    public String day;
    public int productID;
    public int productQuantity;

    public static String ID_COLUMN_NAME = "supplier_id";
    public static String DAY_COLUMN_NAME = "day";
    public static String PRODUCT_ID_COLUMN_NAME      = "product_id";
    public static String PRODUCT_QUANTITY_COLUMN_NAME = "product_quantity";

    public ScheduledOrderDataDTO(int supplierID, String day, int productID, int productQuantity) {
        super(SupplierControllerDTO.getInstance());

        this.supplierID = supplierID;
        this.day = day;
        this.productID = productID;
        this.productQuantity = productQuantity;
    }

    public void Insert() {
        SupplierControllerDTO controller = (SupplierControllerDTO) this.dbController;
        controller.insertScheduledOrderData(this);
    }

    public void Delete() {
        SupplierControllerDTO controller = (SupplierControllerDTO) this.dbController;
        controller.deleteScheduledOrderData(this);
    }
}
