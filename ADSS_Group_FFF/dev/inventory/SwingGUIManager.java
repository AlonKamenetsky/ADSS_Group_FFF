package inventory;

import javax.swing.*;
import java.io.File;
import java.util.Collection;
import java.util.List;

public class SwingGUIManager extends JFrame {
    private InventoryService service;
    private int nextButtonY = 20;
    private static final int BUTTON_SPACING = 40;

    public SwingGUIManager(InventoryService service) {
        this.service = service;
        setupGUI();
    }

    private void setupGUI() {
        setTitle("Inventory Management System (Swing GUI)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new java.awt.Color(240, 248, 255));

        createButton("View Inventory", e -> showAllItems());
        createButton("View Low Stock", e -> showLowStockItems());
        createButton("View Categories", e -> showCategories());
        createButton("Update Quantities", e -> updateQuantities());
        createButton("Mark Damaged/Expired", e -> markItemStatus());
        createButton("Generate Report", e -> generateReport());
        createButton("Export Inventory (TXT)", e -> exportInventoryToTxt());
        createButton("Export Inventory (CSV)", e -> exportInventoryToCsv());
        createButton("Add New Item", e -> addItem());
        createButton("Add New Category", e -> addCategory());
        createButton("Exit", e -> System.exit(0));

        setVisible(true);
    }

    private JButton createButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setBounds(100, nextButtonY, 300, 30);
        button.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        button.addActionListener(action);
        add(button);
        nextButtonY += BUTTON_SPACING;
        return button;
    }

    // --- Button actions ---

    private void showAllItems() {
        int choice = chooseSort("items");
        displayItems(service.getAllItems(), choice);
    }

    private void showLowStockItems() {
        int choice = chooseSort("low stock items");
        displayItems(service.getLowStockItems(), choice);
    }

    private void showCategories() {
        int choice = chooseSortCategory();
        displayCategories(service.getAllCategories(), choice);
    }

    private void addItem() {
        try {
            String id = promptForInput("Enter item ID:");
            if (id == null) return;

            if (!id.matches("\\d{3}")) {
                showError("Item ID must be exactly 3 digits (e.g., 001, 123).");
                return;
            }

            if (itemIdExists(id)) {
                showError("An item with this ID already exists. Please choose a different ID.");
                return;
            }

            InventoryItem newItem = buildItem(id);
            service.addItem(newItem);
            showInfo("Item added successfully!");
        } catch (Exception ex) {
            showError("Error adding item: " + ex.getMessage());
        }
    }

    private void addCategory() {
        try {
            String name = promptForInput("Enter new category name:");
            if (name == null || name.trim().isEmpty()) return;

            Category parent = selectParentCategory();
            Category newCategory = new Category(name, parent);
            service.addCategory(newCategory);
            showInfo("Category added successfully!");
        } catch (Exception ex) {
            showError("Error adding category: " + ex.getMessage());
        }
    }

    private void updateQuantities() {
        try {
            InventoryItem item = findItemById();
            if (item == null) return;

            int[] changes = promptQuantityChange();
            service.updateQuantities(item.getId(), changes[0], changes[1]);
            showInfo("Quantities updated successfully.");
        } catch (Exception ex) {
            showError("Error updating quantities: " + ex.getMessage());
        }
    }

    private void markItemStatus() {
        InventoryItem item = findItemById();
        if (item == null) return;

        ItemStatus status = chooseStatus();
        item.setStatus(status);
        showInfo("Item status updated successfully.");
    }

    private void generateReport() {
        try {
            String reportId = promptForInput("Enter Report ID:");
            ItemStatus statusFilter = chooseStatusFilter();
            List<Category> categories = chooseCategoriesFilter();

            InventoryReport report = service.generateReport(reportId, categories, statusFilter);
            showReport(report);
        } catch (Exception ex) {
            showError("Error generating report: " + ex.getMessage());
        }
    }

    private void exportInventoryToTxt() {
        exportToFile("txt");
    }

    private void exportInventoryToCsv() {
        exportToFile("csv");
    }

    // --- Helpers below ---

    private boolean itemIdExists(String id) {
        return service.getAllItems().stream().anyMatch(item -> item.getId().equals(id));
    }

