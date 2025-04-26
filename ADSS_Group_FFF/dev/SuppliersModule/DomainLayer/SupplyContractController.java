package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.SupplyMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

public class SupplyContractController {
    int contractId;
    ArrayList<SupplyContract> supplyContracts;

    public SupplyContractController() {
        this.contractId = 0;
        this.supplyContracts = new ArrayList<>();

       //this.ReadSupplierContractDataFromCSV();
    }

    public void ReadSupplierContractDataFromCSV() {
        Map<Integer, List<SupplyContract>> supplierToContracts = new HashMap<>();
        Map<String, SupplyContract> uniqueContractLookup = new HashMap<>();

        InputStream in = SupplyContractController.class.getResourceAsStream("/contracts_data.csv");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;  // skip header
                    continue;
                }

                String[] parts = line.split(",");
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim();
                    if (parts[i].startsWith("\"") && parts[i].endsWith("\"")) {
                        parts[i] = parts[i].substring(1, parts[i].length() - 1);
                    }
                }

                int supplierId = Integer.parseInt(parts[0]);
                int productId = Integer.parseInt(parts[1]);
                double productPrice = Double.parseDouble(parts[2]);
                int quantityForDiscount = Integer.parseInt(parts[3]);
                double discountPercentage = Double.parseDouble(parts[4]);
                int contractId = Integer.parseInt(parts[5]);

                String uniqueKey = supplierId + "_" + contractId;

                SupplyContract contract = uniqueContractLookup.get(uniqueKey);
                if (contract == null) {
                    contract = new SupplyContract(supplierId, contractId);
                    uniqueContractLookup.put(uniqueKey, contract);
                    supplierToContracts
                            .computeIfAbsent(supplierId, k -> new ArrayList<>())
                            .add(contract);
                }

                // Now all products with the same supplierId+contractId
                // end up in the same SupplyContract
                contract.supplyContractProductsDataArray.add(
                        new SupplyContractProductData(
                                productId,
                                productPrice,
                                quantityForDiscount,
                                discountPercentage
                        )
                );
            }

        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }

        for (List<SupplyContract> list : supplierToContracts.values()) {
            this.supplyContracts.addAll(list);
        }
    }

    private SupplyContract getContractByContactID(int contractID) {
        for (SupplyContract supplyContract : supplyContracts)
            if(supplyContract.contractId == contractID)
                return supplyContract;

        return null;
    }

    public ArrayList<SupplyContract> getAllSupplierContracts(int supplierID) {
        ArrayList<SupplyContract> supplyContractArrayList = new ArrayList<>();
        for (SupplyContract supplyContract : supplyContracts)
            if(supplyContract.supplierId == supplierID)
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

    public SupplyContract registerNewContract(int supplierID, ArrayList<int[]> dataList, SupplyMethod method) {
        ArrayList<SupplyContractProductData> supplyContractProductDataArrayList = new ArrayList<>();
        for (int[] data : dataList) {
            int productID = data[1];
            int price = data[2];
            int quantityForDiscount = data[3];
            int discountPercentage = data[4];
            SupplyContractProductData supplyContractProductData = new SupplyContractProductData(productID, price, quantityForDiscount, discountPercentage);
            supplyContractProductDataArrayList.add(supplyContractProductData);
        }

        SupplyContract supplyContract = new SupplyContract(contractId, supplierID, method, supplyContractProductDataArrayList);
        contractId++;
        return supplyContract;
    }

    public String getContractToString(int contractID) {
        SupplyContract supplyContract = getContractByContactID(contractID);
        if(supplyContract != null) {
            return supplyContract.toString();
        }
        return null;
    }
    public String[] getAllContractToStrings() {
        String[] contractToStrings = new String[supplyContracts.size()];
        for(int i = 0; i < supplyContracts.size(); i++){
            contractToStrings[i] = supplyContracts.get(i).toString();
        }
        return contractToStrings;
    }
}
