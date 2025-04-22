package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.SupplyMethod;

import java.util.ArrayList;

public class SupplyContract {
   SupplyMethod supplierSupplyMethod;
   ArrayList<SupplyContractProductData> supplyContractProductsDataArray;
   int contractId;

   public SupplyContract(SupplyMethod supplierSupplyMethod ,int contractId) {
      this.supplierSupplyMethod = supplierSupplyMethod;
      this.supplyContractProductsDataArray = new ArrayList<SupplyContractProductData>();
      this.contractId = 0;
   }
   public SupplyContract(SupplyMethod supplierSupplyMethod, ArrayList<SupplyContractProductData> supplyContractProductsDataArray, int contractId ) {
      this.supplierSupplyMethod = supplierSupplyMethod;
      this.supplyContractProductsDataArray = supplyContractProductsDataArray;
      this.contractId = contractId;
   }

   public void AddSupplyContractProductData(SupplyContractProductData data) {
      this.supplyContractProductsDataArray.add(data);
   }

   public SupplyMethod getSupplierSupplyMethod(){
      return this.supplierSupplyMethod;
   }
   public void setSupplierSupplyMethod(SupplyMethod supplyMethod){
      this.supplierSupplyMethod = supplyMethod;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("SupplyContract {\n");
      sb.append("  Supplier Supply Method: ").append(supplierSupplyMethod).append(",\n");
      sb.append("  Product Data List:\n");
      for (SupplyContractProductData data : supplyContractProductsDataArray) {
         sb.append("    ").append(data).append("\n");
      }
      sb.append("}");
      return sb.toString();
   }
}
