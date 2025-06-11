package Transportation.Domain;

import HR.DTO.EmployeeDTO;
import HR.Domain.Shift;
import Transportation.DTO.*;
import Transportation.Domain.Repositories.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class TaskManager {
    private final SiteManager siteManager;
    private final TruckManager truckManager;
    private final ItemManager itemManager;
    private final TransportationDocRepository docRepository;
    private final TransportationTaskRepository taskRepository;
    private final EmployeeProvider employeeProvider;

    public TaskManager(EmployeeProvider employeeProvider) {
        docRepository = new TransportationDocRepositoryImpli();
        taskRepository = new TransportationTaskRepositoryImpli(new SiteRepositoryImpli());
        itemManager = new ItemManager();
        siteManager = new SiteManager();
        truckManager = new TruckManager();
        this.employeeProvider = employeeProvider;
    }

    public TaskManager(SiteManager siteManager, TruckManager truckManager, ItemManager itemManager, TransportationDocRepository docRepository, TransportationTaskRepository taskRepository, EmployeeProvider employeeProvider) {
        this.siteManager = siteManager;
        this.truckManager = truckManager;
        this.itemManager = itemManager;
        this.docRepository = docRepository;
        this.taskRepository = taskRepository;
        this.employeeProvider = employeeProvider;
    }

    public TransportationTaskDTO addTask(LocalDate _taskDate, LocalTime _departureTime, String taskSourceSite) throws ParseException, SQLException {
        Optional<SiteDTO> site = siteManager.findSiteByAddress(taskSourceSite);
        if (site.isEmpty()) {
            throw new NoSuchElementException();
        }
        return taskRepository.createTask(_taskDate, _departureTime, taskSourceSite);
    }

    public void removeTask(LocalDate taskDate, LocalTime taskDeparture, String taskSourceSite) throws SQLException {
        Optional<TransportationTaskDTO> task = taskRepository.findTaskByDateTimeAndSource(taskDate, taskDeparture, taskSourceSite);
        if (task.isPresent()) {
            // free truck and driver
            if (!task.get().truckLicenseNumber().isEmpty() && !task.get().driverId().isEmpty()) {
                truckManager.setTruckAvailability(truckManager.getTruckIdByLicense(task.get().truckLicenseNumber()), true);
            }
            taskRepository.deleteTask(task.get().taskId());
        }
    }


    public void addDocToTask(LocalDate taskDate, LocalTime taskDeparture, String taskSourceSite,
                             String destinationSite, HashMap<String, Integer> itemsChosen) throws SQLException {
        int sourceSiteId = siteManager.findSiteByAddress(taskSourceSite).get().siteId();
        Optional<TransportationTaskDTO> task = taskRepository.findTaskByDateTimeAndSource(taskDate, taskDeparture, taskSourceSite);
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
        Optional<TransportationTaskDTO> task = taskRepository.findTaskByDateTimeAndSource(taskDate, taskDeparture, taskSourceSite);
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
        Optional<TransportationTaskDTO> task = taskRepository.findTaskByDateTimeAndSource(taskDate, taskDeparture, taskSourceSite);
        if (task.isPresent()) {
            Optional<TruckDTO> nextAvailableTruck = truckManager.getNextTruckAvailable(task.get().weightBeforeLeaving());
            if (nextAvailableTruck.isEmpty()) {
                throw new NoSuchElementException("No truck available for this task");
            }

            String licenseTypeNeeded = LicenseMapper.getRequiredLicense(TruckType.fromString(nextAvailableTruck.get().truckType())).toString();
            Shift.ShiftTime shiftTime = Shift.fromTime(taskDeparture);
            Date shiftDate = java.sql.Date.valueOf(taskDate);

            List<EmployeeDTO> availableDrivers = employeeProvider.findAvailableDrivers(licenseTypeNeeded, shiftDate, taskDeparture.toString());

            if (availableDrivers.isEmpty()) {
                throw new NoSuchElementException("No drivers available for this task");
            }

            // assign warehouse worker
            boolean availableWarehouseWorker = employeeProvider.findAvailableWarehouseWorkers(shiftDate, shiftTime);


            if (!availableWarehouseWorker) {
                throw new NoSuchElementException("No warehouse workers available for this task");
            }

            // All good → assign
            taskRepository.assignTruckToTask(task.get().taskId(), nextAvailableTruck.get().licenseNumber());
            truckManager.setTruckAvailability(nextAvailableTruck.get().truckId(), false);


            String shiftId = employeeProvider.getShiftIdByDateTime(shiftDate, taskDeparture.toString());

            int counter = availableDrivers.size();
            while (!availableDrivers.isEmpty()) {
                EmployeeDTO driverToAssign = availableDrivers.get(0);

                if (!taskRepository.hasOccupiedDriver(shiftId, driverToAssign.getId())) {
                    taskRepository.addOccupiedDriver(shiftId, driverToAssign.getId());
                    taskRepository.assignDriverToTask(task.get().taskId(), driverToAssign.getId());
                    break;
                }
                if (counter == 0) {
                    throw new NoSuchElementException("No drivers available for this task");
                }
                counter--;
            }

            return true;
        } else {
            throw new NoSuchElementException("This task isn't available");
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
        int counter = 1;
        List<TransportationTaskDTO> tasks = taskRepository.findTaskBySourceAddress(sourceAddress);
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
        Optional<TransportationTaskDTO> task = taskRepository.findTaskByDateTimeAndSource(taskDate, taskDeparture, taskSourceSite);
        if (task.isEmpty()) {
            throw new NoSuchElementException();
        }

        int taskId = task.get().taskId();
        return taskRepository.hasDestination(taskId, site.get().siteId());
    }
}