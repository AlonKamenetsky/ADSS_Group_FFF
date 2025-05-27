package HR.DataAccess;

import HR.Domain.SwapRequest;
import HR.Presentation.PresentationUtils;

import java.util.ArrayList;
import java.util.List;

public class SwapRequestsRepo implements HR.Domain.SwapRequestsRepo {
    private static SwapRequestsRepo instance = null;

    private final List<SwapRequest> swapRequests;

    // Private constructor to enforce singleton pattern.
    private SwapRequestsRepo() {
        this.swapRequests = new ArrayList<>();
    }

    // Returns the single instance of SwapRequestsRepo.
    public static SwapRequestsRepo getInstance() {
        if (instance == null) {
            instance = new SwapRequestsRepo();
        }
        return instance;
    }

    // Retrieves the full list of swap requests.
    public List<SwapRequest> getSwapRequests() {
        return swapRequests;
    }

    // Adds a new swap request.
    public void addSwapRequest(SwapRequest request) {
        swapRequests.add(request);
        PresentationUtils.typewriterPrint("Swap request sent: " + request,20);
    }

    // Removes a swap request.
    public void removeSwapRequest(SwapRequest request) {
        swapRequests.remove(request);
        System.out.println("Swap request cancelled: " + request);
    }
}