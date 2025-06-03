package Util;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import HR.DTO.CreateEmployeeDTO;
import HR.DTO.RoleDTO;
import HR.DTO.ShiftTemplateDTO;
import HR.DataAccess.ShiftDAO;
import HR.DataAccess.ShiftDAOImpl;
import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Presentation.PresentationUtils;
import HR.Service.EmployeeService;
import HR.Service.RoleService;
import HR.Service.ShiftService;
import Transportation.DataAccess.*;
import Transportation.DTO.*;

public class DatabaseInitializer {
    public  void loadTransportationFakeData() throws SQLException {
        SqliteSiteDAO siteDAO = new SqliteSiteDAO();
        SqliteZoneDAO zoneDAO = new SqliteZoneDAO();
        SqliteTransportationTaskDAO taskDAO = new SqliteTransportationTaskDAO();
        SqliteTruckDAO truckDAO = new SqliteTruckDAO();

        // Adding Zones
        ZoneDTO zone1 = zoneDAO.insert(new ZoneDTO(null, "center", new ArrayList<>()));
        ZoneDTO zone2 = zoneDAO.insert(new ZoneDTO(null, "east", new ArrayList<>()));
        ZoneDTO zone3 = zoneDAO.insert(new ZoneDTO(null, "north", new ArrayList<>()));

        //  Adding Sites for Zone 1 (Center)
        SiteDTO site1 = siteDAO.insert(new SiteDTO(null, "bareket 20 shoham", "liel", "0501111111", zone1.zoneId()));
        SiteDTO site2 = siteDAO.insert(new SiteDTO(null, "tel aviv", "Aaice", "0501234567", zone1.zoneId()));

        //  Adding Sites for Zone 2 (East)
        SiteDTO site3 = siteDAO.insert(new SiteDTO(null, "yafo 123, jerusalem", "avi", "0509999999", zone2.zoneId()));
        SiteDTO site4 = siteDAO.insert(new SiteDTO(null, "david King Hotel, the dead sea", "nadav", "0508888888", zone2.zoneId()));

        //  Adding Sites for Zone 3 (North)
        SiteDTO site5 = siteDAO.insert(new SiteDTO(null, "ben gurion university", "lidor", "0502222222", zone3.zoneId()));
        SiteDTO site6 = siteDAO.insert(new SiteDTO(null, "mini market eilat", "dana", "0507777777", zone3.zoneId()));

        //Adding Trucks
        TruckDTO truck1 = truckDAO.insert(new TruckDTO(null,"small","123","BMW",100F,120F,true));
        TruckDTO truck2 = truckDAO.insert(new TruckDTO(null,"large","555","BMW",133F,140F,true));

    }

    public void loadItems() throws SQLException {
        SqliteItemDAO itemDAO = new SqliteItemDAO();
        //Adding Items
        ItemDTO item1 = itemDAO.insert(new ItemDTO(null,"bamba",0.5F));
        ItemDTO item2 = itemDAO.insert(new ItemDTO(null,"chicken",2F));
        ItemDTO item3 = itemDAO.insert(new ItemDTO(null,"sugar",1F));
    }

    public void InitializeFullHRData() throws ParseException {
        RoleService roleService  = RoleService.getInstance();
        EmployeeService employeeService = EmployeeService.getInstance();
        ShiftService shiftService       = ShiftService.getInstance();
        // 1) Seed roles
        Arrays.asList("HR", "Shift Manager", "Cashier", "Warehouse", "Cleaner", "Driver","Transportation Manager")
                .forEach(roleName -> {
                    RoleDTO dto = new RoleDTO(roleName);
                    roleService.addRole(dto);
                });

        // 2) Fetch RoleDTOs
        RoleDTO hrRoleDto        = roleService.getRoleByName("HR");
        RoleDTO cashierRoleDto   = roleService.getRoleByName("Cashier");
        RoleDTO warehouseRoleDto = roleService.getRoleByName("Warehouse");
        RoleDTO driverRoleDto    = roleService.getRoleByName("Driver");
        RoleDTO shiftMgrRoleDto  = roleService.getRoleByName("Shift Manager");
        RoleDTO transportationManagerRoleDto = roleService.getRoleByName("Transportation Manager");

        // 3) Create hireDate
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setLenient(false);
        Date hireDate = df.parse("2020-01-01");

        // 4a) Build and insert Employee #1 (ID = "1", roles = ["Shift Manager","Cashier"])
        CreateEmployeeDTO create1 = new CreateEmployeeDTO();
        create1.setId("1");
        create1.setName("Dana");
        create1.setRoles(Arrays.asList(shiftMgrRoleDto, cashierRoleDto,transportationManagerRoleDto));
        create1.setRawPassword("123");
        create1.setBankAccount("IL123BANK");
        create1.setSalary(5000f);
        create1.setEmploymentDate(hireDate);
        create1.setAvailabilityThisWeek(null);
        create1.setAvailabilityNextWeek(null);
        create1.setHolidays(null);
        employeeService.addEmployee(create1);

        // 4b) Build and insert Employee #2 (ID = "2", roles = ["Warehouse","Cashier"])
        CreateEmployeeDTO create2 = new CreateEmployeeDTO();
        create2.setId("2");
        create2.setName("John");
        create2.setRoles(Arrays.asList(warehouseRoleDto, cashierRoleDto));
        create2.setRawPassword("456");
        create2.setBankAccount("IL456BANK");
        create2.setSalary(4500f);
        create2.setEmploymentDate(hireDate);
        create2.setAvailabilityThisWeek(null);
        create2.setAvailabilityNextWeek(null);
        create2.setHolidays(null);
        employeeService.addEmployee(create2);

        // 4c) Build and insert Employee #3 (ID = "hr", roles = ["HR"])
        CreateEmployeeDTO createHR = new CreateEmployeeDTO();
        createHR.setId("hr");
        createHR.setName("HR Manager");
        createHR.setRoles(Collections.singletonList(hrRoleDto));
        createHR.setRawPassword("123");
        createHR.setBankAccount("IL456BANK");
        createHR.setSalary(4500f);
        createHR.setEmploymentDate(hireDate);
        createHR.setAvailabilityThisWeek(null);
        createHR.setAvailabilityNextWeek(null);
        createHR.setHolidays(null);
        employeeService.addEmployee(createHR);

        // 5) Define recurring‐shift templates
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

        // 6) Create one actual Morning and one Evening shift for each of the next 7 days
        //    (We bypass ShiftService since it has no add‐shift method; use ShiftDAO directly.)
        var conn     = Database.getConnection();
        ShiftDAO shiftDAO = new ShiftDAOImpl(conn);

        LocalDate today = LocalDate.now();
        for (int offset = 0; offset < 7; offset++) {
            LocalDate dateLocal = today.plusDays(offset);
            java.sql.Date sqlDate = java.sql.Date.valueOf(dateLocal);

            // Morning shift
            String morningId = dateLocal.toString() + "-Morning";
            Map<Role, ArrayList<HR.Domain.Employee>> requiredRolesM   = new HashMap<>();
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
            String eveningId = dateLocal.toString() + "-Evening";
            Map<Role, ArrayList<HR.Domain.Employee>> requiredRolesE   = new HashMap<>();
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
        );}



