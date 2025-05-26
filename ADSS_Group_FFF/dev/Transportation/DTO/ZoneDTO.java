package Transportation.DTO;

import java.util.ArrayList;

public record ZoneDTO(
        int zoneId,
        String zoneName,
        ArrayList<String> sitesRelated
) {
}