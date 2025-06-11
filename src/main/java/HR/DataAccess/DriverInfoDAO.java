package HR.DataAccess;

import HR.Domain.DriverInfo;

public interface DriverInfoDAO {
    void insert(DriverInfo info);
    DriverInfo getByEmployeeId(String employeeId);
    void update(DriverInfo info);
    void delete(String employeeId);
}

