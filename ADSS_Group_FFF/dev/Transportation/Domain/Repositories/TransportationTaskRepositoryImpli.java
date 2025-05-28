package Transportation.Domain.Repositories;

import Transportation.DTO.TransportationTaskDTO;
import Transportation.DataAccess.SqliteTransportationTaskDAO;
import Transportation.DataAccess.TransportationTaskDAO;

import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransportationTaskRepositoryImpli implements TransportationTaskRepository {

    private final TransportationTaskDAO taskDAO;

    public TransportationTaskRepositoryImpli() {
        taskDAO = new SqliteTransportationTaskDAO();
    }


    @Override
    public TransportationTaskDTO createTask(LocalDate taskDate, LocalTime departureTime, int sourceSiteId) throws ParseException, SQLException {
        TransportationTaskDTO newTask = new TransportationTaskDTO(
                null,
                taskDate,
                departureTime,
                sourceSiteId,
                new ArrayList<>(),
                "",
                "",
                0
        );

        return taskDAO.insert(newTask);
    }


    @Override
    public void deleteTask(int taskId) throws SQLException {
        taskDAO.delete(taskId);

    }

    @Override
    public Optional<TransportationTaskDTO> findTask(int taskId) throws SQLException {
        return taskDAO.findById(taskId);
    }

    @Override
    public Optional<TransportationTaskDTO> findTaskByDateTimeAndSource(LocalDate taskDate, LocalTime departureTime, int sourceSiteId) throws SQLException {
        return taskDAO.findByDateTimeAndSource(taskDate, departureTime, sourceSiteId);
    }

    @Override
    public List<TransportationTaskDTO> findAllTasks() throws SQLException {
        return taskDAO.findAll();
    }

    @Override
    public List<TransportationTaskDTO> findTaskBySourceAddress(int sourceSiteId) throws SQLException {
        return taskDAO.findBySourceAddress(sourceSiteId);
    }

    @Override
    public List<TransportationTaskDTO> findTaskByDriverId(String driverId) throws SQLException {
        return taskDAO.findByDriverId(driverId);
    }

    @Override
    public boolean hasDestination(int taskId, int siteId) throws SQLException {
        return taskDAO.hasDestination(taskId, siteId);
    }

    @Override
    public TransportationTaskDTO addDestination(int taskId, int destinationSiteId) throws SQLException {
        return taskDAO.addDestination(taskId, destinationSiteId);
    }

    @Override
    public TransportationTaskDTO updateWeight(int taskId, float weight) throws SQLException {
        return taskDAO.updateWeight(taskId, weight);
    }

    public TransportationTaskDTO assignTruckToTask(int taskId, String truckLicenseNumber) throws SQLException {
        return taskDAO.assignTruck(taskId, truckLicenseNumber);
    }
}