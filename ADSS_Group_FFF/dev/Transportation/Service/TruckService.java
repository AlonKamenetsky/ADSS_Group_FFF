package Transportation.Service;

import Transportation.DTO.TruckDTO;
import Transportation.Domain.TruckManager;
import javax.management.InstanceAlreadyExistsException;
import java.sql.SQLException;
import java.util.List;
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

    public void deleteTruck(String licenseNumber) throws SQLException {
        if (licenseNumber == null) {
            throw new NullPointerException();
        }
        int truckId = truckManager.getTruckIdByLicense(licenseNumber);
        truckManager.removeTruck(truckId);
    }

    public String viewAllTrucks() {
        return truckManager.getAllTrucksString();
    }

    public String getTruckByLicenseNumber(String licenseNumber) {
        if (licenseNumber == null) {
            throw new NullPointerException("License number is null");
        }

        try {
            int truckId = truckManager.getTruckIdByLicense(licenseNumber);
            List<TruckDTO> allTrucks = truckManager.getAllTrucks();

            for (TruckDTO t : allTrucks) {
                if (t.truckId() == truckId) {
                    return "Truck ID: " + t.truckId() + "\n" +
                            "License: " + t.licenseNumber() + "\n" +
                            "Type: " + t.truckType() + "\n" +
                            "Model: " + t.model() + "\n" +
                            "Net Weight: " + t.netWeight() + "\n" +
                            "Max Weight: " + t.maxWeight() + "\n" +
                            "Available: " + (t.isFree() ? "Yes" : "No");
                }
            }
            return "Truck not found.";
        } catch (NoSuchElementException e) {
            return "Truck with license '" + licenseNumber + "' not found.";
        } catch (Exception e) {
            e.printStackTrace();
            return "An unexpected error occurred while retrieving the truck.";
        }
    }


    public void ChangeTruckAvailability(String licenseNumber, boolean status) throws  SQLException {
        int truckId = truckManager.getTruckIdByLicense(licenseNumber);
        truckManager.setTruckAvailability(truckId, status);
    }
}