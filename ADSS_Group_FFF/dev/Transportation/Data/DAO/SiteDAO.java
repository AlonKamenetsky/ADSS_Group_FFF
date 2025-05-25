package Transportation.Data.DAO;
import  Transportation.Domain.Site;

import java.util.ArrayList;

public interface SiteDAO {
     void insert(Site site);
     void delete(Site site);
     void update(Site site);
     ArrayList<Site> findAll();
     Site findById(int siteId);
}
