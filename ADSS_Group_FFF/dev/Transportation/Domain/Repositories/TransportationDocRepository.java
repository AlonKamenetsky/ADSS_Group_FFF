package Transportation.Domain.Repositories;

import Transportation.DTO.TransportationDocDTO;
import Transportation.Domain.ItemsList;
import Transportation.Domain.Site;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TransportationDocRepository {

    TransportationDocDTO createDoc(int taskId, String destinationSite, float totalWeight) throws SQLException;
    void deleteDoc(int docId) throws SQLException;
    Optional<TransportationDocDTO> findDoc(int docId) throws SQLException;
    List<TransportationDocDTO> findDocByTaskId(int taskId) throws SQLException;
    //List<TransportationDocDTO> findDocByDestinationAddress(String address) throws SQLException;

}
