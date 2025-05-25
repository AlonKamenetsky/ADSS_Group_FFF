package Transportation.Service;

import Transportation.Domain.TruckManager;

import javax.management.InstanceAlreadyExistsException;
import java.util.NoSuchElementException;

public class TruckService {
    private final TruckManager truckManager;

    public TruckService(TruckManager truckManager) {
        this.truckManager = truckManager;
    }

    public void AddTruck(String truckType, String licenseNumber, String model, float netWeight, float maxWeight) throws IllegalArgumentException, NullPointerException, InstanceAlreadyExistsException {
        if (truckType == null || licenseNumber == null || model == null || netWeight < 0 || maxWeight < 0) {
            throw new NullPointerException();
        }
        truckManager.addTruck(truckType, licenseNumber, model.toLowerCase(), netWeight, maxWeight);
    }

    public void deleteTruck(String licenseNumber) throws NullPointerException, NoSuchElementException {
        if (licenseNumber == null) {
            throw new NullPointerException();
        }
        if (truckManager.doesTruckExist(licenseNumber)) {
            truckManager.removeTruck(licenseNumber);
        }
        else {
            throw new NoSuchElementException();
        }
    }

    public String viewAllTrucks() {
        return truckManager.getAllTrucksString();
    }

    public String getTruckByLicenseNumber(String licenseNumber) throws NullPointerException, NoSuchElementException {
        if (licenseNumber == null) {
            throw new NullPointerException();
        }
        if (truckManager.doesTruckExist(licenseNumber)) {
            return truckManager.getTruckIdByLicenseNumber(licenseNumber).toString();
        }
        return "Truck doesn't exist";
    }

    public void ChangeTruckAvailability(String licenseNumber, boolean status) throws NoSuchElementException {
        if(licenseNumber == null) {
            throw new NullPointerException();
        }
        truckManager.setTruckAvailability(licenseNumber, status);
    }
}