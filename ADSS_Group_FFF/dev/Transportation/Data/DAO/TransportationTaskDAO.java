package Transportation.Data.DAO;

import Transportation.Domain.TransportationTask;

import java.util.ArrayList;
import java.util.Date;
import java.time.LocalTime;

public interface TransportationTaskDAO {

    void insert(TransportationTask transportationTask);
    void update(TransportationTask transportationTask);
    void delete(String license);
    TransportationTask findById(int taskId);
    TransportationTask findByDateTimeAndSource(Date taskDate, LocalTime departureTime , String sourceSite);
    ArrayList<TransportationTask> findAll();
    ArrayList<TransportationTask> findBySourceAddress(String sourceAddress);
    ArrayList<TransportationTask> findByDriverId(String driverId);
}
