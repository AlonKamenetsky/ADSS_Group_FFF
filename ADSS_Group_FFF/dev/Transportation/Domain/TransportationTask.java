package Transportation.Domain;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

public class TransportationTask {
    private final int taskId;
    private String truckLicenseNumber;
    private final Date taskDate;
    private final LocalTime departureTime;
    private final Site sourceSite;
    private final ArrayList<Site> destinationSites;
    private float weightBeforeLeaving;
    private String driverId;
    private final HashMap<Integer, TransportationDoc> taskDocs;

    public TransportationTask(int _taskId, Date _taskDate, LocalTime _departureTime, Site _sourceSite) {
        taskId = _taskId;
        taskDate = _taskDate;
        truckLicenseNumber = ""; // "" = unassigned
        driverId = "";
        departureTime = _departureTime;
        sourceSite = _sourceSite;
        weightBeforeLeaving = 0;
        destinationSites = new ArrayList<>();
        taskDocs = new HashMap<>();
    }

    public String getTaskSourceAddress() {
        return sourceSite.getAddress();
    }

    public String getDriverId() {
        return driverId;
    }

    public int getTaskId() {
        return taskId;
    }

    public Date getTaskDate() {
        return taskDate;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public String getTruckLicenseNumber() {
        return truckLicenseNumber;
    }


    public void setWeightBeforeLeaving() {
        final float[] totalWeight = {0};
        taskDocs.forEach((key, value) -> {
            totalWeight[0] += value.getDocWeight();
        });
        weightBeforeLeaving = totalWeight[0];
    }

    public float getWeightBeforeLeaving() {
        return weightBeforeLeaving;
    }

    public void addDoc(TransportationDoc doc) {
        destinationSites.add(doc.getDestinationSite());
        taskDocs.put(doc.getDocId(), doc);
    }

    public void assignDriver(String driverId) {
        this.driverId = driverId;
    }

    public void assignTruck(String truckLicenseNumber) {
        this.truckLicenseNumber = truckLicenseNumber;
    }

    public ArrayList<Site> getDestinationSites() {
        return destinationSites;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Transportation Task\n");
        sb.append("Source Site: ").append(sourceSite).append("\n");
        sb.append("Departure Date: ").append(taskDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).append("\n");
        sb.append("Departure Time: ").append(departureTime).append("\n");
        sb.append("Driver Assigned: ").append(driverId).append("\n");
        sb.append("Truck Assigned: ").append(truckLicenseNumber).append("\n");
        sb.append("Weight Before Leaving: ").append(weightBeforeLeaving).append("\n");
        sb.append("Destinations:\n");

        for (TransportationDoc doc : taskDocs.values()) {
            sb.append(doc).append("\n");
        }

        return sb.toString();
    }

    public boolean hasDestination(String destinationSite) {
        for (Site s : destinationSites) {
            if (s.getAddress().equals(destinationSite)) {
                return true;
            }
        }
        return false;
    }
}