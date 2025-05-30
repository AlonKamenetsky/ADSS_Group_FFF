package Transportation.Domain.Repositories;

import Transportation.DTO.SiteDTO;
import Transportation.DTO.TransportationDocDTO;
import Transportation.DataAccess.SqliteTransportationDocDAO;
import Transportation.DataAccess.TransportationDocDAO;
import Transportation.Domain.ItemsList;
import Transportation.Domain.Site;
import Transportation.Domain.TransportationDoc;
import Transportation.Domain.Item;
import Transportation.Domain.Repositories.SiteRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransportationDocRepositoryImpli implements TransportationDocRepository {
    private final TransportationDocDAO docDAO;
    private final List<TransportationDoc> tempDocList;
    private final SiteRepository siteRepository;

    public TransportationDocRepositoryImpli(SiteRepository siteRepository) {
        this.docDAO = new SqliteTransportationDocDAO();
        this.tempDocList = new ArrayList<>();
        this.siteRepository = siteRepository;
    }

    @Override
    public TransportationDocDTO createDoc(int taskId, int destinationSiteId, int itemsListId) throws SQLException {
        TransportationDocDTO created = docDAO.insert(new TransportationDocDTO(null, taskId, destinationSiteId, itemsListId));
        Site destination_Site = siteRepository.fromSiteDTO(
                siteRepository.findSite(destinationSiteId)
                        .orElseThrow(() -> new SQLException("Site not found with id: " + destinationSiteId))
        );

        tempDocList.add(new TransportationDoc(taskId, created.docId(), destination_Site, itemsListId));
        return created;
    }

    @Override
    public void deleteDoc(int docId) throws SQLException {
        tempDocList.removeIf(doc -> doc.getDocId() == docId);
        docDAO.delete(docId);
    }

    @Override
    public int findDocItemsListId(int docId) throws SQLException {
        for (TransportationDoc doc : tempDocList) {
            if (doc.getDocId() == docId) {
                return doc.getDocWeight() > 0 ? doc.getDocId() : doc.getItemListId();
            }
        }
        return docDAO.findDocItemsListId(docId);
    }

    @Override
    public Optional<TransportationDocDTO> findDoc(int docId) throws SQLException {
        for (TransportationDoc doc : tempDocList) {
            if (doc.getDocId() == docId) {
                return Optional.of(new TransportationDocDTO(
                        doc.getDocId(),
                        doc.getTaskId(),
                        doc.getDestinationSite().getSiteId(),
                        doc.getItemListId())); //
            }
        }
        return docDAO.findById(docId);
    }

    @Override
    public List<TransportationDocDTO> findDocByTaskId(int taskId) throws SQLException {
        List<TransportationDocDTO> results = new ArrayList<>();
        for (TransportationDoc doc : tempDocList) {
            if (doc.getTaskId() == taskId) {
                results.add(new TransportationDocDTO(
                        doc.getDocId(),
                        doc.getTaskId(),
                        doc.getDestinationSite().getSiteId(),
                        doc.getItemListId()));
            }
        }
        if (!results.isEmpty()) {
            return results;
        }
        return docDAO.findByTaskId(taskId);
    }

    public void clearCache() {
        tempDocList.clear();
    }
}
