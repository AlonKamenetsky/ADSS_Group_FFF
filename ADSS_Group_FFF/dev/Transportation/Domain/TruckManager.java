package Transportation.Domain;

import javax.management.InstanceAlreadyExistsException;
import java.util.*;

public class TruckManager {
    private final HashMap<Integer, Truck> allTrucks;
    private int nextTruckId = 1;

    public TruckManager() {
        allTrucks = new HashMap<>();
    }

    public void addTruck(String truckType, String licenseNumber, String model, float netWeight, float maxWeight) throws IllegalArgumentException, NullPointerException, InstanceAlreadyExistsException {
        if (truckType == null || licenseNumber == null || model == null) {
            throw new NullPointerException("Missed Parameters to added Truck");
        }
        try {
            getTruckIdByLicenseNumber(licenseNumber);
            throw new InstanceAlreadyExistsException();
        } catch (NoSuchElementException e) {
            int truckId = nextTruckId++;
            TruckType type = TruckType.fromString(truckType);
            Truck newTruck = new Truck(truckId, type, licenseNumber, model.toLowerCase(), netWeight, maxWeight);
            allTrucks.putIfAbsent(truckId, newTruck);
        }
    }

    public void removeTruck(String licenseNumber) throws NoSuchElementException {
        Truck truckToRemove = getTruckIdByLicenseNumber(licenseNumber.toLowerCase());
        if (truckToRemove == null) {
            throw new NoSuchElementException();
        }
        int truckId = truckToRemove.getTruckID();
        allTrucks.remove(truckId);
    }

    public Truck getTruckIdByLicenseNumber(String licenseNumber) throws NoSuchElementException {
        for (Truck truck : allTrucks.values()) {
            if (truck.getLicenseNumber().equalsIgnoreCase(licenseNumber)) {
                return truck;
            }
        }
        throw new NoSuchElementException("Truck does not exist");
    }


    public List<Truck> getAllTrucks() {
        return new ArrayList<>(allTrucks.values());
    }

    public boolean doesTruckExist(String licenseNumber) {
        int _truckId = getTruckIdByLicenseNumber(licenseNumber).getTruckID();
        return allTrucks.containsKey(_truckId);
    }

    public void setTruckAvailability(String licenseNumber, boolean status) throws NoSuchElementException {
        Truck truckToChange = getTruckIdByLicenseNumber(licenseNumber);
        if (truckToChange == null) {
            throw new NoSuchElementException();
        } else {
            truckToChange.setAvailability(status);
            return;
        }
    }

    public String getAllTrucksString() {
        List<Truck> allTrucks = getAllTrucks();
        if (allTrucks.isEmpty()) return "No trucks available.";

        StringBuilder sb = new StringBuilder("All Trucks:\n");
        for (Truck t : allTrucks) {
            sb.append(t).append("\n----------------------\n");
        }
        return sb.toString();
    }

    public Truck getNextTruckAvailable(float weight) {
        for (Truck t : allTrucks.values()) {
            if (t.isFree() && weight < t.getMaxWeight()) {
                return t;
            }
        }
        return null;
    }
}