package SuppliersModule.DataLayer;

import SuppliersModule.DomainLayer.SupplyContract;

public class SupplyContractDTO extends DTO{
    public int supplyContractID;
    public int supplierID;

    public static String ID_COLUMN_NAME = "id";
    public static String SUPPLIER_ID_COLUMN_NAME = "supplier_id";

    public SupplyContractDTO(int supplyContractID, int supplierID){
        super(SupplyContractControllerDTO.getInstance());

        this.supplyContractID = supplyContractID;
        this.supplierID = supplierID;
    }

    public void Insert() {
        SupplyContractControllerDTO controller = (SupplyContractControllerDTO) this.dbController;
        controller.insertSupplyContract(this);
    }

    public SupplyContract convertDTOToEntity() {
        return new SupplyContract(supplyContractID, supplierID);
    }
}
