package Util;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import HR.DTO.CreateEmployeeDTO;
import HR.DTO.RoleDTO;
import HR.DTO.ShiftTemplateDTO;
import HR.DataAccess.ShiftDAO;
import HR.DataAccess.ShiftDAOImpl;
import HR.Domain.DriverInfo;
import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Service.EmployeeService;
import HR.Service.RoleService;
import HR.Service.ShiftService;
import HR.Presentation.PresentationUtils;

// FIXED: Use DTOs from Transportation.DTO, not DataAccess
import Transportation.DTO.ItemDTO;
import Transportation.DTO.SiteDTO;
import Transportation.DTO.TruckDTO;
import Transportation.DTO.ZoneDTO;
import Transportation.DataAccess.SqliteItemDAO;
import Transportation.DataAccess.SqliteSiteDAO;
import Transportation.DataAccess.SqliteTruckDAO;
import Transportation.DataAccess.SqliteTransportationTaskDAO;
import Transportation.DataAccess.SqliteZoneDAO;

public class DatabaseInitializer {

    private final RoleService roleService;
    private final EmployeeService employeeService;
    private final ShiftService shiftService;

    public DatabaseInitializer(
            RoleService roleService,
            EmployeeService employeeService,
            ShiftService shiftService
    ) {
        this.roleService = Objects.requireNonNull(roleService);
        this.employeeService = Objects.requireNonNull(employeeService);
        this.shiftService = Objects.requireNonNull(shiftService);
    }

    /**
     * Load some fake Transportation data (Zones, Sites, Trucks, etc.).
     */
    public void loadTransportationFakeData() throws SQLException {
        SqliteSiteDAO siteDAO               = new SqliteSiteDAO();
        SqliteZoneDAO zoneDAO               = new SqliteZoneDAO();
        SqliteTransportationTaskDAO taskDAO = new SqliteTransportationTaskDAO();
        SqliteTruckDAO truckDAO             = new SqliteTruckDAO();

        // 1) Adding Zones
        ZoneDTO zone1 = zoneDAO.insert(new ZoneDTO(null, "center", new ArrayList<>()));
        ZoneDTO zone2 = zoneDAO.insert(new ZoneDTO(null, "east", new ArrayList<>()));
        ZoneDTO zone3 = zoneDAO.insert(new ZoneDTO(null, "north", new ArrayList<>()));

        // 2) Adding Sites for each Zone
        siteDAO.insert(new SiteDTO(null, "bareket 20 shoham", "liel", "0501111111", zone1.zoneId()));
        siteDAO.insert(new SiteDTO(null, "tel aviv", "alice", "0501234567", zone1.zoneId()));
        siteDAO.insert(new SiteDTO(null, "yafo 123, jerusalem", "avi", "0509999999", zone2.zoneId()));
        siteDAO.insert(new SiteDTO(null, "david King Hotel, the dead sea", "nadav", "0508888888", zone2.zoneId()));
        siteDAO.insert(new SiteDTO(null, "ben gurion university", "shlomi", "0502222222", zone3.zoneId()));
        siteDAO.insert(new SiteDTO(null, "mini market eilat", "dana", "0507777777", zone3.zoneId()));

        // 3) Adding Trucks
        truckDAO.insert(new TruckDTO(null, "small", "123", "BMW", 100F, 120F, true));
        truckDAO.insert(new TruckDTO(null, "large", "555", "BMW", 133F, 140F, true));
    }

    /**
     * Load some example items into the Transportation “items” table.
     */
    public void loadItems() throws SQLException {
        SqliteItemDAO itemDAO = new SqliteItemDAO();
        itemDAO.insert(new ItemDTO(null, "bamba",   0.5F));
        itemDAO.insert(new ItemDTO(null, "chicken", 2F));
        itemDAO.insert(new ItemDTO(null, "sugar",   1F));
    }

