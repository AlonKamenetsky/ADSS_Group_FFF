package SuppliersModule.DomainLayer;

import SuppliersModule.DataLayer.SupplyContractControllerDTO;
import SuppliersModule.DataLayer.SupplyContractDTO;
import SuppliersModule.DataLayer.SupplyContractProductDataDTO;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.*;

public class SupplyContractController {
    int contractID;

    SupplyContractControllerDTO supplyContractControllerDTO;

    ArrayList<SupplyContract> supplyContractsArrayList;

    public SupplyContractController() {
        this.contractID = 0;

        this.supplyContractsArrayList = new ArrayList<>();

        this.supplyContractControllerDTO = SupplyContractControllerDTO.getInstance();

        for (SupplyContractDTO supplyContractDTO : supplyContractControllerDTO.getAllSupplyContracts()) {
            SupplyContract supplyContract = supplyContractDTO.convertDTOToEntity();
            for (SupplyContractProductDataDTO productDataDTO : supplyContractControllerDTO.getSupplyContractProductDataByContractID(supplyContractDTO))
                supplyContract.addSupplyContractProductData(productDataDTO.convertDTOToEntity());

            this.supplyContractsArrayList.add(supplyContract);
            this.contractID++;
        }
    }

    public SupplyContract registerNewContract(int supplierID, ArrayList<int[]> dataList, SupplyMethod method) {
        ArrayList<SupplyContractProductData> supplyContractProductDataArrayList = new ArrayList<>();
        for (int[] data : dataList) {
            int productID = data[0];
            int price = data[1];
            int quantityForDiscount = data[2];
            int discountPercentage = data[3];

            SupplyContractProductData supplyContractProductData = new SupplyContractProductData(contractID, productID, price, quantityForDiscount, discountPercentage);

            supplyContractProductDataArrayList.add(supplyContractProductData);
            supplyContractProductData.supplyContractProductDataDTO.Insert();
        }

        SupplyContract supplyContract = new SupplyContract(contractID, supplierID, method, supplyContractProductDataArrayList);
        supplyContract.supplyContractDTO.Insert();
        supplyContractsArrayList.add(supplyContract);
        contractID++;

        return supplyContract;
    }

    private SupplyContract getContractByContactID(int contractID) {
        for (SupplyContract supplyContract : supplyContractsArrayList)
            if(supplyContract.contractID == contractID)
                return supplyContract;

        return null;
    }

    public boolean removeContractByID(int contractID) {
        return this.supplyContractsArrayList.removeIf(contract -> contract.contractID == contractID);
    }
    
    public boolean removeAllSupplierContracts(int supplierID) {
        return this.supplyContractsArrayList.removeIf(contract -> contract.supplierID == supplierID);
    }

    // ********** GETTERS FUNCTIONS **********

    public ArrayList<SupplyContract> getAllSupplierContracts(int supplierID) {
        ArrayList<SupplyContract> supplyContractArrayList = new ArrayList<>();
        for (SupplyContract supplyContract : supplyContractsArrayList)
            if(supplyContract.supplierID == supplierID)
                supplyContractArrayList.add(supplyContract);

        return supplyContractArrayList;
    }

    public ArrayList<SupplyContractProductData> getSupplyContractProductDataArrayList(int contractID) {
        SupplyContract supplyContract = getContractByContactID(contractID);
        if(supplyContract != null) {
            return supplyContract.getSupplyContractProductData();
        }
        return null;
    }

    public ArrayList<SupplyContract> getAllAvailableContracts() {
        ArrayList<SupplyContract> copy = new ArrayList<>();
        Collections.copy(copy, this.supplyContractsArrayList);

        return copy;
    }

    // ********** OUTPUT FUNCTIONS **********

    public String getContractToString(int contractID) {
        SupplyContract supplyContract = getContractByContactID(contractID);
        if(supplyContract != null) {
            return supplyContract.toString();
        }
        return null;
    }

    public String[] getAllContractToStrings() {
        String[] contractToStrings = new String[supplyContractsArrayList.size()];
        for(int i = 0; i < supplyContractsArrayList.size(); i++)
            contractToStrings[i] = supplyContractsArrayList.get(i).toString();

        return contractToStrings;
    }
}
