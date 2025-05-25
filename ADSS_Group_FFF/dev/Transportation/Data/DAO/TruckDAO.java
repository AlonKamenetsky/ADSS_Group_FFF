package Transportation.Data.DAO;
import Transportation.Domain.Truck;
import java.util.ArrayList;


public interface TruckDAO {

void insert(Truck truck);
Truck findByLicense(String license);
ArrayList<Truck> findAll();
void delete(String license);
void update(Truck truck);

}
