package Transportation.Presentation;

import Transportation.Service.ItemService;
import Transportation.Service.SiteService;
import Transportation.Service.TaskService;

import java.text.ParseException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class TaskMenu {
    private final TaskService TasksHandler;
    private final ItemService ItemHandler;
    private final SiteService SiteHandler;
    private final TManagerRoleMenu managerRoleMenu;
    private final Scanner input;

    public TaskMenu(TaskService taskService, ItemService itemService, SiteService siteHandler, TManagerRoleMenu managerRoleMenu) {
        TasksHandler = taskService;
        ItemHandler = itemService;
        SiteHandler = siteHandler;
        this.managerRoleMenu = managerRoleMenu;
        input = new Scanner(System.in);
    }

    public void show() {
        while (true) {
            System.out.println("""
                    === Task Management ===
                    1. View All Tasks
                    2. View Task By Source Site
                    3. Add Task
                    4. Remove Task
                    0. Return to Main Menu""");

            String choice = input.nextLine();
            switch (choice) {
                case "1":
                    viewAllTasks();
                    break;
                case "2":
                    viewTaskBySourceSite();
                    break;
                case "3":
                    addTask();
                    break;
                case "4":
                    removeTask();
                    break;
                case "0":
                    returnToMain();
                    return;
                default:
                    System.out.println("Invalid input.");
            }
        }
    }

    private void removeTask() {
        String taskDate, taskDeparture, taskSourceSite;
        System.out.println("Enter date of task (in this format: dd/MM/yyyy):");
        taskDate = input.nextLine();
        System.out.println("Enter time of departure (in this format: hh:mm):");
        taskDeparture = input.nextLine();
        System.out.println("Enter source site of the task:");
        taskSourceSite = input.nextLine();
        try {
            TasksHandler.deleteTask(taskDate, taskDeparture, taskSourceSite);
            System.out.println("Task removed successfully");
        } catch (ParseException e) {
            System.out.println("Invalid date/time format.");
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException n) {
            System.out.println("Empty parameters entered.");
        }
    }

    private void viewAllTasks() {
        System.out.println(TasksHandler.viewAllTasks());
    }

    private void returnToMain() {
        managerRoleMenu.show();
    }

    private void addTask() {
        String taskDate, taskDeparture, taskSourceSite;
        HashMap<String, Integer> itemsChosen = new HashMap<>();
        System.out.println("Enter date of task (in this format: dd/MM/yyyy):");
        taskDate = input.nextLine();
        System.out.println("Enter time of departure:");
        taskDeparture = input.nextLine();
        System.out.println("Enter source site of the task:");
        taskSourceSite = input.nextLine();
        try {
            TasksHandler.addTask(taskDate, taskDeparture, taskSourceSite.toLowerCase());
            System.out.println("Task added successfully without destination sites.");
        } catch (ParseException e) {
            System.out.println("Invalid date/time format");
            return;
        } catch (NoSuchElementException e) {
            System.out.println("Site doesn't exist.");
            return;
        }

        while (true) {
            System.out.println("Which site would you like to add to this task as destination? (must have at least one):");
            String destinationSite = input.nextLine();
            try {
                SiteHandler.getSiteByAddress(destinationSite);
            }
            catch (NullPointerException e) {
                System.out.println("Not a valid site.");
                continue;
            }
            catch (NoSuchElementException e) {
                System.out.println("Site doesn't exist.");
                continue;
            }
            if(TasksHandler.hasDestination(taskDate, taskDeparture, taskSourceSite.toLowerCase(), destinationSite)) {
                System.out.println("Task already has this destination.");
                continue;
            }
            System.out.println("""
                    Choose one of the following:
                    1. Choose items to add to this destination document (at least one).
                    2. Return to choosing more destination sites for this task.
                    3. Return to Task Management Menu""");
            String choice = input.nextLine();
            switch (choice) {
                case "1":
                    while (true) {
                        System.out.println(ItemHandler.viewAllItems());
                        System.out.println("Enter name of item you would like you add.");
                        String choiceItem = input.nextLine();
                        if (!ItemHandler.doesItemExist(choiceItem)) {
                            System.out.println("Given item doesn't exist.");
                            continue;
                        }
                        System.out.println("How many " + choiceItem + " would you like to add?");
                        try {
                            int inputQuantity = Integer.parseInt(input.nextLine());
                            itemsChosen.put(choiceItem, inputQuantity);
                        } catch (NumberFormatException e) {
                            System.out.println("Not a valid quantity, only numbers.");
                            continue;
                        }
                        System.out.println("Are you done adding items? (Yes/No)");
                        String done = input.nextLine();
                        if (done.equalsIgnoreCase("yes")) {
                            TasksHandler.addDocToTask(taskDate, taskDeparture, taskSourceSite, destinationSite, itemsChosen);
                            break;
                        }
                    }
                    break;

                case "2":
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
            if (choice.equals("2")) {
                continue;
            }
            TasksHandler.updateWeightForTask(taskDate, taskDeparture, taskSourceSite);
            System.out.println("Added destination to this site successfully");
            System.out.println("Do you want to add more destination sites or go back to Task Management menu? (Yes/Anything for no)");
            itemsChosen.clear();
            if (!input.nextLine().equalsIgnoreCase("yes")) {
                break;
            }
        }
        if (!TasksHandler.assignDriverAndTruckToTask(taskDate, taskDeparture, taskSourceSite)) {
            System.out.println("""
                        Adding task not successful, no drivers or trucks available for this task.
                        Consider choosing less items or taking off destination sites.
                        Full list of trucks is available to you in the Truck Menu.
                        Task is deleted for now. Thank you and sorry!""");
            try {
                TasksHandler.deleteTask(taskDate, taskDeparture, taskSourceSite);
            } catch (ParseException e) {
                return;
            }
        }
        else {
            System.out.println("Added task successfully.");
        }
    }

    private void viewTaskBySourceSite() {
        System.out.println("Enter a site address:");
        String siteAddress = input.nextLine();
        System.out.println(TasksHandler.getTasksBySourceAddress(siteAddress));
    }
}