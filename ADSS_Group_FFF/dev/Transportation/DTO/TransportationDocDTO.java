package Transportation.DTO;

public record TransportationDocDTO(
        int docId,
        int taskId,
        String destinationAddress,
        float totalWeight
) {
}