    /**
     * Load **full** HR data (roles, employees, shift templates, actual shifts).
     * Converts old java.util.Date for employmentDate into a LocalDate before setting.
     */
    public void initializeFullHRData() throws ParseException {
        // 1) Seed roles
        List<String> rolesToSeed = List.of(
                "HR",
                "Shift Manager",
                "Cashier",
                "Warehouse",
                "Cleaner",
                "Driver",
                "Transportation Manager"
        );
        for (String roleName : rolesToSeed) {
            roleService.addRole(new RoleDTO(roleName));
        }

        // 2) Fetch RoleDTOs (we will reuse these)
        RoleDTO hrRoleDto                = roleService.getRoleByName("HR");
        RoleDTO cashierRoleDto           = roleService.getRoleByName("Cashier");
        RoleDTO warehouseRoleDto         = roleService.getRoleByName("Warehouse");
        RoleDTO driverRoleDto            = roleService.getRoleByName("Driver");
        RoleDTO shiftMgrRoleDto          = roleService.getRoleByName("Shift Manager");
        RoleDTO transportationMgrRoleDto = roleService.getRoleByName("Transportation Manager");

        // 3) Create a LocalDate for hireDate (convert from old java.util.Date)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        java.util.Date parsedDate = sdf.parse("2020-01-01");
        LocalDate hireDate = parsedDate.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();

        //
        // 4a) Non-driver Employee #1 (ID = "1", roles = ["Shift Manager","Cashier","Transportation Manager"])
        //
        CreateEmployeeDTO create1 = new CreateEmployeeDTO();
        create1.setId("1");
        create1.setName("Dana");
        create1.setRoles(List.of(shiftMgrRoleDto, cashierRoleDto, transportationMgrRoleDto));
        create1.setRawPassword("123");
        create1.setBankAccount("IL123BANK");
        create1.setSalary(5000f);
        create1.setEmploymentDate(hireDate);         // <-- use LocalDate now
        create1.setAvailabilityThisWeek(null);
        create1.setAvailabilityNextWeek(null);
        create1.setHolidays(null);
        // No “Driver” role here → use single-arg overload
        employeeService.addEmployee(create1);

        //
        // 4b) Non-driver Employee #2 (ID = "2", roles = ["Warehouse","Cashier"])
        //
        CreateEmployeeDTO create2 = new CreateEmployeeDTO();
        create2.setId("2");
        create2.setName("John");
        create2.setRoles(List.of(warehouseRoleDto, cashierRoleDto));
        create2.setRawPassword("456");
        create2.setBankAccount("IL456BANK");
        create2.setSalary(4500f);
        create2.setEmploymentDate(hireDate);
        create2.setAvailabilityThisWeek(null);
        create2.setAvailabilityNextWeek(null);
        create2.setHolidays(null);
        // Still no “Driver” role
        employeeService.addEmployee(create2);

        //
        // 4c) Non-driver Employee #3 (ID = "hr", roles = ["HR"])
        //
        CreateEmployeeDTO createHR = new CreateEmployeeDTO();
        createHR.setId("hr");
        createHR.setName("HR Manager");
        createHR.setRoles(List.of(hrRoleDto));
        createHR.setRawPassword("123");
        createHR.setBankAccount("IL456BANK");
        createHR.setSalary(4500f);
        createHR.setEmploymentDate(hireDate);
        createHR.setAvailabilityThisWeek(null);
        createHR.setAvailabilityNextWeek(null);
        createHR.setHolidays(null);
        employeeService.addEmployee(createHR);

        //
        // 4d) Driver-only Employee #4 (ID = "driver1", roles = ["Driver"])
        //
        CreateEmployeeDTO createDriver1 = new CreateEmployeeDTO();
        createDriver1.setId("driver1");
        createDriver1.setName("Alex Driver");
        createDriver1.setRoles(List.of(driverRoleDto)); // has “Driver”
        createDriver1.setRawPassword("driverpass");
        createDriver1.setBankAccount("IL789BANK");
        createDriver1.setSalary(4700f);
        createDriver1.setEmploymentDate(hireDate);
        createDriver1.setAvailabilityThisWeek(null);
        createDriver1.setAvailabilityNextWeek(null);
        createDriver1.setHolidays(null);

        // Because “Driver” appears in roles, call the two-arg overload once:
        List<DriverInfo.LicenseType> licenses1 = List.of(DriverInfo.LicenseType.B);
        employeeService.addEmployee(createDriver1, licenses1);

        //
        // 4e) Driver + Warehouse Employee #5 (ID = "driver2", roles = ["Driver","Warehouse"])
        //
        CreateEmployeeDTO createDriver2 = new CreateEmployeeDTO();
        createDriver2.setId("driver2");
        createDriver2.setName("Sam Wheels");
        createDriver2.setRoles(List.of(driverRoleDto, warehouseRoleDto)); // has “Driver”
        createDriver2.setRawPassword("truckit");
        createDriver2.setBankAccount("IL998BANK");
        createDriver2.setSalary(4900f);
        createDriver2.setEmploymentDate(hireDate);
        createDriver2.setAvailabilityThisWeek(null);
        createDriver2.setAvailabilityNextWeek(null);
        createDriver2.setHolidays(null);

        // Because “Driver” appears in roles, call the two-arg overload once:
        List<DriverInfo.LicenseType> licenses2 = List.of(
                DriverInfo.LicenseType.C,
                DriverInfo.LicenseType.C1
        );
        employeeService.addEmployee(createDriver2, licenses2);

        //
        // 5) Define recurring shift templates (all days of week × Morning/Evening)
        //
        for (DayOfWeek dow : DayOfWeek.values()) {
            ShiftTemplateDTO morningTpl = new ShiftTemplateDTO();
            morningTpl.setDay(dow);
            morningTpl.setTime(Shift.ShiftTime.Morning);
            morningTpl.setDefaultCounts(Collections.emptyMap());
            shiftService.addTemplate(morningTpl);

            ShiftTemplateDTO eveningTpl = new ShiftTemplateDTO();
            eveningTpl.setDay(dow);
            eveningTpl.setTime(Shift.ShiftTime.Evening);
            eveningTpl.setDefaultCounts(Collections.emptyMap());
            shiftService.addTemplate(eveningTpl);
        }

        //
        // 6) Create one actual Morning and one Evening shift for each of the next 7 days
        //    (Using ShiftDAO since ShiftService has no “addShift”)
        //
        Connection conn = Database.getConnection();
        ShiftDAO shiftDAO = new ShiftDAOImpl(conn);

        LocalDate today = LocalDate.now();
        for (int offset = 0; offset < 7; offset++) {
            LocalDate dateLocal = today.plusDays(offset);
            java.sql.Date sqlDate = java.sql.Date.valueOf(dateLocal);

            // Morning shift
            String morningId = dateLocal + "-Morning";
            Map<Role, ArrayList<HR.Domain.Employee>> requiredRolesM = new HashMap<>();
            Map<Role, Integer> requiredCountsM = new HashMap<>();
            Shift morningShift = new Shift(
                    morningId,
                    sqlDate,
                    Shift.ShiftTime.Morning,
                    requiredRolesM,
                    requiredCountsM
            );
            shiftDAO.insert(morningShift);

            // Evening shift
            String eveningId = dateLocal + "-Evening";
            Map<Role, ArrayList<HR.Domain.Employee>> requiredRolesE = new HashMap<>();
            Map<Role, Integer> requiredCountsE = new HashMap<>();
            Shift eveningShift = new Shift(
                    eveningId,
                    sqlDate,
                    Shift.ShiftTime.Evening,
                    requiredRolesE,
                    requiredCountsE
            );
            shiftDAO.insert(eveningShift);
        }

        PresentationUtils.typewriterPrint(
                "Example data, templates, and actual shifts loaded successfully.",
                20
        );
    }

