package Transportation.Domain.Repositories;

import Transportation.DTO.TruckDTO;
import Transportation.Domain.TruckType;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TruckRepository {
    TruckDTO addTruck(String truckType, String licenseNumber, String model, float netWeight, float maxWeight, boolean isFree) throws SQLException;
    Optional<TruckDTO> findTruckById(int truckId) throws SQLException;
    Optional<TruckDTO> findTruckByLicense(String licenseNumber) throws SQLException;
    List<TruckDTO> getAllTrucks() throws SQLException;
    void deleteTruck(int truckId) throws SQLException;
    void updateAvailability(int truckId, boolean status) throws SQLException;
}