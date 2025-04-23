package Transportation.Service;

import Transportation.Domain.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class TaskService {
    private final TaskManager taskManager;

    public TaskService(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void addTask(String _taskDate, String _departureTime, String sourceSiteAddress) throws ParseException, NoSuchElementException, NullPointerException {
        if (_taskDate == null || _departureTime == null || sourceSiteAddress == null) {
            throw new NullPointerException();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date taskDate = sdf.parse(_taskDate);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime departureTime = LocalTime.parse(_departureTime, timeFormatter);
        taskManager.addTask(taskDate, departureTime, sourceSiteAddress.toLowerCase());
    }

    public void deleteTask(String _taskDate, String _departureTime, String sourceSiteAddress) {
        if (_taskDate == null || _departureTime == null || sourceSiteAddress == null) {
            return;
        }
        if (taskManager.doesTaskExist(_taskDate, _departureTime, sourceSiteAddress.toLowerCase())) {
            taskManager.removeTask(_taskDate, _departureTime, sourceSiteAddress.toLowerCase());
        }
    }

    public void addDocToTask(String taskDate, String departureTime, String sourceSite, String destinationAddress, HashMap<String, Integer> itemsToAdd) {
        taskManager.addDocToTask(taskDate, departureTime, sourceSite.toLowerCase(), destinationAddress.toLowerCase(), itemsToAdd);
    }

    public void updateWeightForTask(String taskDate, String taskDeparture, String taskSourceSite) {
        taskManager.updateWeightForTask(taskDate, taskDeparture, taskSourceSite.toLowerCase());
    }

    public boolean assignDriverAndTruckToTask(String taskDate, String taskDeparture, String taskSourceSite) {
        return taskManager.assignDriverAndTruckToTask(taskDate, taskDeparture, taskSourceSite.toLowerCase());
    }

    public String getTasksByDriverId(String driverId) {
        return taskManager.getTasksByDriverId(driverId);
    }

    public String viewAllTasks() {
        return taskManager.getAllTasksString();
    }

    public String getTask(String taskDate, String departureTime, String sourceSiteAddress) {
        return taskManager.getTask(taskDate, departureTime, sourceSiteAddress.toLowerCase()).toString();
    }

    public String getTasksBySourceAddress(String sourceAddress) {
        return taskManager.getTasksBySourceAddress(sourceAddress.toLowerCase());
    }
}