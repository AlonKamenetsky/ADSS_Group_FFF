package HR.Domain;

import java.util.List;

public interface SwapRequestsRepo {
    List<SwapRequest> getSwapRequests();
    void addSwapRequest(SwapRequest request);
    void removeSwapRequest(SwapRequest request);
}
