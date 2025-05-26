package Transportation.DataAccess.DAO;

import Transportation.DTO.TruckDTO;

import java.util.ArrayList;

public interface TruckDAO {
    void insert(TruckDTO truck);
    TruckDTO findByLicense(String licenseNumber);
    ArrayList<TruckDTO> findAll();
    void delete(String license);
    void update(TruckDTO truck);
}