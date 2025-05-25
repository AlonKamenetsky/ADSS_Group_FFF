package Transportation.Data.DAO;
import  Transportation.Domain.Zone;

import java.util.ArrayList;

public interface ZoneDAO {
     void insert(Zone zone);
     void delete(Zone zone);
     void update(Zone zone);
    Zone findById(int zoneId);
    Zone findByName(String zoneName);
    ArrayList<Zone> findAll();
}
