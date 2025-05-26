package Transportation.DTO;

public record SiteDTO(
        Integer siteId,
        String siteName,
        String contactName,
        String phoneNumber,
        int zoneId
) {
}