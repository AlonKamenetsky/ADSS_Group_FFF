package Transportation.DataAccess.DAO;

import Transportation.DTO.TransportationDocDTO;

import java.util.List;
import java.util.Optional;

public interface TransportationDocDAO {
    void insert(TransportationDocDTO transportationDoc);
    void update(TransportationDocDTO transportationDoc);
    void delete(int docId);
    Optional<TransportationDocDTO> findById(int docId);
    List<TransportationDocDTO> findByTaskId(int taskId);
    List<TransportationDocDTO> findByDestinationAddress(String address);
}