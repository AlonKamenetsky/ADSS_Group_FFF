package Transportation.Domain.Repositories;

import Transportation.DTO.TruckDTO;
import Transportation.DataAccess.DAO.SqliteTruckDAO;
import Transportation.DataAccess.DAO.TruckDAO;
import Transportation.Domain.TruckType;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TruckRepositoryImpli implements TruckRepository {
    private TruckDAO truckDAO;
    public TruckRepositoryImpli() {
        this.truckDAO = new SqliteTruckDAO();
    }

    @Override
    public TruckDTO addTruck(TruckType truckType, String licenseNumber, String model, float netWeight, float maxWeight, boolean isFree) throws SQLException {
        return truckDAO.insert(new TruckDTO(null,truckType,licenseNumber,model,netWeight,maxWeight,isFree));
    }

    @Override
    public Optional<TruckDTO> findTruckByLicense(String licenseNumber) throws SQLException {
        return truckDAO.findByLicense(licenseNumber);
    }

    @Override
    public List<TruckDTO> getAllTrucks() throws SQLException {
        return truckDAO.findAll();
    }

    @Override
    public void deleteTruck(TruckDTO truckDTO) throws SQLException {
        truckDAO.delete(truckDTO);
    }
}
