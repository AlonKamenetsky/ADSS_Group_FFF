package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.SupplyMethod;

import java.util.ArrayList;

public class SupplyContract {
   int contractId;
   int supplierId;
   ArrayList<SupplyContractProductData> supplyContractProductsDataArray;

   public SupplyContract(int supplierId, int contractID) {
      this.supplierId = supplierId;
      this.contractId = contractID;
      this.supplyContractProductsDataArray = new ArrayList<>();
   }

   public SupplyContract(int contractId, int supplierId, SupplyMethod supplierSupplyMethod, ArrayList<SupplyContractProductData> supplyContractProductsDataArray) {
      this.contractId = contractId;
      this.supplierId = supplierId;
      this.supplyContractProductsDataArray = supplyContractProductsDataArray;
   }

   public void addSupplyContractProductData(SupplyContractProductData data) {
      this.supplyContractProductsDataArray.add(data);
   }

   public int getContractId() {
      return contractId;
   }
   public void setContractId(int contractId) {
      this.contractId = contractId;
   }

   public ArrayList<SupplyContractProductData> getSupplyContractProductData() {
      return this.supplyContractProductsDataArray;
   }

   public SupplyContractProductData getSupplyContractProductDataOfProduct(int productID) {
      for (SupplyContractProductData productData : this.supplyContractProductsDataArray)
         if (productData.getProductID() == productID)
            return productData;

      return null;
   }

   public boolean CheckIfProductInData(int productID) {
      for (SupplyContractProductData productData : this.supplyContractProductsDataArray)
         if (productData.getProductID() == productID)
            return true;

      return false;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("SupplyContract {\n");
      sb.append("  Contract ID: ").append(contractId).append(",\n");
      sb.append("  Product Data List:\n");
      for (SupplyContractProductData data : supplyContractProductsDataArray) {
         sb.append("    ").append(data).append("\n");
      }
      sb.append("}");
      return sb.toString();
   }
}
