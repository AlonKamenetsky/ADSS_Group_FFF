package SuppliersModule.DataLayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SupplyContractControllerDTO extends DbController {
    private static SupplyContractControllerDTO single_instance = null;

    private final String supplyContractsTableName = "supply_contracts";
    private final String supplyContractsProductDataTableName = "supply_contract_product_data";

    private ArrayList<SupplyContractDTO> supplyContracts;
    private ArrayList<SupplyContractProductDataDTO> supplyContractProductsData;

    public SupplyContractControllerDTO() {
        super();
        this.supplyContracts = new ArrayList<SupplyContractDTO>();
        this.supplyContractProductsData = new ArrayList<SupplyContractProductDataDTO>();
    }

    public static SupplyContractControllerDTO getInstance() {
        if (single_instance == null)
            single_instance = new SupplyContractControllerDTO();

        return single_instance;
    }

    public ArrayList<SupplyContractDTO> getAllSupplyContracts() {
        String sql = "SELECT * FROM " + this.supplyContractsTableName;

        try (Statement stmt = this.connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SupplyContractDTO supplyContract = new SupplyContractDTO(
                        rs.getInt(SupplyContractDTO.ID_COLUMN_NAME),
                        rs.getInt(SupplyContractDTO.SUPPLIER_ID_COLUMN_NAME)
                );
                this.supplyContracts.add(supplyContract);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return this.supplyContracts;
    }

    public void insertSupplyContract(SupplyContractDTO supplyContract) {
        String sql = String.format(
                "INSERT INTO %s (%s, %s) VALUES (?, ?)",
                this.supplyContractsTableName,
                SupplyContractDTO.ID_COLUMN_NAME,
                SupplyContractDTO.SUPPLIER_ID_COLUMN_NAME);


        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplyContract.supplyContractID);
            pstmt.setInt(2, supplyContract.supplierID);

            int result = pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        this.supplyContracts.add(supplyContract);
    }

    public ArrayList<SupplyContractProductDataDTO> getAllSupplyContractProductData() {
        String sql = "SELECT * FROM " + this.supplyContractsProductDataTableName;

        try (Statement stmt = this.connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SupplyContractProductDataDTO supplyContractProductDataDTO = new SupplyContractProductDataDTO(
                        rs.getInt(SupplyContractProductDataDTO.ID_COLUMN_NAME),
                        rs.getInt(SupplyContractProductDataDTO.PRODUCT_ID_COLUMN_NAME),
                        rs.getDouble(SupplyContractProductDataDTO.PRODUCT_PRICE_COLUMN_NAME),
                        rs.getInt(SupplyContractProductDataDTO.QUANTITY_FOR_DISCOUNT_COLUMN_NAME),
                        rs.getDouble(SupplyContractProductDataDTO.DISCOUNT_PERCENTAGE_COLUMN_NAME)
                );
                this.supplyContractProductsData.add(supplyContractProductDataDTO);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return this.supplyContractProductsData;
    }

    public void insertSupplyContractProductData(SupplyContractProductDataDTO supplyContractProductData) {
        String sql = String.format(
                "INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?)",
                this.supplyContractsProductDataTableName,
                SupplyContractProductDataDTO.ID_COLUMN_NAME,
                SupplyContractProductDataDTO.PRODUCT_ID_COLUMN_NAME,
                SupplyContractProductDataDTO.PRODUCT_PRICE_COLUMN_NAME,
                SupplyContractProductDataDTO.QUANTITY_FOR_DISCOUNT_COLUMN_NAME,
                SupplyContractProductDataDTO.DISCOUNT_PERCENTAGE_COLUMN_NAME
                );


        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplyContractProductData.supplyContractID);
            pstmt.setInt(2, supplyContractProductData.productID);
            pstmt.setDouble(3, supplyContractProductData.productPrice);
            pstmt.setInt(4, supplyContractProductData.quantityForDiscount);
            pstmt.setDouble(5, supplyContractProductData.discountPercentage);

            int result = pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        this.supplyContractProductsData.add(supplyContractProductData);
    }

    public ArrayList<SupplyContractProductDataDTO> getSupplyContractProductDataByContractID(SupplyContractDTO supplyContractDTO) {
        String sql = "SELECT * FROM " + this.supplyContractsProductDataTableName + " WHERE " + SupplyContractProductDataDTO.ID_COLUMN_NAME + "= ?" ;

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplyContractDTO.supplyContractID);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SupplyContractProductDataDTO supplyContractProductDataDTO = new SupplyContractProductDataDTO(
                            rs.getInt(SupplyContractProductDataDTO.ID_COLUMN_NAME),
                            rs.getInt(SupplyContractProductDataDTO.PRODUCT_ID_COLUMN_NAME),
                            rs.getDouble(SupplyContractProductDataDTO.PRODUCT_PRICE_COLUMN_NAME),
                            rs.getInt(SupplyContractProductDataDTO.QUANTITY_FOR_DISCOUNT_COLUMN_NAME),
                            rs.getDouble(SupplyContractProductDataDTO.DISCOUNT_PERCENTAGE_COLUMN_NAME));


                    supplyContractProductsData.add(supplyContractProductDataDTO);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return supplyContractProductsData;
    }
}
