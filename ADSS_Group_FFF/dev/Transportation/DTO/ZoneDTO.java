package Transportation.DTO;

import java.util.ArrayList;

public record ZoneDTO(
        Integer zoneId,
        String zoneName,
        ArrayList<String> sitesRelated
) {
}