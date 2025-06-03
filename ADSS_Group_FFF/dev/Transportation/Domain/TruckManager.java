package Transportation.Domain;

import Transportation.DTO.TruckDTO;
import Transportation.Domain.Repositories.TruckRepository;
import Transportation.Domain.Repositories.TruckRepositoryImpli;

import javax.management.InstanceAlreadyExistsException;
import java.sql.SQLException;
import java.util.*;

public class TruckManager {

    private final TruckRepository truckRepository;

    public TruckManager() {
        this.truckRepository = new TruckRepositoryImpli();
    }

    //for mock tests
    public TruckManager(TruckRepository truckRepository1) {
        this.truckRepository = truckRepository1;
    }

    public void addTruck(String truckType, String licenseNumber, String model, float netWeight, float maxWeight) throws SQLException, InstanceAlreadyExistsException {
        if (truckRepository.findTruckByLicense(licenseNumber).isEmpty()) {
            truckRepository.addTruck(truckType, licenseNumber, model, netWeight, maxWeight, true);
        } else {
            throw new InstanceAlreadyExistsException();
        }
    }

    public void removeTruck(int truckId) throws SQLException, NoSuchElementException {
        if (getTruckById(truckId).isEmpty()) {
            throw new NoSuchElementException();
        } else {
            truckRepository.deleteTruck(truckId);
        }
    }


    public List<TruckDTO> getAllTrucks() throws SQLException {
        return truckRepository.getAllTrucks();
    }

    public void setTruckAvailability(int truckId, boolean status) throws SQLException, NoSuchElementException {
        Optional<TruckDTO> truckToChange = truckRepository.findTruckById(truckId);
        if (truckToChange.isPresent()) {
            truckRepository.updateAvailability(truckId, status);
        } else {
            throw new NoSuchElementException();
        }
    }

    public String getAllTrucksString() throws SQLException {
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
    }

    public Optional<TruckDTO> getNextTruckAvailable(float weight) throws SQLException {
        for (TruckDTO t : getAllTrucks()) {
            if (t.isFree() && weight < t.maxWeight()) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    public Optional<TruckDTO> getTruckById(int truckId) throws SQLException {
        return truckRepository.findTruckById(truckId);
    }

    public int getTruckIdByLicense(String licenseNumber) throws SQLException, NoSuchElementException {
        Optional<TruckDTO> truck = truckRepository.findTruckByLicense(licenseNumber);
        if (truck.isEmpty()) {
            throw new NoSuchElementException();
        } else {
            return truck.get().truckId();
        }
    }
}