    /**
     * Load minimal ("Part HR") data: only HR role, plus shift templates and actual shifts,
     * and prompt for a single initial HR user.
     */
    public void initializePartHRData() {
        // 1) Add only the “HR” role
        roleService.addRole(new RoleDTO("HR"));

        // 2) Create shift templates for every day × Morning/Evening
        for (DayOfWeek dow : DayOfWeek.values()) {
            ShiftTemplateDTO morningTpl = new ShiftTemplateDTO();
            morningTpl.setDay(dow);
            morningTpl.setTime(Shift.ShiftTime.Morning);
            morningTpl.setDefaultCounts(Collections.emptyMap());
            shiftService.addTemplate(morningTpl);

            ShiftTemplateDTO eveningTpl = new ShiftTemplateDTO();
            eveningTpl.setDay(dow);
            eveningTpl.setTime(Shift.ShiftTime.Evening);
            eveningTpl.setDefaultCounts(Collections.emptyMap());
            shiftService.addTemplate(eveningTpl);
        }

        // 3) Create one actual Morning and one Evening shift for the next 7 days
        Connection conn = Database.getConnection();
        ShiftDAO shiftDAO = new ShiftDAOImpl(conn);

        LocalDate today = LocalDate.now();
        for (int offset = 0; offset < 7; offset++) {
            LocalDate dateLocal = today.plusDays(offset);
            java.sql.Date sqlDate = java.sql.Date.valueOf(dateLocal);

            // Morning shift
            String morningId = dateLocal + "-Morning";
            Map<Role, ArrayList<HR.Domain.Employee>> requiredRolesM = new HashMap<>();
            Map<Role, Integer> requiredCountsM = new HashMap<>();
            Shift morningShift = new Shift(
                    morningId,
                    sqlDate,
                    Shift.ShiftTime.Morning,
                    requiredRolesM,
                    requiredCountsM
            );
            shiftDAO.insert(morningShift);

            // Evening shift
            String eveningId = dateLocal + "-Evening";
            Map<Role, ArrayList<HR.Domain.Employee>> requiredRolesE = new HashMap<>();
            Map<Role, Integer> requiredCountsE = new HashMap<>();
            Shift eveningShift = new Shift(
                    eveningId,
                    sqlDate,
                    Shift.ShiftTime.Evening,
                    requiredRolesE,
                    requiredCountsE
            );
            shiftDAO.insert(eveningShift);
        }

        // 4) Prompt the console user for exactly one initial HR user
        Scanner scanner = new Scanner(System.in);
        String newId, newName, newPw, newBankAccount;
        Float newSalary;
        LocalDate newEmpDate;

        // Prompt for ID
        while (true) {
            PresentationUtils.typewriterPrint("Enter ID for initial HR user: ", 20);
            newId = scanner.nextLine().trim();
            if (newId.isEmpty()) {
                PresentationUtils.typewriterPrint("ID cannot be empty. Try again.", 20);
            } else break;
        }

        // Prompt for name
        while (true) {
            PresentationUtils.typewriterPrint("Enter name for initial HR user: ", 20);
            newName = scanner.nextLine().trim();
            if (newName.isEmpty()) {
                PresentationUtils.typewriterPrint("Name cannot be empty. Try again.", 20);
            } else break;
        }

        // Prompt for password
        while (true) {
            PresentationUtils.typewriterPrint("Enter password for initial HR user: ", 20);
            newPw = scanner.nextLine().trim();
            if (newPw.isEmpty()) {
                PresentationUtils.typewriterPrint("Password cannot be empty. Try again.", 20);
            } else break;
        }

        // Prompt for bank account
        while (true) {
            PresentationUtils.typewriterPrint("Enter bank account for initial HR user: ", 20);
            newBankAccount = scanner.nextLine().trim();
            if (newBankAccount.isEmpty()) {
                PresentationUtils.typewriterPrint("Bank account cannot be empty. Try again.", 20);
            } else break;
        }

        // Prompt for salary
        while (true) {
            PresentationUtils.typewriterPrint("Enter salary for initial HR user: ", 20);
            String salaryLine = scanner.nextLine().trim();
            try {
                newSalary = Float.valueOf(salaryLine);
                if (newSalary < 0) {
                    PresentationUtils.typewriterPrint("Salary must be non-negative. Try again.", 20);
                } else break;
            } catch (NumberFormatException e) {
                PresentationUtils.typewriterPrint("Invalid number. Please enter a valid salary.", 20);
            }
        }

        // Prompt for employment date (convert from String → LocalDate)
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setLenient(false);
        while (true) {
            PresentationUtils.typewriterPrint("Enter employment date (YYYY-MM-DD): ", 20);
            String dateLine = scanner.nextLine().trim();
            try {
                java.util.Date parsed = df.parse(dateLine);
                newEmpDate = parsed.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();
                break;
            } catch (ParseException e) {
                PresentationUtils.typewriterPrint("Invalid date format. Use YYYY-MM-DD. Try again.", 20);
            }
        }

        // Build CreateEmployeeDTO for the initial HR user
        CreateEmployeeDTO createHr0 = new CreateEmployeeDTO();
        createHr0.setId(newId);
        createHr0.setName(newName);
        createHr0.setRoles(List.of(roleService.getRoleByName("HR")));
        createHr0.setRawPassword(newPw);
        createHr0.setBankAccount(newBankAccount);
        createHr0.setSalary(newSalary);
        createHr0.setEmploymentDate(newEmpDate);
        createHr0.setAvailabilityThisWeek(null);
        createHr0.setAvailabilityNextWeek(null);
        createHr0.setHolidays(null);

        employeeService.addEmployee(createHr0);
        PresentationUtils.typewriterPrint("Initial HR user created.", 20);
    }
}
