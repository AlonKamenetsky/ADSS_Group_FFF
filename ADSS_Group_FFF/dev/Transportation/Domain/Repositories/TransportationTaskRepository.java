package Transportation.Domain.Repositories;

import Transportation.DTO.TransportationTaskDTO;

import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TransportationTaskRepository {
    TransportationTaskDTO createTask(LocalDate taskDate, LocalTime departureTime, String sourceAddress) throws ParseException, SQLException;
    void deleteTask(int taskId) throws SQLException;
    Optional<TransportationTaskDTO> findTask(int taskId) throws SQLException;
    Optional<TransportationTaskDTO> findTaskByDateTimeAndSource(LocalDate taskDate, LocalTime departureTime , int sourceSiteId) throws SQLException;
    List<TransportationTaskDTO> getAllTasks() throws SQLException;
    List<TransportationTaskDTO> findTaskBySourceAddress(int sourceSiteId) throws SQLException;
    boolean hasDestination(int taskId, int siteId) throws SQLException;
    TransportationTaskDTO addDestination(int taskId, int destinationSiteId) throws SQLException;
    TransportationTaskDTO updateWeight(int taskId, float weight) throws SQLException;
    TransportationTaskDTO assignTruckToTask(int taskId, String truckLicenseNumber) throws SQLException;
    // TransportationTaskDTO assignDriverToTask(int taskId, String driverId) throws SQLException;

}