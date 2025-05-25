package Transportation.Domain;

import java.util.HashMap;

public class ItemsList {
    private final int listId;
    private final HashMap<Item, Integer> itemsList;

    public ItemsList(int listId) {
        this.listId = listId;
        itemsList = new HashMap<>();
    }

    public void addItemToList(Item newItem, int quantity) {
        itemsList.put(newItem, quantity);
    }

    public int getListId() {
        return listId;
    }

    public void removeItemFromList(Item removeItem) {
        itemsList.remove(removeItem);
    }

    public float getListWeight() {
        final float[] listWeight = {0};
        itemsList.forEach((key, value) -> {
            listWeight[0] += key.getWeight() * value;
        });
        return listWeight[0];
    }

    @Override
    public String toString() {
        if (itemsList.isEmpty()) return "No items in list.";

        StringBuilder sb = new StringBuilder();
        for (var entry : itemsList.entrySet()) {
            sb.append("- ")
                    .append(entry.getKey().toString())
                    .append(", Quantity: ")
                    .append(entry.getValue())
                    .append("\n");
        }
        sb.append("Total Weight: ").append(getListWeight());
        return sb.toString();
    }
}