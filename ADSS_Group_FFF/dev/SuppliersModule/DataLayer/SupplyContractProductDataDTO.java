package SuppliersModule.DataLayer;

import SuppliersModule.DomainLayer.SupplyContractProductData;

public class SupplyContractProductDataDTO extends DTO{
    public int supplyContractID;
    public int productID;
    public double productPrice;
    public int quantityForDiscount;
    public double discountPercentage;

    public static String ID_COLUMN_NAME = "contract_id";
    public static String PRODUCT_ID_COLUMN_NAME = "product_id";
    public static String PRODUCT_PRICE_COLUMN_NAME = "product_price";
    public static String QUANTITY_FOR_DISCOUNT_COLUMN_NAME = "quantity_for_discount";
    public static String DISCOUNT_PERCENTAGE_COLUMN_NAME = "discount_percentage";

    public SupplyContractProductDataDTO(int supplyContractID, int productID, double productPrice, int quantityForDiscount, double discountAmount ){
        super(SupplyContractControllerDTO.getInstance());

        this.supplyContractID = supplyContractID;
        this.productID = productID;
        this.productPrice = productPrice;
        this.quantityForDiscount = quantityForDiscount;
        this.discountPercentage = discountAmount;
    }

    public void Insert() {
        SupplyContractControllerDTO controller = (SupplyContractControllerDTO) this.dbController;
        controller.insertSupplyContractProductData(this);
    }

    public SupplyContractProductData convertDTOToEntity() {
        return new SupplyContractProductData(supplyContractID, productID, productPrice, quantityForDiscount, discountPercentage);
    }
}
