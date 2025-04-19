package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.SupplyMethod;

import java.util.ArrayList;

public class SupplyContract {
   int contractId;
   SupplyMethod supplierSupplyMethod;
   ArrayList<SupplyContractProductData> supplyContractProductsDataArray;
}
