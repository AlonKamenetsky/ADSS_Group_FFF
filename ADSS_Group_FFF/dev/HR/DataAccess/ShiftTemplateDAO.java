package HR.DataAccess;

import HR.Domain.ShiftTemplate;
import java.util.List;

public interface ShiftTemplateDAO {
    void insert(ShiftTemplate template);
    List<ShiftTemplate> selectAll();
}
