package Transportation.Domain.Repositories;

import Transportation.DTO.TransportationDocDTO;
import Transportation.DataAccess.DAO.SqliteTransportationDocDAO;
import Transportation.DataAccess.DAO.TransportationDocDAO;
import Transportation.Domain.ItemsList;
import Transportation.Domain.TransportationDoc;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class TransportationDocRepositoryImpli implements TransportationDocRepository {

    private final TransportationDocDAO DocDAO;
    public TransportationDocRepositoryImpli() {
        this.DocDAO = new SqliteTransportationDocDAO();
    }

    @Override
    public TransportationDocDTO createDoc(int taskId, String destinationSite, float totalWeight) throws SQLException {
        return DocDAO.insert(new TransportationDocDTO(null,taskId,destinationSite,totalWeight));
    }

    @Override
    public void deleteDoc(int docId) throws SQLException {
        DocDAO.delete(docId);
    }

    @Override
    public Optional<TransportationDocDTO> findDoc(int docId) throws SQLException {
        return DocDAO.findById(docId);
    }

    @Override
    public List<TransportationDocDTO> findDocByTaskId(int taskId) throws SQLException {
        return DocDAO.findByTaskId(taskId) ;
    }
}
