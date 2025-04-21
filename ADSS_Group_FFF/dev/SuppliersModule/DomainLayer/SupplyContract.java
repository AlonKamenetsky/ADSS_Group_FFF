package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.SupplyMethod;

import java.util.ArrayList;

public class SupplyContract {
   SupplyMethod supplierSupplyMethod;
   ArrayList<SupplyContractProductData> supplyContractProductsDataArray;

   public SupplyContract(SupplyMethod supplierSupplyMethod) {
      this.supplierSupplyMethod = supplierSupplyMethod;
      this.supplyContractProductsDataArray = new ArrayList<SupplyContractProductData>();
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
}
