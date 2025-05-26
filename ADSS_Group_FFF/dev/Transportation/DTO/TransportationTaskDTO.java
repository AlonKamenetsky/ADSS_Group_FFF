package Transportation.DTO;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record TransportationTaskDTO(
        Integer taskId,
        LocalDate taskDate,
        LocalTime departureTime,
        String sourceAddress,
        List<String> destinationsAddresses,
        String driverId,
        String truckLicenseNumber,
        float weightBeforeLeaving
) {
}