
import Transportation.Presentation.TransportationMenu;
import Transportation.SystemInitializer.SystemInitializer;

import java.sql.SQLException;

public class TransportationMain {
    public static void initTransportation() throws SQLException {
        TransportationMenu menu = SystemInitializer.buildApplication();
        menu.show();
    }
}