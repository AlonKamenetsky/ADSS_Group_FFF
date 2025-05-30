package Transportation.Domain.Repositories;

import Transportation.DTO.TruckDTO;
import Transportation.DataAccess.SqliteTruckDAO;
import Transportation.DataAccess.TruckDAO;
import Transportation.Domain.Repositories.TruckRepository;
import Transportation.Domain.Truck;
import Transportation.Domain.TruckType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TruckRepositoryImpli implements TruckRepository {
    private final TruckDAO truckDAO;
    private final ArrayList<Truck> tempTruckList;

    public TruckRepositoryImpli() {
        this.truckDAO = new SqliteTruckDAO();
        tempTruckList = new ArrayList<>();
    }

    @Override
    public TruckDTO addTruck(String truckType, String licenseNumber, String model, float netWeight, float maxWeight, boolean isFree) throws SQLException {
        TruckDTO insertedTruckDTO = truckDAO.insert(new TruckDTO(null, truckType, licenseNumber, model, netWeight, maxWeight, true));
        tempTruckList.add(new Truck(insertedTruckDTO.truckId(), TruckType.fromString(truckType), insertedTruckDTO.licenseNumber(), insertedTruckDTO.model(), insertedTruckDTO.netWeight(), insertedTruckDTO.maxWeight()));
        return insertedTruckDTO;
    }

    @Override
    public Optional<TruckDTO> findTruckByLicense(String licenseNumber) throws SQLException {
        for (Truck currTruck : tempTruckList) {
            if (currTruck.getLicenseNumber() == licenseNumber) {
                TruckDTO foundTruck = new TruckDTO(currTruck.getTruckID(), currTruck.getTruckType().toString(), currTruck.getLicenseNumber(), currTruck.getModel(), currTruck.getNetWeight(), currTruck.getMaxWeight(), currTruck.isFree());
                return Optional.of(foundTruck);
            }
        }

        return truckDAO.findByLicense(licenseNumber);
    }

    @Override
    public Optional<TruckDTO> findTruckById(int truckId) throws SQLException {
        Truck foundTruck = findTruckInList(truckId);
        if (foundTruck != null) {
            TruckDTO toDTOTruck = toDTO(foundTruck);
            return Optional.of(toDTOTruck);
        }
        return truckDAO.findTruckById(truckId);
    }

    @Override
    public List<TruckDTO> getAllTrucks() throws SQLException {
        if (!tempTruckList.isEmpty()) {
            List<TruckDTO> returnedList = new ArrayList<>();
            for (Truck currTruck : tempTruckList) {
                returnedList.add(toDTO(currTruck));
            }
            return returnedList;
        } else {
            return truckDAO.findAll();
        }
    }

    @Override
    public void deleteTruck(int truckId) throws SQLException {
        Truck foundTruck = findTruckInList(truckId);
        if (foundTruck != null) {
            tempTruckList.remove(foundTruck);
        }
        truckDAO.delete(truckId);
    }

    @Override
    public void updateAvailability(int truckId, boolean status) throws SQLException {
        Truck foundTruck = findTruckInList(truckId);
        if (foundTruck != null) {
            foundTruck.setAvailability(status);
        }
        truckDAO.setAvailability(truckId, status);
    }

    // helper methods
    private TruckDTO toDTO(Truck truck) {
        return new TruckDTO(truck.getTruckID(), truck.getTruckType().toString(), truck.getLicenseNumber(), truck.getModel(), truck.getNetWeight(), truck.getMaxWeight(), truck.isFree());
    }

    private Truck findTruckInList(int truckId) {
        for (Truck currTruck : tempTruckList) {
            if (currTruck.getTruckID() == truckId) {
                return currTruck;
            }
        }
        return null;
    }
}