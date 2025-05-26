package Transportation.DataAccess.DAO;

import  Transportation.DTO.ZoneDTO;

import java.util.ArrayList;

public interface ZoneDAO {
     void insert(ZoneDTO zone);
     void delete(ZoneDTO zone);
     void update(ZoneDTO zone);
    ZoneDTO findById(int zoneId);
    ZoneDTO findByName(String zoneName);
    ArrayList<ZoneDTO> findAll();
}