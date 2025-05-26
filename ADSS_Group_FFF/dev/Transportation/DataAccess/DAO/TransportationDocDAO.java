package Transportation.DataAccess.DAO;

import Transportation.DTO.TransportationDocDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TransportationDocDAO {
    TransportationDocDTO insert(TransportationDocDTO transportationDoc) throws SQLException;
    void delete(int docId) throws SQLException;
    Optional<TransportationDocDTO> findById(int docId) throws SQLException;
    List<TransportationDocDTO> findByTaskId(int taskId) throws SQLException;
    List<TransportationDocDTO> findByDestinationAddress(String address) throws SQLException;
}