package Transportation.DataAccess.DAO;

import Transportation.DTO.TruckDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TruckDAO {
    TruckDTO insert(TruckDTO truck) throws SQLException;
    Optional<TruckDTO> findByLicense(String licenseNumber) throws SQLException;
    List<TruckDTO> findAll() throws SQLException;
    void delete(TruckDTO truckDTO) throws SQLException;
}