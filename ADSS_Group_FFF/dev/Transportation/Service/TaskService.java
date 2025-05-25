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

    public void addTask(String _taskDate, String _departureTime, String taskSourceSite) throws ParseException, NoSuchElementException, NullPointerException {
        if (_taskDate == null || _departureTime == null || taskSourceSite == null) {
            throw new NullPointerException();
        }

        taskManager.addTask(_taskDate, _departureTime, taskSourceSite.toLowerCase());
    }

    public void deleteTask(String taskDate, String taskDeparture, String taskSourceSite) throws NullPointerException, ParseException, NoSuchElementException {
        if (taskDate == null || taskDeparture == null || taskSourceSite == null) {
            throw new NullPointerException();
        }
        if (taskManager.doesTaskExist(taskDate, taskDeparture, taskSourceSite.toLowerCase())) {
            taskManager.removeTask(taskDate, taskDeparture, taskSourceSite.toLowerCase());
        }
        else {
            throw new NoSuchElementException("Task doesn't exist.");
        }
    }

    public void addDocToTask(String taskDate, String taskDeparture, String taskSourceSite, String destinationSite, HashMap<String, Integer> itemsChosen) {
        taskManager.addDocToTask(taskDate, taskDeparture, taskSourceSite.toLowerCase(), destinationSite.toLowerCase(), itemsChosen);
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

    public boolean hasDestination(String taskDate, String taskDeparture, String taskSourceSite, String destinationSite) throws NullPointerException, NoSuchElementException {
        if (taskDate == null || taskDeparture == null || taskSourceSite == null || destinationSite == null) {
            throw new NullPointerException();
        }
        else {
            return taskManager.hasDestination(taskDate, taskDeparture, taskSourceSite, destinationSite);
        }
    }
}