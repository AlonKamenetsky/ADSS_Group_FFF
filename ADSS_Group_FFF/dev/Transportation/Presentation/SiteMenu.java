package Transportation.Presentation;

import Transportation.Service.SiteService;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class SiteMenu {
    private final SiteService SitesHandler;
    private final TManagerRoleMenu managerRoleMenu;
    private final Scanner input;

    public SiteMenu(SiteService siteService, TManagerRoleMenu managerRoleMenu) {
        SitesHandler = siteService;
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
        System.out.println(SitesHandler.viewAllSites());
    }


    public void viewSiteByAddress() {
        System.out.println("Enter a site's address:");
        System.out.println(SitesHandler.getSiteByAddress(input.nextLine()));
    }

    private void addSite() {
        System.out.println("Enter new site's address:");
        String inputAddress = input.nextLine();
        System.out.println("Enter new site's contact name:");
        String inputContactName = input.nextLine();
        System.out.println("Enter new site's contact's phone number:");
        String inputPhoneNumber = input.nextLine();
        System.out.println("Enter new site's zone:");
        String inputZone = input.nextLine();
        try {
            SitesHandler.addSite(inputAddress, inputContactName, inputPhoneNumber, inputZone.toLowerCase());
            System.out.println("Site added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (NoSuchElementException n) {
            System.out.println("Zone doesn't exist.");
        }
    }
}