    private InventoryItem buildItem(String id) {
        String name = promptForInput("Enter item name:");
        String manufacturer = promptForInput("Enter manufacturer:");
        int shelfQuantity = promptForInt("Enter shelf quantity:");
        int backroomQuantity = promptForInt("Enter backroom quantity:");
        int minThreshold = promptForInt("Enter minimum threshold:");
        double purchasePrice = promptForDouble("Enter purchase price:");
        double salePrice = promptForDouble("Enter sale price:");
        Category category = selectCategory();

        return new InventoryItem(id, name, manufacturer, shelfQuantity, backroomQuantity,
                minThreshold, purchasePrice, salePrice, ItemStatus.NORMAL, category);
    }

    private String promptForInput(String message) {
        return JOptionPane.showInputDialog(this, message);
    }

    private int promptForInt(String message) {
        return Integer.parseInt(promptForInput(message));
    }

    private double promptForDouble(String message) {
        return Double.parseDouble(promptForInput(message));
    }

    private InventoryItem findItemById() {
        String id = promptForInput("Enter Item ID:");
        return service.getAllItems().stream().filter(i -> i.getId().equals(id)).findFirst().orElse(null);
    }

    private int[] promptQuantityChange() {
        String[] options = {"Shelf Only", "Backroom Only", "Both"};
        int choice = JOptionPane.showOptionDialog(this, "Which quantity to update?", "Update Quantities",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        int shelf = 0, backroom = 0;
        if (choice == 0) shelf = promptForInt("Enter quantity change for shelf:");
        else if (choice == 1) backroom = promptForInt("Enter quantity change for backroom:");
        else if (choice == 2) {
            shelf = promptForInt("Enter quantity change for shelf:");
            backroom = promptForInt("Enter quantity change for backroom:");
        }
        return new int[]{shelf, backroom};
    }

    private ItemStatus chooseStatus() {
        String[] options = {"DAMAGED", "EXPIRED"};
        int choice = JOptionPane.showOptionDialog(this, "Choose Status:", "Mark Item Status",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        return choice == 0 ? ItemStatus.DAMAGED : ItemStatus.EXPIRED;
    }

    private int chooseSort(String what) {
        String[] options = {"Sort by ID", "Sort by Name"};
        return JOptionPane.showOptionDialog(this, "How would you like to sort the " + what + "?", "Sort Options",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
    }

    private int chooseSortCategory() {
        String[] options = {"Sort by Name", "Sort by Parent Category"};
        return JOptionPane.showOptionDialog(this, "How would you like to sort the categories?", "Sort Options",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
    }

    private Category selectCategory() {
        Collection<Category> categories = service.getAllCategories();
        if (categories.isEmpty()) return null;
        String[] names = categories.stream().map(Category::getName).toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(this, "Choose a category:", "Category Selection",
                JOptionPane.PLAIN_MESSAGE, null, names, names[0]);
        return categories.stream().filter(c -> c.getName().equals(selected)).findFirst().orElse(null);
    }

    private Category selectParentCategory() {
        return selectCategory();
    }

    private ItemStatus chooseStatusFilter() {
        String[] options = {"No Filter", "DAMAGED", "EXPIRED"};
        int choice = JOptionPane.showOptionDialog(this, "Filter by Status?", "Status Filter",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (choice == 1) return ItemStatus.DAMAGED;
        if (choice == 2) return ItemStatus.EXPIRED;
        return null;
    }

    private List<Category> chooseCategoriesFilter() {
        Collection<Category> allCategories = service.getAllCategories();
        if (allCategories.isEmpty()) return null;

        String[] names = allCategories.stream().map(Category::getName).toArray(String[]::new);
        JList<String> list = new JList<>(names);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new java.awt.Dimension(300, 200));
        int result = JOptionPane.showConfirmDialog(this, scrollPane, "Select Categories",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            List<String> selected = list.getSelectedValuesList();
            return allCategories.stream().filter(c -> selected.contains(c.getName())).toList();
        }
        return null;
    }

    private void displayItems(Collection<InventoryItem> items, int sortChoice) {
        StringBuilder sb = new StringBuilder();
        items.stream()
                .sorted((a, b) -> sortChoice == 1 ? a.getName().compareToIgnoreCase(b.getName()) : a.getId().compareToIgnoreCase(b.getId()))
                .forEach(item -> sb.append(formatItem(item)).append("\n-----------------------------\n\n"));
        showMessage(sb.toString(), "Inventory Items");
    }

    private void displayCategories(Collection<Category> categories, int sortChoice) {
        StringBuilder sb = new StringBuilder();
        categories.stream()
                .sorted((a, b) -> {
                    if (sortChoice == 1) {
                        String pA = a.getParentCategory() != null ? a.getParentCategory().getName() : "";
                        String pB = b.getParentCategory() != null ? b.getParentCategory().getName() : "";
                        return pA.compareToIgnoreCase(pB);
                    } else {
                        return a.getName().compareToIgnoreCase(b.getName());
                    }
                })
                .forEach(c -> sb.append(formatCategory(c)).append("\n-----------------------------\n\n"));
        showMessage(sb.toString(), "Categories");
    }

    private void showReport(InventoryReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("Report ID: ").append(report.getId()).append("\n");
        sb.append("Generated on: ").append(report.getDateGenerated()).append("\n\n");
        sb.append("Items in Report:\n");
        for (InventoryItem item : report.getItems()) {
            sb.append(formatItem(item)).append("\n-----------------------------\n\n");
        }
        showMessage(sb.toString(), "Inventory Report");
    }

    private void exportToFile(String type) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Inventory as " + type.toUpperCase());
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        fileChooser.setSelectedFile(new File("inventory_export." + type));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith("." + type)) {
                file = new File(file.getAbsolutePath() + "." + type);
            }
            saveInventoryToFile(file, type);
        }
    }

    private void saveInventoryToFile(File file, String type) {
        try {
            StringBuilder sb = new StringBuilder();
            if ("csv".equals(type)) {
                sb.append("Item ID,Name,Manufacturer,Shelf Quantity,Backroom Quantity,Min Threshold,Purchase Price,Sale Price,Status,Category\n");
                for (InventoryItem item : service.getAllItems()) {
                    sb.append(item.getId()).append(",")
                            .append(item.getName()).append(",")
                            .append(item.getManufacturer()).append(",")
                            .append(item.getShelfQuantity()).append(",")
                            .append(item.getBackroomQuantity()).append(",")
                            .append(item.getMinThreshold()).append(",")
                            .append(item.getPurchasePrice()).append(",")
                            .append(item.getSalePrice()).append(",")
                            .append(item.getStatus()).append(",")
                            .append(item.getCategory() != null ? item.getCategory().getName() : "None")
                            .append("\n");
                }
            } else {
                for (InventoryItem item : service.getAllItems()) {
                    sb.append(formatItem(item)).append("\n-----------------------------\n\n");
                }
            }
            java.nio.file.Files.write(file.toPath(), sb.toString().getBytes());
            showInfo("File saved successfully: " + file.getAbsolutePath());
        } catch (Exception e) {
            showError("Failed to export inventory: " + e.getMessage());
        }
    }

    private String formatItem(InventoryItem item) {
        return "Item ID: " + item.getId() + "\n" +
                "Name: " + item.getName() + "\n" +
                "Manufacturer: " + item.getManufacturer() + "\n" +
                "Shelf Quantity: " + item.getShelfQuantity() + "\n" +
                "Backroom Quantity: " + item.getBackroomQuantity() + "\n" +
                "Minimum Threshold: " + item.getMinThreshold() + "\n" +
                "Purchase Price: " + item.getPurchasePrice() + "\n" +
                "Sale Price: " + item.getSalePrice() + "\n" +
                "Status: " + item.getStatus() + "\n" +
                "Category: " + (item.getCategory() != null ? item.getCategory().getName() : "None");
    }

    private String formatCategory(Category cat) {
        return "Category Name: " + cat.getName() + "\n" +
                "Parent Category: " + (cat.getParentCategory() != null ? cat.getParentCategory().getName() : "None") + "\n" +
                "Subcategories: " + (cat.getSubCategories() != null && !cat.getSubCategories().isEmpty() ? cat.getSubCategories().size() : "None");
    }

    private void showMessage(String message, String title) {
        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(500, 400));
        JOptionPane.showMessageDialog(this, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Bring the GUI window to the front (after creating it)
    public void bringToFront() {
        setAlwaysOnTop(true);
        toFront();
        requestFocus();
        setAlwaysOnTop(false);
    }

    // Ask the user if they want to load demo sample data
    public void askToLoadSampleData() {
        int choice = JOptionPane.showOptionDialog(
                this,
                "Would you like to load demo data (example stock)?",
                "Load Demo Data",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Yes", "No"},
                "Yes"
        );

        if (choice == JOptionPane.YES_OPTION) {
            AppInitializer.loadSampleData(service);
            JOptionPane.showMessageDialog(this, "Demo data loaded successfully!");
        }
    }

}
