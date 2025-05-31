package Transportation.DataAccess;

import Transportation.DTO.TransportationTaskDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TransportationTaskDAO {

    TransportationTaskDTO insert(TransportationTaskDTO transportationTask) throws SQLException;
    void delete(int taskId) throws SQLException;
    Optional<TransportationTaskDTO> findById(int taskId) throws SQLException;
    Optional<TransportationTaskDTO> findByDateTimeAndSource(LocalDate taskDate, LocalTime departureTime , int sourceSiteAddress) throws SQLException;
    List<TransportationTaskDTO> findAll() throws SQLException;
    List<TransportationTaskDTO> findBySourceAddress(int sourceSiteId) throws SQLException;
    List<TransportationTaskDTO> findByDriverId(String driverId) throws SQLException;
    boolean hasDestination(int taskId, int siteId) throws SQLException;
    TransportationTaskDTO addDestination(int taskId, int destinationSiteId) throws SQLException;
    TransportationTaskDTO updateWeight(int taskId, float weight) throws SQLException;
    TransportationTaskDTO assignTruck(int taskId, String truckLicenseNumber) throws SQLException;
    TransportationTaskDTO assignDriver(int taskId, String driverId) throws SQLException;

}