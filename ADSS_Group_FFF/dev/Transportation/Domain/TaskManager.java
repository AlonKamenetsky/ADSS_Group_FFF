package Transportation.Domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TaskManager {
    private final HashMap<Integer, TransportationTask> allTasks;
    private final SiteManager siteManager;
    private final DriverManager driverManager;
    private final TruckManager truckManager;
    private final ItemManager itemManager;
    private int nextTaskId = 1;
    private int nextDocId = 1;

    public TaskManager(SiteManager siteManager1, DriverManager driverManager1, TruckManager truckManager1, ItemManager itemManager1) {
        itemManager = itemManager1;
        siteManager = siteManager1;
        driverManager = driverManager1;
        truckManager = truckManager1;
        allTasks = new HashMap<>();

    }

    public void addTask(String _taskDate, String _departureTime, String taskSourceSite) throws ParseException {
        int _taskId = nextTaskId++;
        Site sourceSite = siteManager.getSiteByAddress(taskSourceSite.toLowerCase());
        if (sourceSite == null) {
            throw new NoSuchElementException();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date taskDate = sdf.parse(_taskDate);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime departureTime = LocalTime.parse(_departureTime, timeFormatter);
        TransportationTask newTask = new TransportationTask(_taskId, taskDate, departureTime, sourceSite);
        allTasks.put(_taskId, newTask);
    }

    public void removeTask(String taskDate, String taskDeparture, String taskSourceSite) {
        TransportationTask currTask = getTask(taskDate, taskDeparture, taskSourceSite);
        if (!currTask.getDriverId().isEmpty() && !currTask.getTruckLicenseNumber().isEmpty()) {
            driverManager.setDriverAvailability(currTask.getDriverId(), true);
            truckManager.setTruckAvailability(currTask.getTruckLicenseNumber(), true);
        }
        allTasks.remove(currTask.getTaskId());
    }

    public boolean doesTaskExist(String taskDate, String taskDeparture, String taskSourceSite) throws NoSuchElementException, ParseException {
        TransportationTask currTask = getTask(taskDate, taskDeparture, taskSourceSite);
        if (currTask == null) {
            throw new ParseException("", 0);
        }
        int taskId = currTask.getTaskId();
        return allTasks.containsKey(taskId);
    }


    public void     addDocToTask(String taskDate, String taskDeparture, String taskSourceSite, String destinationSite, HashMap<String, Integer> itemsChosen) {
        TransportationTask currTask = getTask(taskDate, taskDeparture, taskSourceSite);
        Site destinationSite1 = siteManager.getSiteByAddress(destinationSite.toLowerCase());
        TransportationDoc newDoc = new TransportationDoc(currTask.getTaskId(), nextDocId++, destinationSite1);
        for (Map.Entry<String, Integer> entry : itemsChosen.entrySet()) {
            String itemName = entry.getKey();
            int quantity = entry.getValue();
            Item itemToAdd = itemManager.getItemByName(itemName);
            newDoc.addItem(itemToAdd, quantity);
        }
        currTask.addDoc(newDoc);
    }

    public void updateWeightForTask(String taskDate, String taskDeparture, String taskSourceSite) {
        TransportationTask task1 = getTask(taskDate, taskDeparture, taskSourceSite);
        task1.setWeightBeforeLeaving();
    }

    public boolean assignDriverAndTruckToTask(String taskDate, String taskDeparture, String taskSourceSite) {
        TransportationTask task = getTask(taskDate, taskDeparture, taskSourceSite);
        Truck nextAvailableTruck = truckManager.getNextTruckAvailable(task.getWeightBeforeLeaving());
        if (nextAvailableTruck == null) {
            return false;
        }
        Driver nextFittingDriver = driverManager.getAvailableDriverByLicense((LicenseMapper.getRequiredLicense(nextAvailableTruck.getTruckType())).name());
        if (nextFittingDriver == null) {
            return false;
        }

        // All good → assign
        task.assignDriver(nextFittingDriver.getDriverId());
        task.assignTruck(nextAvailableTruck.getLicenseNumber());
        nextFittingDriver.setAvailability(false);
        nextAvailableTruck.setAvailability(false);

        return true;
    }

    public List<TransportationTask> getAllTasks() {
        return new ArrayList<>(allTasks.values());
    }

    public String getAllTasksString() {
        List<TransportationTask> allTasks = getAllTasks();
        if (allTasks.isEmpty()) return "No tasks available.";

        StringBuilder sb = new StringBuilder("All Tasks:\n");
        for (TransportationTask t : allTasks) {
            sb.append(t).append("\n----------------------\n");
        }
        return sb.toString();
    }

    public TransportationTask getTask(String _taskDate, String _departureTime, String sourceSite) throws NoSuchElementException {
        try {
            Site s = siteManager.getSiteByAddress(sourceSite);
            if (s == null) {
                throw new NoSuchElementException("Site doesn't exist");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date taskDate = sdf.parse(_taskDate);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime departureTime = LocalTime.parse(_departureTime, timeFormatter);
            for (TransportationTask t : allTasks.values()) {
                if (t.getTaskSourceAddress().equalsIgnoreCase(s.getAddress()) && t.getTaskDate().equals(taskDate) && t.getDepartureTime().equals(departureTime)) {
                    return t;
                }
            }
            throw new NoSuchElementException("Task doesn't exist");
        } catch (ParseException e) {
            return null;
        }
    }

    public String getTasksBySourceAddress(String sourceAddress) {
        StringBuilder sb = new StringBuilder();
        boolean found = false;

        for (TransportationTask t : allTasks.values()) {
            if (t.getTaskSourceAddress().equalsIgnoreCase(sourceAddress)) {
                sb.append(t).append("\n----------------------\n");
                found = true;
            }
        }

        if (!found) {
            return "";
        }

        return sb.toString();
    }

    public String getTasksByDriverId(String driverId) {
        StringBuilder sb = new StringBuilder();
        boolean found = false;

        for (TransportationTask t : allTasks.values()) {
            if (t.getDriverId().equalsIgnoreCase(driverId)) {
                sb.append(t).append("\n----------------------\n");
                found = true;
            }
        }

        if (!found) {
            return "";
        }

        return sb.toString();
    }

    public boolean hasDestination(String taskDate, String taskDeparture, String taskSourceSite, String destinationSite) throws NoSuchElementException {
        siteManager.getSiteByAddress(destinationSite);
        TransportationTask currTask = getTask(taskDate, taskDeparture, taskSourceSite);
        return currTask.hasDestination(destinationSite);
    }

//    make it work:
//    public TransportationDocDTO createDocByAddress(int taskId, String address) throws SQLException {
//        int siteId = siteDAO.findByAddress(address).orElseThrow().siteId();
//        return docDAO.insert(new TransportationDocDTO(null, taskId, siteId, 0f));
//    }
}