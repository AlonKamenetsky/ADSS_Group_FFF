package Transportation.Domain;

public class DocItemsInfo {
    private  int itemId;
    private  int quantity;

    public void DocItemInfo(int itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public int getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }
}
