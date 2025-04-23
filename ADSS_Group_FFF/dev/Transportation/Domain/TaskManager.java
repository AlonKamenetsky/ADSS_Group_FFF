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
        allTasks = new HashMap<Integer, TransportationTask>();

    }

    public void addTask(Date _taskDate, LocalTime _departureTime, String sourceSiteAddress) {
        int _taskId = nextTaskId++;
        Site sourceSite = siteManager.getSiteByAddress(sourceSiteAddress.toLowerCase());
        if (sourceSite == null) {
            throw new NoSuchElementException();
        }
        TransportationTask newTask = new TransportationTask(_taskId, _taskDate, _departureTime, sourceSite);
        allTasks.put(_taskId, newTask);
    }

    public void removeTask(String taskDate, String departureTime, String sourceSite) {
        TransportationTask currTask = getTask(taskDate, departureTime, sourceSite);
        driverManager.setDriverAvailability(currTask.getDriverId(), true);
        truckManager.setTruckAvailability(currTask.getTruckLicenseNumber(), true);
        allTasks.remove(getTask(taskDate, departureTime, sourceSite).getTaskId());
    }

    public boolean doesTaskExist(String taskDate, String departureTime, String sourceSite) {
        int taskId = getTask(taskDate, departureTime, sourceSite).getTaskId();
        return allTasks.containsKey(taskId);
    }


    public void addDocToTask(String taskDate, String departureTime, String sourceSite, String destinationAddress, HashMap<String, Integer> itemsToAdd) {
        {
            TransportationTask currTask = getTask(taskDate, departureTime, sourceSite);
            Site destinationSite = siteManager.getSiteByAddress(destinationAddress);
            TransportationDoc newDoc = new TransportationDoc(currTask.getTaskId(), nextDocId++, destinationSite);
            for (Map.Entry<String, Integer> entry : itemsToAdd.entrySet()) {
                String itemName = entry.getKey();
                int quantity = entry.getValue();
                Item itemToAdd = itemManager.getItemByName(itemName);
                newDoc.addItem(itemToAdd, quantity);
            }
            currTask.addDoc(newDoc);
        }
    }

    public void updateWeightForTask(String taskDate, String taskDeparture, String taskSourceSite)  {
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


    public TransportationTask getTask(String _taskDate, String _departureTime, String sourceSite) {
        try {
            Site s = siteManager.getSiteByAddress(sourceSite);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date taskDate = sdf.parse(_taskDate);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime departureTime = LocalTime.parse(_departureTime, timeFormatter);
            for (TransportationTask t : allTasks.values()) {
                if (t.getTaskSourceAddress().equalsIgnoreCase(s.getAddress()) && t.getTaskDate().equals(taskDate) && t.getDepartureTime().equals(departureTime)) {
                    return t;
                }
            }
            return null;
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

    public float getWeightTask(String taskDate, String departureTime, String sourceSite) throws ParseException {
        return getTask(taskDate, departureTime, sourceSite).getWeightBeforeLeaving();
    }
}