package SuppliersModule.DataLayer;

import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.DomainLayer.Product;

public class ProductDTO extends DTO {
    public int productId;
    public String productName;
    public String productCompanyName;
    public String productCategory;

    public static String ID_COLUMN_NAME = "id";
    public static String NAME_COLUMN_NAME = "name";
    public static String COMPANY_NAME_COLUMN_NAME = "company_name";
    public static String PRODUCT_CATEGORY_COLUMN_NAME = "product_category";

    public ProductDTO(int productId, String productName, String productCompanyName, String productCategory) {
        super(ProductControllerDTO.getInstance());
        this.productId = productId;
        this.productName = productName;
        this.productCompanyName = productCompanyName;
        this.productCategory = productCategory;
    }

    public void Insert() {
        ProductControllerDTO controller = (ProductControllerDTO) this.dbController;
        controller.insertProduct(this);
    }

    public void Delete() {
        ProductControllerDTO controller = (ProductControllerDTO) this.dbController;
        controller.deleteProduct(this);
    }

    public Product convertDTOToEntity() {
        return new Product(this);
    }
}
