package inventory.serviceLayer;

import IntegrationInventoryAndSupplier.*;
import inventory.dataLayer.sqlite.SQLiteCategoryDAO;
import inventory.dataLayer.sqlite.SQLiteDiscountDAO;
import inventory.dataLayer.sqlite.SQLiteInventoryProductDAO;
import inventory.dataLayer.sqlite.SQLiteInventoryReportDAO;
import inventory.domainLayer.*;
import inventory.dataLayer.daos.*;

import java.sql.SQLException;
import java.util.*;

public class InventoryService implements InternalInventoryInterface {

    private final InventoryProductDAO productDAO;
    private final CategoryDAO categoryDAO;
    private final DiscountDAO discountDAO;
    private final InventoryReportDAO reportDAO;


    private static InventoryService instance = null;
    private SupplierInterface supplierInterface;


    // ─────────────────────────────────────────────────────────────────
    // 1) Private no-arg constructor: Instantiates default SQLite DAOs
    // ─────────────────────────────────────────────────────────────────
    private InventoryService() {
        try {
            // 1a) Build Category DAO (creates tables if needed)
            SQLiteCategoryDAO catDao = new SQLiteCategoryDAO();

            // 1b) Build InventoryProduct DAO, passing Category DAO
            SQLiteInventoryProductDAO prodDao = new SQLiteInventoryProductDAO(catDao);

            // 1c) Build Discount DAO, passing Category DAO and Product DAO
            SQLiteDiscountDAO discDao = new SQLiteDiscountDAO(catDao, prodDao);

            // 1d) Build InventoryReport DAO, passing Product DAO
            SQLiteInventoryReportDAO repDao = new SQLiteInventoryReportDAO(prodDao);

            // 1e) Assign to final fields
            this.categoryDAO = catDao;
            this.productDAO = prodDao;
            this.discountDAO = discDao;
            this.reportDAO = repDao;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize default SQLite DAOs", e);
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // 2) Private constructor: Accepts explicit DAO implementations
    // ─────────────────────────────────────────────────────────────────
    private InventoryService(InventoryProductDAO productDAO,
                             CategoryDAO categoryDAO,
                             DiscountDAO discountDAO,
                             InventoryReportDAO reportDAO) {
        this.productDAO = productDAO;
        this.categoryDAO = categoryDAO;
        this.discountDAO = discountDAO;
        this.reportDAO = reportDAO;
    }

    // ─────────────────────────────────────────────────────────────────
    // 3) Public getInstance(): Returns the singleton, instantiating via no-arg constructor if needed
    // ─────────────────────────────────────────────────────────────────
    public static InventoryService getInstance()
    {
        if (instance == null) {
            instance = new InventoryService();
        }
        return instance;
    }

    // ─────────────────────────────────────────────────────────────────
    // 4) Public configure method: Inject custom DAO implementations before any getInstance()
    // ─────────────────────────────────────────────────────────────────
    public static void configureWithDaos(InventoryProductDAO productDAO,
                                         CategoryDAO categoryDAO,
                                         DiscountDAO discountDAO,
                                         InventoryReportDAO reportDAO) {
        if (instance != null) {
            throw new IllegalStateException("InventoryService has already been initialized");
        }
        instance = new InventoryService(productDAO, categoryDAO, discountDAO, reportDAO);
    }

    // ─────────────────────────────────────────────────────────────────
    // 5) Expose public‐facing interface if desired
    // ─────────────────────────────────────────────────────────────────
    public static InventoryInterface getInterface() {
        return getInstance();
    }

    public static InternalInventoryInterface getInternalInterfaceInstance()
    {
        return getInstance();
    }

    // for Stav and Blanga internal use
    public static InternalInventoryInterface getInternalInstance() {
        if (instance == null)
            instance = new InventoryService();
        return instance;
    }

    // ─────────────────────────────────────────────────────────────────
    // CATEGORY‐RELATED METHODS
    // ─────────────────────────────────────────────────────────────────

    /**
     * Create a brand‐new Category and persist it.
     * If parentCategoryName is non‐null but not found, this will throw an exception.
     */
    public void addCategory(String name, String parentCategoryName) {
        Category parent = null;
        if (parentCategoryName != null) {
            parent = categoryDAO.findByName(parentCategoryName);
            if (parent == null) {
                throw new IllegalArgumentException("Parent category '" + parentCategoryName + "' does not exist.");
            }
        }
        Category newCategory = new Category(name, parent);
        categoryDAO.save(newCategory);
    }

    /**
     * Retrieve one category by its name.
     */
    public Category getCategory(String name) {
        return categoryDAO.findByName(name);
    }

    /**
     * Retrieve all categories.
     */
    public List<Category> getAllCategories()
    {
        return categoryDAO.findAll();
    }

    /**
     * Delete a category by name.
     */
    public void deleteCategory(String name) {
        categoryDAO.delete(name);
    }

    /**
     * Update an existing category’s data (for instance, change its parent or name).
     */
    public void updateCategory(Category updated) {
        categoryDAO.update(updated);
    }


    // ─────────────────────────────────────────────────────────────────
    // INVENTORY PRODUCT‐RELATED METHODS
    // ─────────────────────────────────────────────────────────────────

    /**
     * Add a new product (including category lookup).
     */
    public void addProduct(int id, String name, String manufacturer,
                           int shelfQty, int backroomQty, int minThreshold,
                           double purchasePrice, double salePrice,
                           ItemStatus status, String categoryName) {

        // 1) Fetch the Category domain object (or null if categoryName is null)
        Category category = null;
        if (categoryName != null) {
            category = categoryDAO.findByName(categoryName);
            if (category == null) { //if category doesnt exist it is added to db
                Category c1 = new Category(categoryName,null);
                categoryDAO.save(c1);
            }
        }

        InventoryProduct product = new InventoryProduct(
                id,
                name,
                manufacturer,
                shelfQty,
                backroomQty,
                minThreshold,
                purchasePrice,
                salePrice,
                status,
                category
        );
        productDAO.save(product);
        checkAndReorderLowStockItems();
    }

    /**
     * Get one product by its ID.
     */
    public InventoryProduct getProductById(int id) {
        return productDAO.findById(id);
    }

    /**
     * Get ALL products (in no particular order).
     */
    public List<InventoryProduct> getAllProducts() {
        return productDAO.findAll();
    }

    /**
     * Delete one product by ID.
     */
    public void deleteProduct(int id) {
        productDAO.delete(id);
    }

    /**
     * Update a product’s basic fields (name, manufacturer, prices, category, etc.)
     */
    public void updateProduct(InventoryProduct updated) {
        // NOTE: you may want to re‐check that the category still exists, etc.
        if (updated.getCategory() != null) {
            Category cat = categoryDAO.findByName(updated.getCategory().getName());
            if (cat == null) {
                throw new IllegalArgumentException("Cannot update product: category '"
                        + updated.getCategory().getName() + "' not found.");
            }
        }
        productDAO.update(updated);
    }



    // ─────────────────────────────────────────────────────────────────
    // DISCOUNT‐RELATED METHODS
    // ─────────────────────────────────────────────────────────────────

    /**
     * Create a new Discount that applies either to a category or to a specific item.
     */
    public void addDiscount(String discountId,
                            double percent,
                            Date startDate,
                            Date endDate,
                            String appliesToCategoryName,
                            Integer appliesToProductId) {

        Category cat = null;
        InventoryProduct prod = null;

        if (appliesToCategoryName != null) {
            cat = categoryDAO.findByName(appliesToCategoryName);
            if (cat == null) {
                throw new IllegalArgumentException("Category '" + appliesToCategoryName + "' not found.");
            }
        }

        if (appliesToProductId != null) {
            prod = productDAO.findById(appliesToProductId);
            if (prod == null) {
                throw new IllegalArgumentException("Product with ID=" + appliesToProductId + " not found.");
            }
        }

        Discount d = new Discount(discountId, percent, startDate, endDate, cat, prod);
        discountDAO.save(d);
    }

    /**
     * Get a discount by its ID.
     */
    public Discount getDiscountById(String discountId) {
        return discountDAO.findById(discountId);
    }

    /**
     * Get all discounts (both category‐level and product‐level).
     */
    public List<Discount> getAllDiscounts() {
        return discountDAO.findAll();
    }

    /**
     * Delete a discount by its ID.
     */
    public void deleteDiscount(String discountId) {
        discountDAO.delete(discountId);
    }

    /**
     * Update an existing Discount (e.g. time window or percent).
     */
    public void updateDiscount(Discount updated) {
        // Optional: re‐validate that its category and/or item still exist:
        if (updated.getAppliesToCategory() != null) {
            Category c = categoryDAO.findByName(updated.getAppliesToCategory().getName());
            if (c == null) {
                throw new IllegalArgumentException("Cannot update discount: category '"
                        + updated.getAppliesToCategory().getName() + "' not found.");
            }
        }
        if (updated.getAppliesToItem() != null) {
            InventoryProduct ip = productDAO.findById(updated.getAppliesToItem().getId());
            if (ip == null) {
                throw new IllegalArgumentException("Cannot update discount: product ID="
                        + updated.getAppliesToItem().getId() + " not found.");
            }
        }
        discountDAO.update(updated);
    }

    // ─────────────────────────────────────────────────────────────────
    // INVENTORY REPORT–RELATED METHODS
    // ─────────────────────────────────────────────────────────────────

    /**
     * Generate a report for all products whose shelfQuantity + backroomQuantity is below minThreshold.
     * Persist the report and return it.
     */
    public InventoryReport generateLowStockReport() {
        // 1) Fetch all products
        List<InventoryProduct> allProducts = productDAO.findAll();

        // 2) Filter “low stock” items
        List<InventoryProduct> lowStockProducts = allProducts.stream()
                .filter(p -> (p.getShelfQuantity() + p.getBackroomQuantity()) < p.getMinThreshold())
                .toList();

        // 3) Create a new InventoryReport
        String reportId = UUID.randomUUID().toString();
        InventoryReport report = new InventoryReport(reportId, new Date(), lowStockProducts);

        // 4) Persist the report
        reportDAO.save(report);
        return report;
    }

    /**
     * Retrieve a previously generated report by ID.
     */
    public InventoryReport getReportById(String reportId) {
        return reportDAO.findById(reportId);
    }

    /**
     * Retrieve all stored reports.
     */
    public List<InventoryReport> getAllReports() {
        return reportDAO.findAll();
    }

    /**
     * Delete a stored report.
     */
    public void deleteReport(String reportId) {
        reportDAO.delete(reportId);
    }

    public void updateReport(InventoryReport updatedReport) {
        reportDAO.update(updatedReport);
    }

    // ─────────────────────────────────────────────────────────────────
    // “ACCEPT DELIVERY” / “REORDER” LOGIC FROM SUPPLIER INTERFACE
    // ─────────────────────────────────────────────────────────────────

    @Override
    public boolean acceptDelivery(int itemId, int quantity) {
        // Called by SupplierIntegration when a delivery has arrived.
        // We simply add 'quantity' to backroom for that product.
        InventoryProduct p = productDAO.findById(itemId);
        if (p == null) {
            return false; // no such product
        }
        p.setBackroomQuantity(p.getBackroomQuantity() + quantity);
        productDAO.update(p);
        return true;
    }

    /**
     * Periodically check all products; if any fall below their minThreshold,
     * place an urgent order via SupplierInterface.
     */
    public void checkAndReorderLowStockItems() {
        if (supplierInterface == null) {
            System.err.println("SupplierService not set.");
            return;
        }
        List<InventoryProduct> allProducts = productDAO.findAll();
        for (InventoryProduct product : allProducts) {
            int totalQty = product.getShelfQuantity() + product.getBackroomQuantity();
            if (totalQty < product.getMinThreshold()) {
                supplierInterface.placeUrgentOrderSingleProduct(product.getId(), lowStockOrderLogic(product));
            }
        }
    }

    private int lowStockOrderLogic(InventoryProduct p)
    {return p.getMinThreshold()*3;}

    public void setSupplierService(SupplierInterface supplierInterface) {
        this.supplierInterface = supplierInterface;
    }


    public List<InventoryProduct> getLowStockItems() {
        // 1) Ask DAO for all products
        List<InventoryProduct> all = productDAO.findAll();

        // 2) Filter out the “low stock” ones
        return all.stream()
                .filter(p -> (p.getShelfQuantity() + p.getBackroomQuantity()) < p.getMinThreshold())
                .toList();
    }

    /**
     * Return the single applicable Discount (product-level first,
     * then category-level up the hierarchy), or null if none exist.
     */
    public Discount getDiscountForItem(int productId) {
        // 1) Check product-specific discount
        Discount itemDisc = discountDAO.findByItemId(productId);
        if (itemDisc != null) {
            return itemDisc;
        }

        // 2) Otherwise, walk up the product’s category chain
        InventoryProduct product = productDAO.findById(productId);
        if (product == null) {
            return null; // no such product
        }
        Category cat = product.getCategory();
        while (cat != null) {
            Discount catDisc = discountDAO.findByCategoryName(cat.getName());
            if (catDisc != null) {
                return catDisc;
            }
            cat = cat.getParentCategory();
        }

        return null; // no discount found
    }


    /**
     * Generate a one-off report that includes all products whose
     * category is in filterCategories (if non-null) AND whose status is statusFilter (if non-null).
     * Persist the report and return it.
     */
    public InventoryReport generateReport(List<Category> filterCategories,
                                          ItemStatus statusFilter) {
        // 1) Fetch every product from the DAO
        List<InventoryProduct> allProducts = productDAO.findAll();

        // 2) Filter in Java according to the passed criteria
        List<InventoryProduct> filtered = allProducts.stream()
                .filter(p -> {
                    boolean matchesCat = (filterCategories == null
                            || filterCategories.isEmpty()
                            || filterCategories.contains(p.getCategory())
                    );
                    boolean matchesStatus = (statusFilter == null
                            || p.getStatus() == statusFilter
                    );
                    return matchesCat && matchesStatus;
                })
                .toList();

        // 3) Create a new InventoryReport domain object
        InventoryReport report = new InventoryReport(getNextReportId(), new Date(), filtered);

        // 4) Persist the report via the DAO
        reportDAO.save(report);

        // 5) Return it
        return report;
    }

    /**
     * Returns the next numeric report‐ID as a String, based on what's already in the DB.
     * Any non‐numeric IDs are skipped. If there are no existing reports, returns "1".
     */
    private String getNextReportId() {
        List<InventoryReport> existing = reportDAO.findAll();
        int max = 0;
        for (InventoryReport r : existing) {
            try {
                int n = Integer.parseInt(r.getId());
                if (n > max) {
                    max = n;
                }
            } catch (NumberFormatException e) {
                // ignore any IDs that are not pure integers
            }
        }
        return String.valueOf(max + 1);
    }




    /**
     * Update an existing product’s shelf/backroom quantities by adding shelfDelta/backroomDelta.
     * If either delta is negative, re-check for low-stock to automatically reorder.
     */
    public void updateItemQuantity(int productId, int shelfDelta, int backroomDelta) {
        // 1) Fetch the product via DAO
        InventoryProduct product = productDAO.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product with ID=" + productId + " not found.");
        }

        // 2) Apply the deltas
        int newShelf = product.getShelfQuantity() + shelfDelta;
        int newBackroom = product.getBackroomQuantity() + backroomDelta;
        product.setShelfQuantity(newShelf);
        product.setBackroomQuantity(newBackroom);

        // 3) Persist the updated quantities
        productDAO.update(product);
        checkAndReorderLowStockItems();
    }

    /**
     * Return the list of all products available from the supplier module.
     * If no SupplierInterface has been set, returns an empty list.
     */
    public List<MutualProduct> getAllAvailableProducts() {
        if (supplierInterface == null) {
            return Collections.emptyList();
        }
        return supplierInterface.getAllAvailableProduct();
    }
}


