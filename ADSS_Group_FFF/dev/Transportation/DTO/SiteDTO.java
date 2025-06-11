package Transportation.DTO;

public record SiteDTO(
        Integer siteId,
        String siteAddress,
        String contactName,
        String phoneNumber,
        int zoneId
) {
}