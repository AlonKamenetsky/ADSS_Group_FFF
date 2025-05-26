package Transportation.DataAccess.DAO;

import Transportation.DTO.TransportationTaskDTO;

import java.util.ArrayList;
import java.util.Date;
import java.time.LocalTime;

public interface TransportationTaskDAO {

    void insert(TransportationTaskDTO transportationTask);
    void update(TransportationTaskDTO transportationTask);
    void delete(String license);
    TransportationTaskDTO findById(int taskId);
    TransportationTaskDTO findByDateTimeAndSource(Date taskDate, LocalTime departureTime , String sourceSite);
    ArrayList<TransportationTaskDTO> findAll();
    ArrayList<TransportationTaskDTO> findBySourceAddress(String sourceAddress);
    ArrayList<TransportationTaskDTO> findByDriverId(String driverId);
}