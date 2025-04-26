package inventory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SwingGUIManager extends JFrame {
    private InventoryService service;

    public SwingGUIManager(InventoryService service) {
        this.service = service;
        setupGUI();
    }

    private void setupGUI() {
        setTitle("Inventory Management System (Swing GUI)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLayout(null);

        getContentPane().setBackground(new java.awt.Color(240, 248, 255)); // Light baby blue

        java.awt.Font font = new java.awt.Font("Arial", java.awt.Font.BOLD, 14);

        JButton viewInventoryBtn = new JButton("View Inventory");
        viewInventoryBtn.setBounds(100, 20, 300, 30);
        viewInventoryBtn.setFont(font);
        add(viewInventoryBtn);

        JButton lowStockBtn = new JButton("View Low Stock");
        lowStockBtn.setBounds(100, 60, 300, 30);
        lowStockBtn.setFont(font);
        add(lowStockBtn);

        JButton categoriesBtn = new JButton("View Categories");
        categoriesBtn.setBounds(100, 100, 300, 30);
        categoriesBtn.setFont(font);
        add(categoriesBtn);

        JButton updateQtyBtn = new JButton("Update Quantities");
        updateQtyBtn.setBounds(100, 140, 300, 30);
        updateQtyBtn.setFont(font);
        add(updateQtyBtn);

        JButton markStatusBtn = new JButton("Mark Damaged/Expired");
        markStatusBtn.setBounds(100, 180, 300, 30);
        markStatusBtn.setFont(font);
        add(markStatusBtn);

        JButton reportBtn = new JButton("Generate Report");
        reportBtn.setBounds(100, 220, 300, 30);
        reportBtn.setFont(font);
        add(reportBtn);

        JButton exportTxtBtn = new JButton("Export Inventory (TXT)");
        exportTxtBtn.setBounds(100, 260, 300, 30);
        exportTxtBtn.setFont(font);
        add(exportTxtBtn);

        JButton exportCsvBtn = new JButton("Export Inventory (CSV)");
        exportCsvBtn.setBounds(100, 300, 300, 30);
        exportCsvBtn.setFont(font);
        add(exportCsvBtn);

        JButton exitBtn = new JButton("Exit");
        exitBtn.setBounds(100, 340, 300, 30);
        exitBtn.setFont(font);
        add(exitBtn);

        viewInventoryBtn.addActionListener(e -> showAllItems());
        lowStockBtn.addActionListener(e -> showLowStockItems());
        categoriesBtn.addActionListener(e -> showCategories());
        updateQtyBtn.addActionListener(e -> updateQuantities());
        markStatusBtn.addActionListener(e -> markItemStatus());
        reportBtn.addActionListener(e -> generateReport());
        exportTxtBtn.addActionListener(e -> exportInventoryToTxt());
        exportCsvBtn.addActionListener(e -> exportInventoryToCsv());
        exitBtn.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    public void bringToFront() {
        setAlwaysOnTop(true);
        toFront();
        requestFocus();
        setAlwaysOnTop(false);
    }

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

    private void showAllItems() {
        StringBuilder sb = new StringBuilder();
        for (InventoryItem item : service.getAllItems()) {
            sb.append(formatItem(item));
            sb.append("\n-----------------------------\n\n");
        }
        showMessage(sb.toString(), "All Inventory Items");
    }

    private void showLowStockItems() {
        StringBuilder sb = new StringBuilder();
        for (InventoryItem item : service.getLowStockItems()) {
            sb.append(formatItem(item));
            sb.append("\n-----------------------------\n\n");
        }
        showMessage(sb.toString(), "Low Stock Items");
    }

    private void showCategories() {
        StringBuilder sb = new StringBuilder();
        for (Category cat : service.getAllCategories()) {
            sb.append(formatCategory(cat));
            sb.append("\n-----------------------------\n\n");
        }
        showMessage(sb.toString(), "Categories");
    }

    private void updateQuantities() {
        String itemId = JOptionPane.showInputDialog(this, "Enter Item ID:");
        InventoryItem itemToUpdate = null;
        for (InventoryItem item : service.getAllItems()) {
            if (item.getId().equals(itemId)) {
                itemToUpdate = item;
                break;
            }
        }

        if (itemToUpdate == null) {
            showMessage("Item not found.", "Error");
            return;
        }

        String[] options = {"Shelf Only", "Backroom Only", "Both"};
        int choice = JOptionPane.showOptionDialog(this, "Which quantity to update?", "Update Quantities",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        int shelfDelta = 0;
        int backroomDelta = 0;

        if (choice == 0) { // Shelf Only
            String shelfDeltaStr = JOptionPane.showInputDialog(this, "Enter quantity change for shelf:");
            shelfDelta = Integer.parseInt(shelfDeltaStr);
        } else if (choice == 1) { // Backroom Only
            String backroomDeltaStr = JOptionPane.showInputDialog(this, "Enter quantity change for backroom:");
            backroomDelta = Integer.parseInt(backroomDeltaStr);
        } else if (choice == 2) { // Both
            String shelfDeltaStr = JOptionPane.showInputDialog(this, "Enter quantity change for shelf:");
            String backroomDeltaStr = JOptionPane.showInputDialog(this, "Enter quantity change for backroom:");
            shelfDelta = Integer.parseInt(shelfDeltaStr);
            backroomDelta = Integer.parseInt(backroomDeltaStr);
        } else {
            return;
        }

        int beforeShelf = itemToUpdate.getShelfQuantity();
        int beforeBackroom = itemToUpdate.getBackroomQuantity();

        service.updateQuantities(itemId, shelfDelta, backroomDelta);

        String summary = "Update Successful!\n\n" +
                "Shelf: " + beforeShelf + " -> " + itemToUpdate.getShelfQuantity() + "\n" +
                "Backroom: " + beforeBackroom + " -> " + itemToUpdate.getBackroomQuantity();

        showMessage(summary, "Quantity Update Summary");
    }


    private void markItemStatus() {
        String itemId = JOptionPane.showInputDialog(this, "Enter Item ID:");
        String[] options = {"DAMAGED", "EXPIRED"};
        int choice = JOptionPane.showOptionDialog(this, "Choose Status:", "Mark Item Status",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0 || choice == 1) {
            ItemStatus status = (choice == 0) ? ItemStatus.DAMAGED : ItemStatus.EXPIRED;

            for (InventoryItem item : service.getAllItems()) {
                if (item.getId().equals(itemId)) {
                    item.setStatus(status);
                    showMessage("Item status updated successfully!", "Success");
                    return;
                }
            }
            showMessage("Item not found.", "Error");
        }
    }

    private void generateReport() {
        String reportId = JOptionPane.showInputDialog(this, "Enter Report ID:");
        String[] options = {"No Filter", "DAMAGED", "EXPIRED"};
        int choice = JOptionPane.showOptionDialog(this, "Filter by Status?", "Generate Report",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        ItemStatus filter = null;
        if (choice == 1) {
            filter = ItemStatus.DAMAGED;
        } else if (choice == 2) {
            filter = ItemStatus.EXPIRED;
        }

        InventoryReport report = service.generateReport(reportId, null, filter);

        StringBuilder sb = new StringBuilder();
        sb.append("Report ID: ").append(report.getId()).append("\n");
        sb.append("Generated on: ").append(report.getDateGenerated()).append("\n\n");
        sb.append("Items in Report:\n");

        for (InventoryItem item : report.getItems()) {
            sb.append(formatItem(item));
            sb.append("\n-----------------------------\n\n");
        }

        showMessage(sb.toString(), "Inventory Report");
    }

    private void exportInventoryToTxt() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Inventory as TXT");
        fileChooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home") + "/Desktop"));
        fileChooser.setSelectedFile(new java.io.File("inventory_export.txt"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String path = fileToSave.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".txt")) {
                fileToSave = new File(path + ".txt");
            }
            saveInventoryToFile(fileToSave);
        }
    }

    private void exportInventoryToCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Inventory as CSV");
        fileChooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home") + "/Desktop"));
        fileChooser.setSelectedFile(new java.io.File("inventory_export.csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String path = fileToSave.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".csv")) {
                fileToSave = new File(path + ".csv");
            }
            saveInventoryToCsv(fileToSave);
        }
    }

    private void saveInventoryToFile(File file) {
        StringBuilder sb = new StringBuilder();
        for (InventoryItem item : service.getAllItems()) {
            sb.append(formatItem(item));
            sb.append("\n-----------------------------\n\n");
        }
        try {
            java.nio.file.Files.write(file.toPath(), sb.toString().getBytes());
            showExportSuccess(file.getAbsolutePath());
        } catch (Exception e) {
            showMessage("Failed to export inventory.", "Error");
        }
    }

    private void saveInventoryToCsv(File file) {
        StringBuilder sb = new StringBuilder();
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
        try {
            java.nio.file.Files.write(file.toPath(), sb.toString().getBytes());
            showExportSuccess(file.getAbsolutePath());
        } catch (Exception e) {
            showMessage("Failed to export inventory.", "Error");
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
                "Subcategories: " + (cat.getSubCategories() != null && !cat.getSubCategories().isEmpty() ?
                cat.getSubCategories().size() : "None");
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

    private void showExportSuccess(String fullPath) {
        JTextArea textArea = new JTextArea("File saved successfully:\n\n" + fullPath);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(500, 200));

        JOptionPane.showMessageDialog(this, scrollPane, "Export Successful", JOptionPane.INFORMATION_MESSAGE);
    }
}
