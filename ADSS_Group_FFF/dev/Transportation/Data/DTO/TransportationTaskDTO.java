package Transportation.Data.DTO;

import Transportation.Domain.TransportationTask;
import Transportation.Domain.Site;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class TransportationTaskDTO {
    private final int taskId;
    private final LocalDate taskDate;
    private final LocalTime departureTime;
    private final String sourceAddress;
    private final List<String> destinationAddresses;
    private final String driverId;
    private final String truckLicenseNumber;
    private final float weightBeforeLeaving;

    public TransportationTaskDTO(int taskId, LocalDate taskDate, LocalTime departureTime,
                                 String sourceAddress, List<String> destinationAddresses,
                                 String driverId, String truckLicenseNumber, float weightBeforeLeaving) {
        this.taskId = taskId;
        this.taskDate = taskDate;
        this.departureTime = departureTime;
        this.sourceAddress = sourceAddress;
        this.destinationAddresses = destinationAddresses;
        this.driverId = driverId;
        this.truckLicenseNumber = truckLicenseNumber;
        this.weightBeforeLeaving = weightBeforeLeaving;
    }

    public static TransportationTaskDTO fromEntity(TransportationTask task) {
        List<String> destinations = new ArrayList<>();

        for (Site site : task.getDestinationSites()) {
            destinations.add(site.getAddress());
        }

        return new TransportationTaskDTO(
                task.getTaskId(),
                task.getTaskDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                task.getDepartureTime(),
                task.getTaskSourceAddress(),
                destinations,
                task.getDriverId(),
                task.getTruckLicenseNumber(),
                task.getWeightBeforeLeaving()
        );
    }


    // Getters
    public int getTaskId() {
        return taskId;
    }

    public LocalDate getTaskDate() {
        return taskDate;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public List<String> getDestinationAddresses() {
        return destinationAddresses;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getTruckLicenseNumber() {
        return truckLicenseNumber;
    }

    public float getWeightBeforeLeaving() {
        return weightBeforeLeaving;
    }
}
