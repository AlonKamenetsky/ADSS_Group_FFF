package HR.DataAccess;

import HR.Domain.SwapRequest;
import java.util.List;

public interface SwapDAO {
    void insert(SwapRequest request);
    void delete(int requestId);
    List<SwapRequest> selectAll(); // all pending
}
