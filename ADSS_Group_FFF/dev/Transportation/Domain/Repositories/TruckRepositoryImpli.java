package Transportation.Domain.Repositories;

import Transportation.DTO.TruckDTO;
import Transportation.DataAccess.SqliteTruckDAO;
import Transportation.DataAccess.TruckDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TruckRepositoryImpli implements TruckRepository {
    private final TruckDAO truckDAO;

    public TruckRepositoryImpli() {
        this.truckDAO = new SqliteTruckDAO();
    }

    @Override
    public TruckDTO addTruck(String truckType, String licenseNumber, String model, float netWeight, float maxWeight, boolean isFree) throws SQLException {
        return truckDAO.insert(new TruckDTO(null,truckType,licenseNumber,model,netWeight,maxWeight,isFree));
    }

    @Override
    public Optional<TruckDTO> findTruckByLicense(String licenseNumber) throws SQLException {
        return truckDAO.findByLicense(licenseNumber);
    }

    @Override
    public Optional<TruckDTO> findTruckById(int truckId) throws SQLException {
        return truckDAO.findTruckById(truckId);
    }

    @Override
    public List<TruckDTO> getAllTrucks() throws SQLException {
        return truckDAO.findAll();
    }

    @Override
    public void deleteTruck(int truckId) throws SQLException {
        truckDAO.delete(truckId);
    }

    @Override
    public void updateAvailability(int truckId, boolean status) throws SQLException {
        truckDAO.setAvailability(truckId, status);
    }
}