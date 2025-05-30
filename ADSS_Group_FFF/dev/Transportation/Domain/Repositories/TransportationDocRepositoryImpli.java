package Transportation.Domain.Repositories;

import Transportation.DTO.TransportationDocDTO;
import Transportation.DataAccess.SqliteTransportationDocDAO;
import Transportation.DataAccess.TransportationDocDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TransportationDocRepositoryImpli implements TransportationDocRepository {
    private final TransportationDocDAO docDAO;
    public TransportationDocRepositoryImpli() {
        this.docDAO = new SqliteTransportationDocDAO();
    }

    @Override
    public TransportationDocDTO createDoc(int taskId, int destinationSiteId, int itemsListId) throws SQLException {
        return docDAO.insert(new TransportationDocDTO(null,taskId,destinationSiteId, itemsListId));
    }

    @Override
    public void deleteDoc(int docId) throws SQLException {
        docDAO.delete(docId);
    }

    public int findDocItemsListId(int docId) throws SQLException {
        return docDAO.findDocItemsListId(docId);
    }
    @Override
    public Optional<TransportationDocDTO> findDoc(int docId) throws SQLException {
        return docDAO.findById(docId);
    }

    @Override
    public List<TransportationDocDTO> findDocByTaskId(int taskId) throws SQLException {
        return docDAO.findByTaskId(taskId) ;
    }
}