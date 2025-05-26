package Transportation.DataAccess.DAO;

import Transportation.DTO.TransportationTaskDTO;

import java.sql.SQLException;
import java.util.Date;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TransportationTaskDAO {

    TransportationTaskDTO insert(TransportationTaskDTO transportationTask) throws SQLException;
    void delete(String license) throws SQLException;
    Optional<TransportationTaskDTO> findById(int taskId) throws SQLException;
    Optional<TransportationTaskDTO> findByDateTimeAndSource(Date taskDate, LocalTime departureTime , String sourceSite) throws SQLException;
    List<TransportationTaskDTO> findAll() throws SQLException;
    List<TransportationTaskDTO> findBySourceAddress(String sourceAddress) throws SQLException;
    List<TransportationTaskDTO> findByDriverId(String driverId) throws SQLException;
}