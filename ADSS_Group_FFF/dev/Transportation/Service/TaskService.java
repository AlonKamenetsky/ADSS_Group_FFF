package Transportation.Service;

import Transportation.Domain.*;

import java.text.ParseException;
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

        taskManager.addTask(_taskDate, _departureTime, sourceSiteAddress.toLowerCase());
    }

    public void deleteTask(String _taskDate, String _departureTime, String sourceSiteAddress) throws NullPointerException, ParseException, NoSuchElementException {
        if (_taskDate == null || _departureTime == null || sourceSiteAddress == null) {
            throw new NullPointerException();
        }
        if (taskManager.doesTaskExist(_taskDate, _departureTime, sourceSiteAddress.toLowerCase())) {
            taskManager.removeTask(_taskDate, _departureTime, sourceSiteAddress.toLowerCase());
        }
        else {
            throw new NoSuchElementException("Task doesn't exist.");
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


    public String getTasksBySourceAddress(String sourceAddress) {
        return taskManager.getTasksBySourceAddress(sourceAddress.toLowerCase());
    }

    public boolean hasDestination(String taskDate, String taskDeparture, String sourceSite, String destinationSite) throws NullPointerException {
        if (taskDate == null || taskDeparture == null || sourceSite == null || destinationSite == null) {
            throw new NullPointerException();
        }
        else {
            return taskManager.hasDestination(taskDate, taskDeparture, sourceSite, destinationSite);
        }
    }
}