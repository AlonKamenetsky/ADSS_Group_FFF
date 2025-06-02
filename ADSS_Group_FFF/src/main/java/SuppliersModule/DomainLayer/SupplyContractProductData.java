package SuppliersModule.DomainLayer;

import SuppliersModule.DataLayer.SupplyContractProductDataDTO;

public class SupplyContractProductData {
    int contractID;
    int productID;
    double productPrice;
    int quantityForDiscount;
    double discountPercentage;

    SupplyContractProductDataDTO supplyContractProductDataDTO;

    public SupplyContractProductData(int contractID, int productID, double productPrice, int quantityForDiscount, double discountPercentage) {
        this.contractID = contractID;
        this.productID = productID;
        this.productPrice = productPrice;
        this.quantityForDiscount = quantityForDiscount;
        this.discountPercentage = discountPercentage;

        supplyContractProductDataDTO = new SupplyContractProductDataDTO(contractID, productID, productPrice, quantityForDiscount, discountPercentage);
    }

    public SupplyContractProductData(SupplyContractProductDataDTO supplyContractProductDataDTO) {
        this.contractID = supplyContractProductDataDTO.supplyContractID;
        this.productID = supplyContractProductDataDTO.productID;
        this.productPrice = supplyContractProductDataDTO.productPrice;
        this.quantityForDiscount = supplyContractProductDataDTO.quantityForDiscount;
        this.discountPercentage = supplyContractProductDataDTO.discountPercentage;

        this.supplyContractProductDataDTO = supplyContractProductDataDTO;
    }

    public int getContractID() {
        return contractID;
    }
    public void setContractID(int contractID) {
        this.contractID = contractID;
    }
    public int getProductID() {
        return productID;
    }
    public void setProductID(int productID) {
        this.productID = productID;
    }
    public double getProductPrice() {
        return productPrice;
    }
    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }
    public int getQuantityForDiscount() {
        return quantityForDiscount;
    }
    public void setQuantityForDiscount(int quantityForDiscount) {
        this.quantityForDiscount = quantityForDiscount;
    }
    public double getDiscountPercentage() {
        return discountPercentage;
    }
    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String toString() {
        return String.format("ContractID: %d productID: %d Price: %.2f Quantity For Discount: %d Discount Percentage: %.2f", contractID,productID, productPrice, quantityForDiscount, discountPercentage);
    }
}
