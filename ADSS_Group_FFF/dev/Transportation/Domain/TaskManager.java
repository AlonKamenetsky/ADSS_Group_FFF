package Transportation.Domain;

import Transportation.DTO.*;
import Transportation.Domain.Repositories.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class TaskManager {
    private final SiteManager siteManager;
    private final DriverManager driverManager;
    private final TruckManager truckManager;
    private final ItemManager itemManager;
    private final TransportationDocRepository docRepository;
    private final TransportationTaskRepository taskRepository;

    public TaskManager() {
        docRepository = new TransportationDocRepositoryImpli(new SiteRepositoryImpli());
        taskRepository = new TransportationTaskRepositoryImpli(new SiteRepositoryImpli());
        itemManager = new ItemManager();
        siteManager = new SiteManager();
        driverManager = new DriverManager();
        truckManager = new TruckManager();
    }

    public TaskManager(TransportationDocRepository docRepo, TransportationTaskRepository taskRepo, SiteManager siteManager, DriverManager driverManager, TruckManager truckManager, ItemManager itemManager) {
        this.docRepository = docRepo;
        this.taskRepository = taskRepo;
        this.siteManager = siteManager;
        this.driverManager = driverManager;
        this.truckManager = truckManager;
        this.itemManager = itemManager;
    }


    public TransportationTaskDTO addTask(LocalDate _taskDate, LocalTime _departureTime, String taskSourceSite) throws ParseException, SQLException {
        Optional<SiteDTO> site = siteManager.findSiteByAddress(taskSourceSite);
        if (site.isEmpty()) {
            throw new NoSuchElementException();
        }
        return taskRepository.createTask(_taskDate, _departureTime, taskSourceSite);
    }

    public void removeTask(LocalDate taskDate, LocalTime taskDeparture, String taskSourceSite) throws SQLException {
        int sourceSiteId = siteManager.findSiteByAddress(taskSourceSite).get().siteId();
        Optional<TransportationTaskDTO> task = taskRepository.findTaskByDateTimeAndSource(taskDate, taskDeparture, sourceSiteId);
        if (task.isPresent()) {
            // free truck and driver
            if(!task.get().truckLicenseNumber().isEmpty() && !task.get().driverId().isEmpty()) {
                // driverManager.setDriverAvailability(driverManager.getDriverId(), true);
                truckManager.setTruckAvailability(truckManager.getTruckIdByLicense(task.get().truckLicenseNumber()), true);
            }
            taskRepository.deleteTask(task.get().taskId());
        }
    }


    public void addDocToTask(LocalDate taskDate, LocalTime taskDeparture, String taskSourceSite,
                             String destinationSite, HashMap<String, Integer> itemsChosen) throws SQLException {
        int sourceSiteId = siteManager.findSiteByAddress(taskSourceSite).get().siteId();
        Optional<TransportationTaskDTO> task = taskRepository.findTaskByDateTimeAndSource(taskDate, taskDeparture, sourceSiteId);
        if (task.isPresent()) {
            //Create itemList and push it to database
            int listId = itemManager.makeList(itemsChosen);
            //Create doc (creating mapping between list and destination) and push it to database
            int destinationSiteId = siteManager.findSiteByAddress(destinationSite).get().siteId();
            docRepository.createDoc(task.get().taskId(), destinationSiteId, listId);
            //Phase add doc to task and add mapping to database
            taskRepository.addDestination(task.get().taskId(), destinationSiteId);
        }
    }


    public TransportationTaskDTO updateWeightForTask(LocalDate taskDate, LocalTime taskDeparture, String taskSourceSite) throws SQLException, NoSuchElementException {
        int sourceSiteId = siteManager.findSiteByAddress(taskSourceSite).get().siteId();
        Optional<TransportationTaskDTO> task = taskRepository.findTaskByDateTimeAndSource(taskDate, taskDeparture, sourceSiteId);
        float weight = 0;
        if (task.isPresent()) {
            List<TransportationDocDTO> taskDocs = docRepository.findDocByTaskId(task.get().taskId());
            for (TransportationDocDTO doc : taskDocs) {
                int itemsListId = docRepository.findDocItemsListId(doc.docId());
                weight += itemManager.findWeightList(itemsListId);
            }
            return taskRepository.updateWeight(task.get().taskId(), weight);
        } else {
            throw new NoSuchElementException();
        }
    }

    public boolean assignDriverAndTruckToTask(LocalDate taskDate, LocalTime taskDeparture, String taskSourceSite) throws SQLException {
        int sourceSiteId = siteManager.findSiteByAddress(taskSourceSite).get().siteId();
        Optional<TransportationTaskDTO> task = taskRepository.findTaskByDateTimeAndSource(taskDate, taskDeparture, sourceSiteId);
        if (task.isPresent()) {
            Optional<TruckDTO> nextAvailableTruck = truckManager.getNextTruckAvailable(task.get().weightBeforeLeaving());
            if (nextAvailableTruck.isEmpty()) {
                throw new NoSuchElementException();
            }
            //Optional<DriverDTO> nextFittingDriver = driverManager.getAvailableDriverByLicense(String.valueOf((LicenseMapper.getRequiredLicense(TruckType.fromString(nextAvailableTruck.get().truckType())))));
            //if (nextFittingDriver.isEmpty()) {
            //    throw new NoSuchElementException();
            //}

            // All good → assign
            //taskRepository.assignDriverToTask(nextFittingDriver.get().driverId;
            taskRepository.assignTruckToTask(task.get().taskId(), nextAvailableTruck.get().licenseNumber());
            //nextFittingDriver.setAvailability(false);
            truckManager.setTruckAvailability(nextAvailableTruck.get().truckId(), false);

            return true;
        } else {
            throw new NoSuchElementException();
        }
    }

    public String getTaskString(TransportationTaskDTO t, int counter) throws SQLException {
        StringBuilder sb = new StringBuilder("Transportation Task\n");
        sb.append(counter).append(". Transportation Task\n");
        sb.append("Source Site: ").append(t.sourceSiteAddress()).append("\n");
        sb.append("Departure Date: ").append(t.taskDate()).append("\n");
        sb.append("Departure Time: ").append(t.departureTime()).append("\n");
        sb.append("Driver Assigned: ").append(t.driverId()).append("\n");
        sb.append("Truck Assigned: ").append(t.truckLicenseNumber()).append("\n");
        sb.append("Weight Before Leaving: ").append(t.weightBeforeLeaving()).append(" kg\n");
        sb.append("Destinations:\n");

        for (TransportationDocDTO doc : docRepository.findDocByTaskId(t.taskId())) {
            String destinationAddress = siteManager.getSiteById(doc.destinationSite())
                    .orElseThrow(() -> new SQLException("Destination site not found")).siteAddress();
            sb.append("  - Destination: ").append(destinationAddress).append("\n");

            ItemsListDTO list = itemManager.getItemsList(docRepository.findDocItemsListId(doc.docId()));
            for (Map.Entry<Integer, Integer> entry : list.items().entrySet()) {
                String itemName = itemManager.getItemById(entry.getKey()).itemName();
                int quantity = entry.getValue();

                sb.append("    • ").append(itemName).append(" — Quantity: ").append(quantity).append("\n");
            }
        }

        sb.append("----------------------\n");
        return sb.toString();
    }

    public String getAllTasksString() throws SQLException {
        List<TransportationTaskDTO> allTasks = taskRepository.getAllTasks();
        int counter = 1;
        StringBuilder sb = new StringBuilder("All Tasks:\n");

        for (TransportationTaskDTO t : allTasks) {
            sb.append(getTaskString(t, counter));
            counter++;
        }

        return sb.toString();
    }

    public String getTasksBySourceAddress(String sourceAddress) throws SQLException {
        int sourceSiteId = siteManager.findSiteByAddress(sourceAddress)
                .orElseThrow(() -> new SQLException("Source site not found"))
                .siteId();
        int counter = 1;
        List<TransportationTaskDTO> tasks = taskRepository.findTaskBySourceAddress(sourceSiteId);
        StringBuilder sb = new StringBuilder();

        for (TransportationTaskDTO t : tasks) {
            sb.append(getTaskString(t, counter));
            counter++;
        }

        return sb.toString();
    }

    public boolean hasDestination(LocalDate taskDate, LocalTime taskDeparture, String taskSourceSite, String destinationSite) throws NoSuchElementException, SQLException {
        Optional<SiteDTO> site = siteManager.findSiteByAddress(destinationSite);
        if (site.isEmpty()) {
            throw new NoSuchElementException();
        }
        int sourceSiteId = siteManager.findSiteByAddress(taskSourceSite).get().siteId();
        Optional<TransportationTaskDTO> task = taskRepository.findTaskByDateTimeAndSource(taskDate, taskDeparture, sourceSiteId);
        if (task.isEmpty()) {
            throw new NoSuchElementException();
        }

        int taskId = task.get().taskId();
        return taskRepository.hasDestination(taskId, site.get().siteId());
    }
}