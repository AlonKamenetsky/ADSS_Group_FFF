
package HR.tests.ServiceTests;

import HR.DTO.SwapRequestDTO;
import HR.Service.SwapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SwapServiceTest {

    private SwapService swapService;

    @BeforeEach
    public void setUp() {
        swapService = SwapService.getInstance();
    }

    @Test
    public void testCreateAndRetrieveSwapRequest() {
        String fromId = "empFrom";
        String toId = "empTo";
        String reason = "medical appointment";
        swapService.createSwapRequest(fromId, toId, reason);

        List<SwapRequestDTO> requests = swapService.getAllSwapRequests();
        assertTrue(requests.stream().anyMatch(r ->
                r.getFromEmployeeId().equals(fromId) &&
                        r.getToEmployeeId().equals(toId) &&
                        r.getReason().equals(reason)
        ));
    }

    @Test
    public void testApproveAndRejectSwapRequest() {
        String fromId = "empFrom2";
        String toId = "empTo2";
        String reason = "family event";
        swapService.createSwapRequest(fromId, toId, reason);

        List<SwapRequestDTO> requests = swapService.getAllSwapRequests();
        SwapRequestDTO request = requests.stream()
                .filter(r -> r.getFromEmployeeId().equals(fromId))
                .findFirst()
                .orElseThrow();

        swapService.approveSwap(request.getId());
        assertTrue(swapService.getSwapRequest(request.getId()).isApproved());

        swapService.rejectSwap(request.getId());
        assertFalse(swapService.getSwapRequest(request.getId()).isApproved());
    }
}
