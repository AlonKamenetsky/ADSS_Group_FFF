package Transportation.DTO;

public record TransportationDocDTO(
        Integer docId,
        int taskId,
        int destinationSite,
        int itemsListId
) {
}