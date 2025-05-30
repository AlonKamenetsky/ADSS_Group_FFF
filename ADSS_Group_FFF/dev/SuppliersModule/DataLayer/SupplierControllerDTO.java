package SuppliersModule.DataLayer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class SupplierControllerDTO extends DbController {
    private static SupplierControllerDTO single_instance = null;

    private String suppliersTableName = "suppliers";
    private String suppliersDaysTableName = "suppliers_days";

    private ArrayList<SupplierDTO> suppliers;
    private ArrayList<SupplierDaysDTO> suppliersDays;

    public SupplierControllerDTO() {
        super();
        this.suppliers = new ArrayList<>();
        this.suppliersDays = new ArrayList<>();
    }

    public static SupplierControllerDTO getInstance() {
        if (single_instance == null)
            single_instance = new SupplierControllerDTO();

        return single_instance;
    }

    public ArrayList<SupplierDTO> getAllSuppliers()  {
        String sql = "SELECT * FROM " + this.suppliersTableName;

        try (Statement stmt = this.connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SupplierDTO supplier = new SupplierDTO(
                        rs.getInt(SupplierDTO.ID_COLUMN_NAME),
                        rs.getString(SupplierDTO.NAME_COLUMN_NAME),
                        rs.getString(SupplierDTO.PRODUCT_CATEGORY_COLUMN_NAME),
                        rs.getString(SupplierDTO.DELIVERY_METHOD_COLUMN_NAME),
                        rs.getString(SupplierDTO.CONTACT_NAME_COLUMN_NAME),
                        rs.getString(SupplierDTO.PHONE_NUMBER_COLUMN_NAME),
                        rs.getString(SupplierDTO.ADDRESS_COLUMN_NAME),
                        rs.getString(SupplierDTO.EMAIL_ADDRESS_COLUMN_NAME),
                        rs.getString(SupplierDTO.BANK_ACCOUNT_COLUMN_NAME),
                        rs.getString(SupplierDTO.PAYMENT_METHOD_COLUMN_NAME),
                        rs.getString(SupplierDTO.SUPPLY_METHOD_COLUMN_NAME)
                );
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return suppliers;
    }

    public void insertSupplier(SupplierDTO supplier)  {
        String sql = String.format(
                "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                suppliersTableName,
                supplier.ID_COLUMN_NAME,
                supplier.NAME_COLUMN_NAME,
                supplier.PRODUCT_CATEGORY_COLUMN_NAME,
                supplier.DELIVERY_METHOD_COLUMN_NAME,
                supplier.CONTACT_NAME_COLUMN_NAME,
                supplier.PHONE_NUMBER_COLUMN_NAME,
                supplier.ADDRESS_COLUMN_NAME,
                supplier.EMAIL_ADDRESS_COLUMN_NAME,
                supplier.BANK_ACCOUNT_COLUMN_NAME,
                supplier.PAYMENT_METHOD_COLUMN_NAME,
                supplier.SUPPLY_METHOD_COLUMN_NAME
        );

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplier.supplierID);
            pstmt.setString(2, supplier.supplierName);
            pstmt.setString(3, supplier.productCategory);
            pstmt.setString(4, supplier.deliveryMethod);
            pstmt.setString(5, supplier.contactName);
            pstmt.setString(6, supplier.phoneNumber);
            pstmt.setString(7, supplier.address);
            pstmt.setString(8, supplier.emailAddress);
            pstmt.setString(9, supplier.bankAccount);
            pstmt.setString(10, supplier.paymentMethod);
            pstmt.setString(11, supplier.supplyMethod);

            int result = pstmt.executeUpdate();
        }

        catch (SQLException e){
            System.out.println(e.getMessage());
        }

        this.suppliers.add(supplier);
    }

    public void deleteSupplier(SupplierDTO supplier){
        String sql = "DELETE FROM " + this.suppliersTableName + " WHERE id = ?";

        try (PreparedStatement pstmt =  this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplier.supplierID);
            int result = pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        this.suppliers.removeIf(s -> s.supplierID == supplier.supplierID);
    }

    public ArrayList<SupplierDaysDTO> getSupplierDaysOfSupplier(SupplierDTO supplier) {
        String sql = "SELECT * FROM " + this.suppliersDaysTableName + " WHERE " + supplier.ID_COLUMN_NAME + "= ?" ;

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplier.supplierID);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SupplierDaysDTO supplierDaysDTO = new SupplierDaysDTO(
                            rs.getInt(SupplierDaysDTO.ID_COLUMN_NAME),
                            rs.getString(SupplierDaysDTO.DAY_COLUMN_NAME),
                            rs.getInt(SupplierDaysDTO.PRODUCT_ID_COLUMN_NAME),
                            rs.getInt(SupplierDaysDTO.PRODUCT_QUANTITY_COLUMN_NAME),
                            rs.getDouble(SupplierDaysDTO.PRODUCT_PRICE_COLUMN_NAME));

                    suppliersDays.add(supplierDaysDTO);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return suppliersDays;
    }

    public void insertSupplierDays(SupplierDaysDTO supplierDays) {
        String sql = String.format(
                "INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
                this.suppliersDaysTableName,
                SupplierDaysDTO.ID_COLUMN_NAME,
                SupplierDaysDTO.DAY_COLUMN_NAME
        );

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplierDays.supplierID);
            pstmt.setString(2, supplierDays.day);
            pstmt.setInt(3, supplierDays.productID);
            pstmt.setInt(3, supplierDays.productQuantity);
            pstmt.setDouble(4, supplierDays.productPrice);

            int result = pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteSupplierDays(SupplierDaysDTO supplier) {
        String sql = "DELETE FROM " + this.suppliersDaysTableName + " WHERE id = ?";

        try (PreparedStatement pstmt =  this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplier.supplierID);
            int result = pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        this.suppliers.removeIf(s -> s.supplierID == supplier.supplierID);
    }
}
