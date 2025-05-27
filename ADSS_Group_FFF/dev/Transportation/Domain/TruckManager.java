package Transportation.Domain;

import Transportation.DTO.TruckDTO;
import Transportation.Domain.Repositories.TruckRepository;

import javax.management.InstanceAlreadyExistsException;
import java.sql.SQLException;
import java.util.*;

public class TruckManager {

    private final TruckRepository truckRepository;

    public TruckManager(TruckRepository truckRepository) {
        this.truckRepository = truckRepository;
    };


    public void addTruck(String truckType, String licenseNumber, String model, float netWeight, float maxWeight) throws IllegalArgumentException, NullPointerException, InstanceAlreadyExistsException {
        if (truckType == null || licenseNumber == null || model == null) {
            throw new NullPointerException("Missed Parameters to added Truck");
        }
        try {
            truckRepository.addTruck(truckType,licenseNumber,model,netWeight,maxWeight,true);
        } catch (NoSuchElementException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeTruck(int truckId) throws SQLException{
        try {
            truckRepository.deleteTruck(truckId);
        }
        catch (NoSuchElementException e) {
            e.printStackTrace();
        }
    }



    public List<TruckDTO> getAllTrucks() {
        try {
            return truckRepository.getAllTrucks();
        }   catch (SQLException e) {
            e.printStackTrace();
            // create new list
            return new ArrayList<>();
        }
    }


    public boolean doesTruckExist(int truckId) throws SQLException{
        try {
            return truckRepository.findTruckById(truckId).isPresent();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setTruckAvailability(int truckId, boolean status) throws SQLException {
        try {
            Optional<TruckDTO> truckToChange = truckRepository.findTruckById(truckId);
            if (truckToChange.isPresent()) {
            truckRepository.updateAvailability(truckId, status);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getAllTrucksString() {
        try {
            List<TruckDTO> trucks = truckRepository.getAllTrucks();
            if (trucks.isEmpty()) return "No trucks available.";

            StringBuilder sb = new StringBuilder("All Trucks:\n");
            for (TruckDTO t : trucks) {
                sb.append(t.licenseNumber())
                        .append(" - ").append(t.truckType())
                        .append(" MaxWeight: ").append(t.maxWeight())
                        .append(" Available: ").append(t.isFree())
                        .append("\n----------------------\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error retrieving trucks.";
        }
    }

    public TruckDTO getNextTruckAvailable(float weight) {
        for (TruckDTO t : getAllTrucks()) {
            if (t.isFree() && weight < t.maxWeight()) {
                return t;
            }
        }
        return null;
    }

    public TruckDTO getTruckById(int truckId) throws SQLException {
        return truckRepository.findTruckById(truckId)
                .orElseThrow(() -> new NoSuchElementException("Truck not found"));
    }

    public int getTruckIdByLicense(String licenseNumber) {
        try {
            return truckRepository.findTruckByLicense(licenseNumber).get().truckId();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while retrieving truck by license", e);
        }
    }
}