package Transportation.Domain.Repositories;

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
    private final ArrayList<TransportationTask> tempTasksList;
    private final SiteRepository siteRepository;


    public TransportationTaskRepositoryImpli(SiteRepository siteRepository) {
        taskDAO = new SqliteTransportationTaskDAO();
        tempTasksList = new ArrayList<>();
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
        TransportationTaskDTO insertedTask = taskDAO.insert(newTask);
        Site taskSite = siteRepository.fromSiteDTO(siteRepository.findBySiteAddress(insertedTask.sourceSiteAddress()).get());
        tempTasksList.add(new TransportationTask(insertedTask.taskId(), insertedTask.taskDate(), insertedTask.departureTime(), taskSite));
        return insertedTask;
    }


    @Override
    public void deleteTask(int taskId) throws SQLException {
        if (!tempTasksList.isEmpty()) {
            tempTasksList.remove(findTaskInListId(taskId));
        }
        taskDAO.delete(taskId);

    }

    @Override
    public Optional<TransportationTaskDTO> findTask(int taskId) throws SQLException {
        TransportationTask task = findTaskInListId(taskId);
        if (task != null) {
            return Optional.of(toDTO(task));
        }
        return taskDAO.findById(taskId);
    }

    @Override
    public Optional<TransportationTaskDTO> findTaskByDateTimeAndSource(LocalDate taskDate, LocalTime departureTime, int sourceSiteId) throws SQLException {
        if (!tempTasksList.isEmpty()) {
            TransportationTask task = findTaskInList(taskDate, departureTime, sourceSiteId);
            if (task != null) {
                return Optional.of(toDTO(task));
            }
        }
        return taskDAO.findByDateTimeAndSource(taskDate, departureTime, sourceSiteId);
    }

    @Override
    public List<TransportationTaskDTO> getAllTasks() throws SQLException {
        if (!tempTasksList.isEmpty()) {
            List<TransportationTaskDTO> returnedList = new ArrayList<>();
            for (TransportationTask task : tempTasksList) {
                returnedList.add(toDTO(task));
            }
            return returnedList;
        }
        return taskDAO.findAll();
    }

    @Override
    public List<TransportationTaskDTO> findTaskBySourceAddress(int sourceSiteId) throws SQLException {
        if (!tempTasksList.isEmpty()) {
            List<TransportationTaskDTO> returnedList = new ArrayList<>();
            List<TransportationTask> tasks = findTaskInListSource(sourceSiteId);
            if (tasks != null) {
                for (TransportationTask task : tasks) {
                    returnedList.add(toDTO(task));
                }
            }
            return returnedList;
        }
        return taskDAO.findBySourceAddress(sourceSiteId);
    }

    @Override
    public boolean hasDestination(int taskId, int siteId) throws SQLException {
        if (!tempTasksList.isEmpty()) {
            TransportationTask task = findTaskInListId(taskId);
            if (task != null) {
                String siteAddress = siteRepository.findSite(siteId).get().siteAddress();
                return task.hasDestination(siteAddress);
            }
        }
        return taskDAO.hasDestination(taskId, siteId);
    }

    @Override
    public TransportationTaskDTO addDestination(int taskId, int destinationSiteId) throws SQLException {
        if (!tempTasksList.isEmpty()) {
            TransportationTask task = findTaskInListId(taskId);
            Site site = siteRepository.fromSiteDTO(siteRepository.findSite(destinationSiteId).get());
            if (task != null) {
                task.addDestination(site);
                return toDTO(task);
            }
        }
        return taskDAO.addDestination(taskId, destinationSiteId);
    }

    @Override
    public TransportationTaskDTO updateWeight(int taskId, float weight) throws SQLException {
        TransportationTask task = findTaskInListId(taskId);
        if (task != null) {
            task.setWeightBeforeLeaving(weight);
        }
        return taskDAO.updateWeight(taskId, weight);
    }

    public TransportationTaskDTO assignTruckToTask(int taskId, String truckLicenseNumber) throws SQLException {
        TransportationTask task = findTaskInListId(taskId);
        if (task != null) {
            task.assignTruck(truckLicenseNumber);
        }
        return taskDAO.assignTruck(taskId, truckLicenseNumber);
    }

    public TransportationTaskDTO assignDriverToTask(int taskId, String driverId) throws SQLException {
        TransportationTask task = findTaskInListId(taskId);
        if (task != null) {
            task.assignDriver(driverId);
        }
        return taskDAO.assignDriver(taskId, driverId);
    }

    //helper methods

    private TransportationTask findTaskInListId(int taskId) {
        for (TransportationTask task : tempTasksList) {
            if (task.getTaskId() == taskId) {
                return task;
            }
        }
        return null;
    }

    private List<TransportationTask> findTaskInListSource(int sourceSiteId) throws SQLException {
        List<TransportationTask> returnedList = new ArrayList<>();
        for (TransportationTask task : tempTasksList) {
            int taskSiteId = siteRepository.findBySiteAddress(task.getTaskSourceAddress()).get().siteId();
            if (taskSiteId == sourceSiteId) {
                returnedList.add(task);
            }
            return returnedList;
        }
        return null;
    }

    private TransportationTask findTaskInList(LocalDate taskDate, LocalTime departureTime, int sourceSiteId) throws SQLException {
        for (TransportationTask task : tempTasksList) {
            int taskSiteId = siteRepository.findBySiteAddress(task.getTaskSourceAddress()).get().siteId();
            if (task.getTaskDate() == taskDate && task.getDepartureTime() == departureTime && taskSiteId == sourceSiteId) {
                return task;
            }
        }
        return null;
    }

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