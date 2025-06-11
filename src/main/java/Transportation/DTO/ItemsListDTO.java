package Transportation.DTO;

import java.util.Map;

public record ItemsListDTO(
        int listId,
        Map<Integer, Integer> items // itemId -> quantity
) {}