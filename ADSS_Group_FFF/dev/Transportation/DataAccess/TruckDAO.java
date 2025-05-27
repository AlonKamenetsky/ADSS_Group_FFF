package Transportation.DataAccess;

import Transportation.DTO.TruckDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TruckDAO {
    TruckDTO insert(TruckDTO truck) throws SQLException;
    Optional<TruckDTO> findByLicense(String licenseNumber) throws SQLException;
    Optional<TruckDTO> findTruckById(int truckId) throws SQLException;
    void setAvailability(int truckId, boolean status) throws SQLException;
    List<TruckDTO> findAll() throws SQLException;
    void delete(int truckId) throws SQLException;
}