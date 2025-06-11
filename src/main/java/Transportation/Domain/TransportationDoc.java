package Transportation.Domain;

public class TransportationDoc {
    private final int docId;
    private final int taskId;
    private final Site destinationSite;
    private final ItemsList docItems;

    public TransportationDoc(int taskId, int docId, Site destinationSite, int itemsListId) {
        this.taskId = taskId;
        this.docId = docId;
        this.destinationSite = destinationSite;
        docItems = new ItemsList(itemsListId);
    }

    public void addItem(Item newItem, int quantity) {
        docItems.addItemToList(newItem, quantity);
    }

    public Site getDestinationSite() {
        return destinationSite;
    }
    public int getDocId() {
        return docId;
    }
    public float getDocWeight() {
        return docItems.getListWeight();
    }


    public int getItemListId() {
        return docItems.getListId();
    }
    public int getTaskId(){
        return taskId;
    }

    public void removeItemFromDocList(Item removeItem) {
        docItems.removeItemFromList(removeItem);
    }


    @Override
    public String toString() {
        return "TransportationDoc to " + destinationSite +
                "\nTotal Weight: " + getDocWeight() +
                "\nItems:\n" + docItems;
    }

}