package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.SupplyMethod;

import java.util.ArrayList;

public class SupplyContractController {
    int contractId;
    ArrayList<SupplyContract> supplyContracts;
    public SupplyContractController() {
        contractId = 0;
        supplyContracts = new ArrayList<>();
    }
    public SupplyContract registerNewContract(int supplierID, ArrayList<int[]> dataList, SupplyMethod method) {
        ArrayList<SupplyContractProductData> supplyContractProductDataArrayList = new ArrayList<>();
        for (int[] data : dataList) {
            int productID = data[0];
            int price = data[1];
            int quantityForDiscount = data[2];
            int discountPercentage = data[3];
            SupplyContractProductData supplyContractProductData = new SupplyContractProductData(productID, price, quantityForDiscount, discountPercentage);
            supplyContractProductDataArrayList.add(supplyContractProductData);
        }

        SupplyContract supplyContract = new SupplyContract(method, supplyContractProductDataArrayList, contractId);
        contractId++;
        return supplyContract;
    }
    public SupplyContract getContract(int contractID) {
        for (SupplyContract supplyContract : supplyContracts) {
            if(supplyContract.contractId == contractID) {
                return supplyContract;
            }
        }
        return null;
    }

    public String getContractToString(int contractID) {
        for (SupplyContract supplyContract : supplyContracts) {
            if(supplyContract.contractId == contractID) {
                return supplyContract.toString();
            }
        }
        return null;
    }
    public ArrayList<SupplyContractProductData> getSupplyContractProductDataArrayList(int contractID) {
        for (SupplyContract supplyContract : supplyContracts) {
            if(supplyContract.contractId == contractID) {
                return supplyContract.getSupplyContractProductData();
            }
        }
        return null;
    }
}
