package SuppliersModule.DomainLayer;

import SuppliersModule.DataLayer.ProductDTO;
import SuppliersModule.DomainLayer.Enums.ProductCategory;

public class Product {
    int productId;
    String productName;
    String productCompanyName;
    ProductCategory productCategory;

    ProductDTO productDTO;

    public Product(int productId, String productName, String productCompanyName, ProductCategory productCategory) {
        this.productId = productId;
        this.productName = productName;
        this.productCompanyName = productCompanyName;
        this.productCategory = productCategory;

        this.productDTO = new ProductDTO(productId, productName, productCompanyName, productCategory.toString());
    }

    public Product(ProductDTO dto) {
        this.productId = dto.productId;
        this.productName = dto.productName;
        this.productCompanyName = dto.productCompanyName;
        this.productCategory = ProductCategory.valueOf(dto.productCategory);
        this.productDTO = dto;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
        this.productDTO.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
        this.productDTO.productName = productName;
    }

    public String getProductCompanyName() {
        return productCompanyName;
    }

    public void setProductCompanyName(String productCompanyName) {
        this.productCompanyName = productCompanyName;
        this.productDTO.productCompanyName = productCompanyName;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
        this.productDTO.productCategory = productCategory.toString();
    }

    public String toString() {
        return this.productId + "\t" + this.productName + "\t" + this.productCompanyName + "\t" + this.productCategory;
    }
}
