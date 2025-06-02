package Util;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Transportation.DataAccess.*;
import Transportation.DTO.*;

public class DatabaseInitializer {
    public  void loadTransportationFakeData() throws SQLException {
        SqliteSiteDAO siteDAO = new SqliteSiteDAO();
        SqliteZoneDAO zoneDAO = new SqliteZoneDAO();
        SqliteTransportationTaskDAO taskDAO = new SqliteTransportationTaskDAO();
        SqliteTruckDAO truckDAO = new SqliteTruckDAO();

        //Adding Zones:
        ZoneDTO zone1 = zoneDAO.insert(new ZoneDTO(1, "Center", new ArrayList<>(Arrays.asList("Bareket 20 Shoham","Tel Aviv"))));
        ZoneDTO zone2 = zoneDAO.insert(new ZoneDTO(2, "East", new ArrayList<>(Arrays.asList("Yafo 123, Jerusalem", "David King Hotel, The dead sea"))));
        ZoneDTO zone3 = zoneDAO.insert(new ZoneDTO(3, "North", new ArrayList<>(Arrays.asList("Ben Gurion University","Mini Market Eilat"))));

        //Adding Sites
        SiteDTO site1 = siteDAO.insert(new SiteDTO(1, "Bareket 20 Shoham", "Liel", "0501111111", zone1.zoneId()));
        SiteDTO site2 = siteDAO.insert(new SiteDTO(2, "Ben Gurion University", "Lidor", "0502222222", zone3.zoneId()));

        //Adding Trucks
        TruckDTO truck1 = truckDAO.insert(new TruckDTO(1,"B","123","BMW",100F,120F,true));
        TruckDTO truck2 = truckDAO.insert(new TruckDTO(2,"C","555","BMW",133F,140F,true));

        //Adding Task
        //ChangeDriver to real
        TransportationTaskDTO task1 = taskDAO.insert(new TransportationTaskDTO(
                1, LocalDate.now(), LocalTime.of(9, 0), site1.siteAddress(),
                List.of(site2.siteAddress(),site1.siteAddress()), "DRIVER1", truck1.licenseNumber(), 50f
        ));
        TransportationTaskDTO task2 = taskDAO.insert(new TransportationTaskDTO(2,LocalDate.now(), LocalTime.of(9, 0), site2.siteAddress(),
                List.of(site2.siteAddress()), "DRIVER1", truck2.licenseNumber(), 100f
        ));

    }

    public void loadItems() throws SQLException {
        SqliteItemDAO itemDAO = new SqliteItemDAO();
        //Adding Items
        ItemDTO item1 = itemDAO.insert(new ItemDTO(1,"BAMBA",0.5F));
        ItemDTO item2 = itemDAO.insert(new ItemDTO(2,"CHICKEN",2F));
        ItemDTO item3 = itemDAO.insert(new ItemDTO(3,"SUGAR",1F));
    }

    public void loadHRData() throws SQLException {
    }
}