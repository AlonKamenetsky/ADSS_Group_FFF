package inventory;

public class Main {
    public static void main(String[] args) {
        InventoryService service = new InventoryService();
        SwingGUIManager gui = new SwingGUIManager(service);
        gui.setVisible(true);
        gui.bringToFront();
        gui.askToLoadSampleData();
    }
}
