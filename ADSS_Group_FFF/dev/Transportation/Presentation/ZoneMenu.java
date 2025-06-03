package Transportation.Presentation;

import Transportation.DTO.SiteDTO;
import Transportation.DTO.ZoneDTO;
import Transportation.Service.SiteZoneService;
import Transportation.Service.ZoneService;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ZoneMenu {
    private final ZoneService zonesHandler;
    private final SiteZoneService siteZoneHandler;
    private final TManagerRoleMenu managerRoleMenu;
    private final Scanner input;

    public ZoneMenu(TManagerRoleMenu managerRoleMenu) {
        this.zonesHandler = new ZoneService();
        this.siteZoneHandler = new SiteZoneService();
        this.managerRoleMenu = managerRoleMenu;
        input = new Scanner(System.in);
    }

    public void show() {
        while (true) {
            System.out.println("""
                    === Zone Management ===
                    1. View All Zones
                    2. View Sites Related to Zone
                    3. Add Zone
                    4. Remove Zone
                    5. Modify Zone
                    0. Return to Main Menu""");

            String choice = input.nextLine();
            switch (choice) {
                case "1":
                    viewAllZones();
                    break;
                case "2":
                    viewSitesByZone();
                    break;
                case "3":
                    addZone();
                    break;
                case "4":
                    removeZone();
                    break;
                case "5":
                    modifyZone();
                    break;
                case "0":
                    returnToMain();
                    return;
                default:
                    System.out.println("Invalid input.");
            }
        }
    }

    private void modifyZone() {
        System.out.println("Enter a zone:");
        String zoneName = input.nextLine();
        if (zoneName == null) {
            System.out.println("Invalid input.");
            return;
        }
        while (true) {
            System.out.println("""
                    What would you like to do?
                    1. Change Zone's name
                    2. Map site to this zone
                    3. Remove site's mapping from this zone
                    0. Return to Zone Menu""");

            String choice = input.nextLine();
            switch (choice) {
                case "1":
                    System.out.println("What do you want your zone's new name to be?");
                    try {
                        zonesHandler.UpdateZone(zoneName, input.nextLine());
                        System.out.println("Zone name updated.");
                    } catch (NullPointerException e) {
                        System.out.println("Not a valid input.");
                    } catch (NoSuchElementException e) {
                        System.out.println("Zone doesn't exist.");
                    }
                    break;
                case "2":
                    System.out.println("What is the address of the site you want to map to this zone?");
                    String siteAddress = input.nextLine();
                    try {
                        siteZoneHandler.addSiteToZone(siteAddress, zoneName);
                        System.out.println("Site added successfully.");
                    } catch (NullPointerException e) {
                        System.out.println("Not a valid input.");
                    } catch (NoSuchElementException n) {
                        System.out.println("Given site or zone don't exist.");
                    }
                    break;
                case "3":
                    System.out.println("What is the address of the site you want to remove from this zone's mapping?");
                    String siteAddress1 = input.nextLine();
                    try {
                        siteZoneHandler.removeSiteFromZone(siteAddress1);
                        System.out.println("Site removed successfully from this zone.");
                    } catch (NullPointerException e) {
                        System.out.println("Not a valid input.");
                    } catch (NoSuchElementException n) {
                        System.out.println("Given site or zone don't exist.");
                    }
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid input.");
            }
        }
    }

    private void returnToMain() {
        managerRoleMenu.show();
    }

    private void removeZone() {
        System.out.println("Enter a zone:");
        try {
            zonesHandler.deleteZone(input.nextLine());
            System.out.println("Zone removed successfully");
        } catch (NullPointerException e) {
            System.out.println("Empty zone name entered.");
        } catch (NoSuchElementException n) {
            System.out.println("Zone doesn't exist.");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addZone() {
        System.out.println("Enter a zone name:");
        try {
            zonesHandler.AddZone(input.nextLine());
            System.out.println("Zone added successfully.");
        } catch (NullPointerException n) {
            System.out.println("Not a valid zone.");
        } catch (InstanceAlreadyExistsException e) {
            System.out.println("Zone already exists.");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void viewSitesByZone() {
        System.out.println("Enter a zone:");
        try {
            String zoneName = input.nextLine();
            List<SiteDTO> allSites = siteZoneHandler.getSitesByZone(zoneName);
            StringBuilder sb = new StringBuilder("All Sites By " + zoneName.toUpperCase() + ":\n");
            for (SiteDTO site : allSites) {
                sb.append(String.format(
                        "Site Address: %s\nContact: %s\nPhone: %s\nZone: %s\n----------------------\n",
                        site.siteAddress(),
                        site.contactName(),
                        site.phoneNumber(),
                        zoneName.toUpperCase()
                ));
            }
            System.out.println(sb);
        } catch (NullPointerException e) {
            System.out.println("Not a valid zone.");
        } catch (NoSuchElementException n) {
            System.out.println("Zone doesn't exist.");
        }
    }

    private void viewAllZones() {
        try {
            List<ZoneDTO> allZones = zonesHandler.viewAllZones();
            StringBuilder sb = new StringBuilder("All Zones:\n");

            for (ZoneDTO zone : allZones) {
                ZoneDTO zonePopulated = siteZoneHandler.getZoneWithSites(zone.zoneName());
                sb.append(String.format("Zone Name: %s\nSites Mapped:\n", zonePopulated.zoneName()));

                int counter = 1;
                for (String siteAddress : zonePopulated.sitesRelated()) {
                    sb.append(String.format("  %d. %s\n", counter++, siteAddress));
                }

                sb.append("----------------------\n");
            }

            System.out.println(sb);
        }
        catch (NoSuchElementException e) {
            System.out.println("No zones found");
        }
    }
}