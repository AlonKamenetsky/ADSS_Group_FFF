package Transportation.Presentation;

import Transportation.DTO.SiteDTO;
import Transportation.Service.SiteService;
import Transportation.Service.SiteZoneService;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

public class SiteMenu {
    private final SiteService SitesHandler;
    private final SiteZoneService siteZoneHandler;
    private final TManagerRoleMenu managerRoleMenu;
    private final Scanner input;

    public SiteMenu(TManagerRoleMenu managerRoleMenu) {
        SitesHandler = new SiteService();
        siteZoneHandler = new SiteZoneService();
        this.managerRoleMenu = managerRoleMenu;
        input = new Scanner(System.in);
    }

    public void show() {
        while (true) {
            System.out.println("""
                    === Site Management ===
                    1. View All Sites
                    2. View Site By address
                    3. Add Site
                    4. Remove Site
                    0. Return to Main Menu""");

            String choice = input.nextLine();
            switch (choice) {
                case "1":
                    viewAllSites();
                    break;
                case "2":
                    viewSiteByAddress();
                    break;
                case "3":
                    addSite();
                    break;
                case "4":
                    removeSite();
                    break;
                case "0":
                    returnToMain();
                    return;
                default:
                    System.out.println("Invalid input.");

            }
        }
    }

    private void removeSite() {
        System.out.println("Enter site's address:");
        try {
            SitesHandler.deleteSite(input.nextLine());
            System.out.println("Site removed successfully");
        } catch (NullPointerException e) {
            System.out.println("Empty Site address entered.");
        } catch (NoSuchElementException n) {
            System.out.println("Site doesn't exist.");
        }
    }

    private void returnToMain() {
        managerRoleMenu.show();
    }

    public void viewAllSites() {
        List<SiteDTO> allSites = SitesHandler.viewAllSites();
        StringBuilder sb = new StringBuilder("All Sites:\n");

        for (SiteDTO site : allSites) {
            String zoneName = siteZoneHandler.getZoneNameBySite(site);
            sb.append(String.format(
                    "Site Address: %s\nContact: %s\nPhone: %s\nZone: %s\n----------------------\n",
                    site.siteAddress(),
                    site.contactName(),
                    site.phoneNumber(),
                    zoneName.toUpperCase()
            ));
        }

        System.out.println(sb);
    }


    public void viewSiteByAddress() {
        System.out.println("Enter a site's address:");
        Optional<SiteDTO> site = SitesHandler.getSiteByAddress(input.nextLine());
        if (site.isPresent()) {
            String zoneName = siteZoneHandler.getZoneNameBySite(site.get());
            System.out.printf(
                    "Site Address: %s\nContact: %s\nPhone: %s\nZone: %s\n----------------------\n%n",
                    site.get().siteAddress(),
                    site.get().contactName(),
                    site.get().phoneNumber(),
                    zoneName.toUpperCase());
        }
    }

    private void addSite() {
        System.out.println("Enter new site's address:");
        String inputAddress = input.nextLine();
        System.out.println("Enter new site's contact name:");
        String inputContactName = input.nextLine();
        System.out.println("Enter new site's contact's phone number:");
        String inputPhoneNumber = input.nextLine();
        try {
            Integer.parseInt(inputPhoneNumber);
        }
        catch (NumberFormatException e) {
            System.out.println("Not a valid phone number, must be numbers only.");
            return;
        }
        System.out.println("Enter new site's zone:");
        String inputZone = input.nextLine();
        try {
            SitesHandler.addSite(inputAddress, inputContactName, inputPhoneNumber);
            siteZoneHandler.addSiteToZone(inputAddress, inputZone);
            System.out.println("Site added successfully and was mapped to " + inputZone.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (NoSuchElementException n) {
            System.out.println("Zone doesn't exist.");
        } catch (InstanceAlreadyExistsException e) {
            System.out.println("Site already exists");
        }
    }
}