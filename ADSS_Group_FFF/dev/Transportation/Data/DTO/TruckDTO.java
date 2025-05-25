package Transportation.Data.DTO;

import Transportation.Domain.Truck;
import Transportation.Domain.TruckType;

public class TruckDTO {
    //Attributes

    private final int truckID;
    private final String licenseNumber;
    private final String model;
    private final TruckType truckType;
    private final float netWeight;
    private final float maxWeight;
    private final boolean isFree;

    //Constructor
    public TruckDTO(int truckID, String licenseNumber, String model, TruckType truckType, float netWeight, float maxWeight, boolean isFree) {
        this.truckID = truckID;
        this.licenseNumber = licenseNumber;
        this.model = model;
        this.truckType = truckType;
        this.netWeight = netWeight;
        this.maxWeight = maxWeight;
        this.isFree = isFree;
    }

    //Convert Truck to TruckDTO
    public static TruckDTO fromEntity(Truck truck) {
        return new TruckDTO(
                truck.getTruckID(),
                truck.getLicenseNumber(),
                truck.getModel(),
                truck.getTruckType(),
                truck.getNetWeight(),
                truck.getMaxWeight(),
                truck.isFree()
        );
    }
    //Getters methods

    public int getTruckID() {
        return truckID;
    }
    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getModel() {
        return model;
    }

    public TruckType getTruckType() {
        return truckType;
    }
    public float getNetWeight() {
        return netWeight;
    }
    public float getMaxWeight() {
        return maxWeight;
    }
    public boolean isFree() {
        return isFree;
    }
}
