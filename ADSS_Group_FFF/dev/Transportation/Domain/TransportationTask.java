package Transportation.Domain;

import java.time.LocalTime;
import java.util.*;

public class TransportationTask {
    private int taskId;
    private String truckLicenseNumber;
    private Date taskDate;
    private LocalTime departureTime;
    private Site sourceSite;
    private ArrayList<Site> destinationSites;
    private float weightBeforeLeaving;
    private String driverId;
    private HashMap<Integer, TransportationDoc> taskDocs;

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

    public void addDestination(Site destinationSite) {
        destinationSites.add(destinationSite);
        int newDocId = taskDocs.size() + 1;
        taskDocs.put(newDocId, new TransportationDoc(getTaskId(), newDocId, destinationSite));
    }

    public String getTaskSourceAddress() {
        return sourceSite.getAddress();
    }

    public String getDriverId() { return driverId;}

    public int getTaskId() {
        return taskId;
    }

    public Date getTaskDate() { return taskDate; }

    public LocalTime getDepartureTime() { return departureTime; }

    public String getTruckLicenseNumber() { return  truckLicenseNumber;}

    public void addItemToDestinationDoc(int docId, Item item) {


    }


    public void setWeightBeforeLeaving() {
        final float[] totalWeight = {0};
        taskDocs.forEach((key, value) -> {
            totalWeight[0] += value.getDocWeight();
        });
        weightBeforeLeaving = totalWeight[0];
    }

    public float getWeightBeforeLeaving() { return weightBeforeLeaving; }

    public TransportationDoc getDoc(Site destination) {
        for (TransportationDoc doc : taskDocs.values()) {
            if (doc.getDestinationSite().equals(destination)) {
                return doc;
            }
        }
        return null;
    }

    public HashMap<Integer, TransportationDoc> getTaskDocs() {
        return taskDocs;
    }

    public void addDoc(TransportationDoc doc) {
        taskDocs.put(doc.getDocId(), doc);
    }

    public void assignDriver(String driverId) {
        this.driverId = driverId;
    }

    public void assignTruck(String truckLicenseNumber) {
        this.truckLicenseNumber = truckLicenseNumber;
    }

    public boolean isReadyToDispatch() {
        return truckLicenseNumber.isEmpty() && !driverId.isEmpty();
    }

    public void removeItemFromTask(int docId, Item removeItem) {
        TransportationDoc currDoc = taskDocs.get(docId);
        currDoc.removeItemFromDocList(removeItem);
    }

    public boolean hasDriver() {
        return driverId.isEmpty();
    }

    public boolean hasTruck() {
        return truckLicenseNumber.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Transportation Task\n");
        sb.append("Source Site: ").append(sourceSite).append("\n");
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
}