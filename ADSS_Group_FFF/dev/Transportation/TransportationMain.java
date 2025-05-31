package Transportation;

import Transportation.Presentation.TransportationMenu;
import Transportation.SystemInitializer.SystemInitializer;

import java.sql.SQLException;

public class TransportationMain {
    public static void main(String[] args) throws SQLException {
        TransportationMenu menu = SystemInitializer.buildApplication();
        menu.show();
    }
}