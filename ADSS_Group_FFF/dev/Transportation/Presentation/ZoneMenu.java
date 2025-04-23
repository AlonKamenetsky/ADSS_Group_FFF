package Transportation.Presentation;

import Transportation.Service.SiteZoneService;
import Transportation.Service.ZoneService;
import Transportation.Service.DataService;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class ZoneMenu {
    private final ZoneService zonesHandler;
    private final SiteZoneService siteZoneHandler;
    private final TManagerRoleMenu managerRoleMenu;
    private Scanner input;

    public ZoneMenu(ZoneService zoneService, SiteZoneService siteZoneHandler, TManagerRoleMenu managerRoleMenu) {
        zonesHandler = zoneService;
        this.siteZoneHandler = siteZoneHandler;
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
        try {
            zonesHandler.doesZoneExist(zoneName);
        } catch (NoSuchElementException e) {
            System.out.println("Zone doesn't exist.");
            return;
        }
        catch (NullPointerException n) {
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
                        siteZoneHandler.removeSiteFromZone(siteAddress1, zoneName);
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
        }
    }

    private void addZone() {
        System.out.println("Enter a zone name:");
        try {
            zonesHandler.AddZone(input.nextLine());
            System.out.println("Zone added successfully.");
        }
        catch (NullPointerException n) {
            System.out.println("Not a valid zone.");
        }
    }

    private void viewSitesByZone() {
        System.out.println("Enter a zone:");
        try {
            System.out.println(zonesHandler.getSitesByZone(input.nextLine()));
        } catch (NullPointerException e) {
            System.out.println("Not a valid zone.");
        } catch (NoSuchElementException n) {
            System.out.println("Zone doesn't exist.");
        }
    }

    private void viewAllZones() {
        System.out.println(zonesHandler.viewAllZones());
    }
}