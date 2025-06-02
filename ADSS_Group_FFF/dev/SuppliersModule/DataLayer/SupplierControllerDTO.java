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
    private final String scheduledOrderProductDataTableName = "scheduled_order_data";


    private ArrayList<SupplierDTO> suppliers;
    private ArrayList<SupplierDaysDTO> suppliersDays;
    private ArrayList<ScheduledOrderDataDTO> scheduledOrderDataList;

    public SupplierControllerDTO() {
        super();
        this.suppliers = new ArrayList<>();
        this.suppliersDays = new ArrayList<>();
        this.scheduledOrderDataList = new ArrayList<>();
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
        String sql = "SELECT * FROM " + this.suppliersDaysTableName + " WHERE " + SupplierDaysDTO.ID_COLUMN_NAME + "= ?" ;

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplier.supplierID);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SupplierDaysDTO supplierDaysDTO = new SupplierDaysDTO(
                            rs.getInt(SupplierDaysDTO.ID_COLUMN_NAME),
                            rs.getString(SupplierDaysDTO.DAY_COLUMN_NAME));

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
                "INSERT INTO %s (%s, %s) VALUES (?, ?)",
                this.suppliersDaysTableName,
                SupplierDaysDTO.ID_COLUMN_NAME,
                SupplierDaysDTO.DAY_COLUMN_NAME
        );

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplierDays.supplierID);
            pstmt.setString(2, supplierDays.day);

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

    public ArrayList<ScheduledOrderDataDTO> getAllScheduledOrderData(SupplierDaysDTO supplierDaysDTO) {
        ArrayList<ScheduledOrderDataDTO> result = new ArrayList<>();

        String sql = String.format("SELECT * FROM %s", this.scheduledOrderProductDataTableName);

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplierDaysDTO.supplierID);
            pstmt.setString(1, supplierDaysDTO.day);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ScheduledOrderDataDTO data = new ScheduledOrderDataDTO(
                            rs.getInt(ScheduledOrderDataDTO.ID_COLUMN_NAME),
                            rs.getString(ScheduledOrderDataDTO.DAY_COLUMN_NAME),
                            rs.getInt(ScheduledOrderDataDTO.PRODUCT_ID_COLUMN_NAME),
                            rs.getInt(ScheduledOrderDataDTO.PRODUCT_QUANTITY_COLUMN_NAME));

                    result.add(data);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    public ArrayList<ScheduledOrderDataDTO> getAllScheduledOrderDataOfSupplier(SupplierDaysDTO supplierDaysDTO) {
        ArrayList<ScheduledOrderDataDTO> result = new ArrayList<>();

        String sql = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?", this.scheduledOrderProductDataTableName,
                ScheduledOrderDataDTO.ID_COLUMN_NAME, ScheduledOrderDataDTO.DAY_COLUMN_NAME);

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplierDaysDTO.supplierID);
            pstmt.setString(2, supplierDaysDTO.day);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ScheduledOrderDataDTO data = new ScheduledOrderDataDTO(
                            rs.getInt(ScheduledOrderDataDTO.ID_COLUMN_NAME),
                            rs.getString(ScheduledOrderDataDTO.DAY_COLUMN_NAME),
                            rs.getInt(ScheduledOrderDataDTO.PRODUCT_ID_COLUMN_NAME),
                            rs.getInt(ScheduledOrderDataDTO.PRODUCT_QUANTITY_COLUMN_NAME));

                    result.add(data);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    public void insertScheduledOrderData(ScheduledOrderDataDTO dto) {
        String sql = String.format(
                "INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
                this.scheduledOrderProductDataTableName,
                ScheduledOrderDataDTO.ID_COLUMN_NAME,
                ScheduledOrderDataDTO.DAY_COLUMN_NAME,
                ScheduledOrderDataDTO.PRODUCT_ID_COLUMN_NAME,
                ScheduledOrderDataDTO.PRODUCT_QUANTITY_COLUMN_NAME
        );

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt   (1, dto.supplierID);
            ps.setString(2, dto.day);
            ps.setInt   (3, dto.productID);
            ps.setInt   (4, dto.productQuantity);

            int affected = ps.executeUpdate();
            if (affected != 1)
                throw new SQLException("Failed inserting scheduled-order data");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        this.scheduledOrderDataList.add(dto);
    }

    public int deleteScheduledOrderData(ScheduledOrderDataDTO dto) {
        String sql = String.format(
                "DELETE FROM %s WHERE %s = ? AND %s = ? AND %s = ?",
                this.scheduledOrderProductDataTableName,
                ScheduledOrderDataDTO.ID_COLUMN_NAME,
                ScheduledOrderDataDTO.DAY_COLUMN_NAME,
                ScheduledOrderDataDTO.PRODUCT_ID_COLUMN_NAME
        );

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt   (1, dto.supplierID);
            ps.setString(2, dto.day);
            ps.setInt   (3, dto.productID);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }
}
