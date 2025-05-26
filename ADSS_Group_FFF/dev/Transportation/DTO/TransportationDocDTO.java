package Transportation.DTO;

public record TransportationDocDTO(
        Integer docId,
        int taskId,
        String destinationAddress,
        float totalWeight
) {
}