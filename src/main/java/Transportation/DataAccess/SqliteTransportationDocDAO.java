package Transportation.DataAccess;

import Transportation.DTO.TransportationDocDTO;
import Util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteTransportationDocDAO implements TransportationDocDAO {

    @Override
    public TransportationDocDTO insert(TransportationDocDTO doc) throws SQLException {
        String sql = "INSERT INTO transportation_docs(task_id, destination_site, item_list_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = Database.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, doc.taskId());
            ps.setInt(2, doc.destinationSite());
            ps.setInt(3, doc.itemsListId());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new TransportationDocDTO(
                        keys.getInt(1),
                        doc.taskId(),
                        doc.destinationSite(),
                        doc.itemsListId()
                );
            }
        }
    }

    @Override
    public void delete(int docId) throws SQLException {
        String sql = "DELETE FROM transportation_docs WHERE doc_id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, docId);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<TransportationDocDTO> findById(int docId) throws SQLException {
        String sql = """
                SELECT doc_id, task_id, destination_site, item_list_id
                FROM transportation_docs d
                WHERE d.doc_id = ?
                """;
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, docId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(new TransportationDocDTO(
                        rs.getInt("doc_id"),
                        rs.getInt("task_id"),
                        rs.getInt("destination_site"),
                        rs.getInt("item_list_id")
                ))
                        : Optional.empty();
            }
        }
    }

    @Override
    public List<TransportationDocDTO> findByTaskId(int taskId) throws SQLException {
        String sql = """
        SELECT doc_id, task_id, destination_site, item_list_id
        FROM transportation_docs
        WHERE task_id = ?
    """;

        List<TransportationDocDTO> list = new ArrayList<>();

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, taskId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new TransportationDocDTO(
                            rs.getInt("doc_id"),
                            rs.getInt("task_id"),
                            rs.getInt("destination_site"),
                            rs.getInt("item_list_id")
                    ));
                }
            }
        }

        return list;
    }


    @Override
    public List<TransportationDocDTO> findByDestinationAddress(String address) throws SQLException {
        String sql = """
                SELECT d.doc_id, d.task_id, s.address, d.item_list_id
                FROM transportation_docs d
                JOIN sites s ON d.destination_site = s.site_id
                WHERE s.address = ?
                """;

        List<TransportationDocDTO> list = new ArrayList<>();
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, address);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new TransportationDocDTO(
                            rs.getInt("doc_id"),
                            rs.getInt("task_id"),
                            rs.getInt("destination_site"),
                            rs.getInt("item_list_id")
                    ));
                }
            }
        }

        return list;
    }

    @Override
    public int findDocItemsListId(int docId) throws SQLException {
        String sql = "SELECT item_list_id FROM transportation_docs WHERE doc_id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, docId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("item_list_id");
                } else {
                    throw new SQLException("No document found with ID: " + docId);
                }
            }
        }
    }
}