    public void InitializePartHRData(){
        RoleService roleService  = RoleService.getInstance();
        EmployeeService employeeService = EmployeeService.getInstance();
        ShiftService shiftService       = ShiftService.getInstance();
        // Minimal “zero” seed: only add HR role, then prompt for initial HR user
        roleService.addRole(new RoleDTO("HR"));

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
        // 6) Create one actual Morning and one Evening shift for each of the next 7 days
        //    (We bypass ShiftService since it has no add‐shift method; use ShiftDAO directly.)
        var conn     = Database.getConnection();
        ShiftDAO shiftDAO = new ShiftDAOImpl(conn);

        LocalDate today = LocalDate.now();
        for (int offset = 0; offset < 7; offset++) {
            LocalDate dateLocal = today.plusDays(offset);
            java.sql.Date sqlDate = java.sql.Date.valueOf(dateLocal);

            // Morning shift
            String morningId = dateLocal.toString() + "-Morning";
            Map<Role, ArrayList<HR.Domain.Employee>> requiredRolesM   = new HashMap<>();
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
            String eveningId = dateLocal.toString() + "-Evening";
            Map<Role, ArrayList<HR.Domain.Employee>> requiredRolesE   = new HashMap<>();
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

        Scanner scanner = new Scanner(System.in);
        String newId, newName, newPw, newBankAccount;
        Float newSalary;
        Date newEmpDate;

        while (true) {
            PresentationUtils.typewriterPrint("Enter ID for initial HR user: ", 20);
            newId = scanner.nextLine().trim();
            if (newId.isEmpty()) {
                PresentationUtils.typewriterPrint("ID cannot be empty. Try again.", 20);
            } else break;
        }
        while (true) {
            PresentationUtils.typewriterPrint("Enter name for initial HR user: ", 20);
            newName = scanner.nextLine().trim();
            if (newName.isEmpty()) {
                PresentationUtils.typewriterPrint("Name cannot be empty. Try again.", 20);
            } else break;
        }
        while (true) {
            PresentationUtils.typewriterPrint("Enter password for initial HR user: ", 20);
            newPw = scanner.nextLine().trim();
            if (newPw.isEmpty()) {
                PresentationUtils.typewriterPrint("Password cannot be empty. Try again.", 20);
            } else break;
        }
        while (true) {
            PresentationUtils.typewriterPrint("Enter bank account for initial HR user: ", 20);
            newBankAccount = scanner.nextLine().trim();
            if (newBankAccount.isEmpty()) {
                PresentationUtils.typewriterPrint("Bank account cannot be empty. Try again.", 20);
            } else break;
        }
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
        SimpleDateFormat df_ = new SimpleDateFormat("yyyy-MM-dd");
        df_.setLenient(false);
        while (true) {
            PresentationUtils.typewriterPrint("Enter employment date (YYYY-MM-DD): ", 20);
            String dateLine = scanner.nextLine().trim();
            try {
                newEmpDate = df_.parse(dateLine);
                break;
            } catch (ParseException e) {
                PresentationUtils.typewriterPrint("Invalid date format. Use YYYY-MM-DD. Try again.", 20);
            }
        }

        // Build CreateEmployeeDTO for the initial HR user
        CreateEmployeeDTO createHr0 = new CreateEmployeeDTO();
        createHr0.setId(newId);
        createHr0.setName(newName);
        createHr0.setRoles(Collections.singletonList(roleService.getRoleByName("HR")));
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