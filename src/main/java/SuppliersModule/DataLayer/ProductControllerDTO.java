package SuppliersModule.DataLayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ProductControllerDTO extends DbController {
    private static ProductControllerDTO single_instance = null;

    private String productsTableName = "products";

    ArrayList<ProductDTO> products;

    private ProductControllerDTO() {
        super();
        this.products = new ArrayList<>();
    }

    public static ProductControllerDTO getInstance() {
        if (single_instance == null)
            single_instance = new ProductControllerDTO();

        return single_instance;
    }

    public ArrayList<ProductDTO> getAllProducts() {
        String sql = "SELECT * FROM " + this.productsTableName;

        try (Statement stmt = this.connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ProductDTO product = new ProductDTO(
                        rs.getInt(ProductDTO.ID_COLUMN_NAME),
                        rs.getString(ProductDTO.NAME_COLUMN_NAME),
                        rs.getString(ProductDTO.COMPANY_NAME_COLUMN_NAME),
                        rs.getString(ProductDTO.PRODUCT_CATEGORY_COLUMN_NAME)
                );
                this.products.add(product);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return this.products;
    }

    public void insertProduct(ProductDTO product) {
        String sql = String.format(
                "INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
                this.productsTableName,
                ProductDTO.ID_COLUMN_NAME,
                ProductDTO.NAME_COLUMN_NAME,
                ProductDTO.COMPANY_NAME_COLUMN_NAME,
                ProductDTO.PRODUCT_CATEGORY_COLUMN_NAME);

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, product.productId);
            pstmt.setString(2, product.productName);
            pstmt.setString(3,  product.productCompanyName);
            pstmt.setString(4, product.productCategory);

            int result = pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        products.add(product);
    }

    public void deleteProduct(ProductDTO product) {
        String sql = "DELETE FROM " + this.productsTableName + " WHERE id = ?";

        try (PreparedStatement pstmt =  this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, product.productId);
            int result = pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        this.products.removeIf(p -> p.productId == product.productId);
    }
}
