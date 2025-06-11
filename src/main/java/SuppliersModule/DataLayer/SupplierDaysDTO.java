package SuppliersModule.DataLayer;

import java.io.IOException;
import java.sql.SQLException;

public class SupplierDaysDTO extends DTO {
    public int supplierID;
    public String day;

    public static String ID_COLUMN_NAME = "id";
    public static String DAY_COLUMN_NAME = "day";

    public SupplierDaysDTO(int supplierID, String day)  {
        super(SupplierControllerDTO.getInstance());

        this.supplierID = supplierID;
        this.day = day;
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
