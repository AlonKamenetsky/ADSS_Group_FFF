package Transportation.Domain.Repositories;

import Transportation.DTO.TransportationTaskDTO;

import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransportationTaskRepository {
    TransportationTaskDTO createTask(String taskDate, String departureTime, String sourceSiteAddress) throws ParseException, SQLException;
    void deleteTask(int taskId) throws SQLException;
    Optional<TransportationTaskDTO> findTask(int taskId) throws SQLException;
    Optional<TransportationTaskDTO> findTaskByDateTimeAndSource(Date taskDate, LocalTime departureTime , String sourceSite) throws SQLException;
    List<TransportationTaskDTO> findAllTasks() throws SQLException;
    List<TransportationTaskDTO> findTaskBySourceAddress(String sourceAddress) throws SQLException;
    List<TransportationTaskDTO> findTaskByDriverId(String driverId) throws SQLException;

}