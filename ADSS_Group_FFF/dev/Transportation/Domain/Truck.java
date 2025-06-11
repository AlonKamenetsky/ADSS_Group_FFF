package Transportation.Domain;

public class Truck {
    private final int truckID;
    private final TruckType truckType;
    private final String licenseNumber;
    private final String model;
    private final float netWeight;
    private final float maxWeight;
    private boolean isFree;

    public Truck(int _truckID, TruckType _truckType, String _licenseNumber, String _model, float _netWeight, float _maxWeight) {
        truckID = _truckID;
        truckType = _truckType;
        licenseNumber = _licenseNumber;
        model = _model;
        netWeight = _netWeight;
        maxWeight = _maxWeight;
        isFree = true;
    }

    public int getTruckID() {
        return truckID;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setAvailability(boolean status) {
        isFree = status;
    }

    public TruckType getTruckType() {
        return truckType;
    }

    public float getMaxWeight() { return maxWeight; }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getModel() {
        return model;
    }

    public float getNetWeight() {
        return netWeight;
    }

    public String toString() {
        return String.format(
                "Truck Type: %s\nLicense Number: %s\nModel: %s\nNet Weight: %.2f\nMax Weight: %.2f\nAvailable: %s",
                truckType, licenseNumber, model.toUpperCase(), netWeight, maxWeight, isFree ? "Yes" : "No"
        );
    }
}