package Transportation.Data.DTO;

import Transportation.Domain.TransportationDoc;

public class TransportationDocDTO {
    private final int docId;
    private final int taskId;
    private final String destinationAddress;
    private final float totalWeight;

    public TransportationDocDTO(int docId, int taskId, String destinationAddress, float totalWeight) {
        this.docId = docId;
        this.taskId = taskId;
        this.destinationAddress = destinationAddress;
        this.totalWeight = totalWeight;
    }

    //Convert
    public static TransportationDocDTO fromEntity(TransportationDoc doc) {
        return new TransportationDocDTO(
                doc.getDocId(),
                doc.getTaskId(),
                doc.getDestinationSite().getAddress(),
                doc.getDocWeight()
        );
    }

    // Getters
    public int getDocId() {
        return docId;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public float getTotalWeight() {
        return totalWeight;
    }
}
