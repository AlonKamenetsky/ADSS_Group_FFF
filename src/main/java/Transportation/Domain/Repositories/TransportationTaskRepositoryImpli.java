package Transportation.Domain.Repositories;

import Transportation.DTO.DriverAvailabilityDTO;
import Transportation.DTO.TransportationTaskDTO;
import Transportation.DataAccess.SqliteTransportationTaskDAO;
import Transportation.DataAccess.TransportationTaskDAO;
import Transportation.Domain.Site;
import Transportation.Domain.TransportationTask;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TransportationTaskRepositoryImpli implements TransportationTaskRepository {

    private final TransportationTaskDAO taskDAO;
    private final SiteRepository siteRepository;


    public TransportationTaskRepositoryImpli(SiteRepository siteRepository) {
        taskDAO = new SqliteTransportationTaskDAO();
        this.siteRepository = siteRepository;
    }


    @Override
    public TransportationTaskDTO createTask(LocalDate taskDate, LocalTime departureTime, String sourceAddress) throws SQLException {
        TransportationTaskDTO newTask = new TransportationTaskDTO(
                null,
                taskDate,
                departureTime,
                sourceAddress,
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
    public Optional<TransportationTaskDTO> findTaskByDateTimeAndSource(LocalDate taskDate, LocalTime departureTime, String sourceSiteAddress) throws SQLException {
        return taskDAO.findByDateTimeAndSource(taskDate, departureTime, sourceSiteAddress);
    }

    @Override
    public List<TransportationTaskDTO> getAllTasks() throws SQLException {
        return taskDAO.findAll();
    }

    @Override
    public List<TransportationTaskDTO> findTaskBySourceAddress(String sourceSiteAddress) throws SQLException {
        return taskDAO.findBySourceAddress(sourceSiteAddress);
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

    public TransportationTaskDTO assignDriverToTask(int taskId, String driverId) throws SQLException {

        return taskDAO.assignDriver(taskId, driverId);
    }

    @Override
    public DriverAvailabilityDTO addOccupiedDriver(String shiftId, String driverId) throws SQLException {
        return taskDAO.addOccupiedDriver(shiftId, driverId);
    }

    @Override
    public void removeOccupiedDriver(String shiftId, String driverId) throws SQLException {
        taskDAO.removeOccupiedDriver(shiftId, driverId);
    }

    @Override
    public boolean hasOccupiedDriver(String shiftId, String driverId) throws SQLException {
        return taskDAO.hasOccupiedDriver(shiftId, driverId);
    }


    //helper methods


    private TransportationTaskDTO toDTO(TransportationTask task) {
        return new TransportationTaskDTO(
                task.getTaskId(),
                task.getTaskDate(),
                task.getDepartureTime(),
                task.getTaskSourceAddress(),
                task.getDestinationSites().stream()
                        .map(Site::getAddress)
                        .collect(Collectors.toCollection(ArrayList::new)),
                task.getDriverId(),
                task.getTruckLicenseNumber(),
                task.getWeightBeforeLeaving()
        );
    }
}