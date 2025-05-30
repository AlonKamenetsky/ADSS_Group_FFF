package SuppliersModule.DomainLayer;

import SuppliersModule.DataLayer.SupplierDaysDTO;
import SuppliersModule.DomainLayer.Enums.*;
import SuppliersModule.DataLayer.SupplierControllerDTO;
import SuppliersModule.DataLayer.SupplierDTO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.Map;

import static SuppliersModule.DomainLayer.OrderController.buildProductDataArray;

public class SupplierController {
    OrderController orderController;
    SupplyContractController supplyContractController;

    int numberOfSuppliers;
    ArrayList<Supplier> suppliersArrayList;

    SupplierControllerDTO supplierControllerDTO;

    public SupplierController() throws SQLException, IOException {
        this.numberOfSuppliers = 0;
        this.suppliersArrayList = new ArrayList<>();

        this.orderController = new OrderController();
        this.supplyContractController = new SupplyContractController();

        this.supplierControllerDTO = SupplierControllerDTO.getInstance();
        ArrayList<SupplierDTO> dtoList = supplierControllerDTO.getAllSuppliers();

        for (SupplierDTO dto : dtoList) {
            ContactInfo supplierContactInfo = new ContactInfo(dto.phoneNumber, dto.address, dto.emailAddress, dto.contactName);
            PaymentInfo supplierPaymentInfo = new PaymentInfo(dto.bankAccount, PaymentMethod.valueOf(dto.paymentMethod));

            Supplier supplier = null;
            if (SupplyMethod.valueOf(dto.supplyMethod) == SupplyMethod.ON_DEMAND)
                supplier = new OnDemandSupplier(dto.supplierID, dto.supplierName, ProductCategory.valueOf(dto.productCategory), DeliveringMethod.valueOf(dto.deliveryMethod), supplierContactInfo, supplierPaymentInfo);
            else if (SupplyMethod.valueOf(dto.supplyMethod) == SupplyMethod.SCHEDULED) {
                ArrayList<SupplierDaysDTO> supplierDaysDTOList = supplierControllerDTO.getSupplierDaysOfSupplier(dto);

                EnumSet<WeekDay> days = EnumSet.noneOf(WeekDay.class);
                ArrayList<ScheduledOrder> scheduledOrders = new ArrayList<>();
                for (SupplierDaysDTO supplierDaysDTO : supplierDaysDTOList) {
                    days.add(WeekDay.valueOf(supplierDaysDTO.day));
                    scheduledOrders.add(new ScheduledOrder(dto.supplierID, WeekDay.valueOf(supplierDaysDTO.day)));
                }

                supplier = new ScheduledSupplier(dto.supplierID, dto.supplierName, ProductCategory.valueOf(dto.productCategory), DeliveringMethod.valueOf(dto.deliveryMethod), supplierContactInfo, supplierPaymentInfo, days);
                for (ScheduledOrder scheduledOrder : scheduledOrders) {
                    ScheduledSupplier scheduledSupplier = (ScheduledSupplier)supplier;
                    scheduledSupplier.addScheduledOrder(scheduledOrder.getDay(), scheduledOrder);
                }
            }

            for (SupplyContract supplyContract : this.supplyContractController.getAllSupplierContracts(dto.supplierID))
                supplier.addSupplierContract(supplyContract);

            this.numberOfSuppliers++;
            this.suppliersArrayList.add(supplier);
        }
    }

    // --------------------------- SUPPLIER FUNCTIONS ---------------------------

    public int registerNewSupplier(SupplyMethod supplyMethod, String supplierName, ProductCategory productCategory, DeliveringMethod deliveringMethod,
                                    String phoneNumber, String address, String email, String contactName,
                                    String bankAccount, PaymentMethod paymentMethod, EnumSet<WeekDay> supplyDays) {
        ContactInfo supplierContactInfo = new ContactInfo(phoneNumber, address, email, contactName);
        PaymentInfo supplierPaymentInfo = new PaymentInfo(bankAccount, paymentMethod);

        Supplier supplier = null;
        if (supplyMethod == SupplyMethod.ON_DEMAND)
            supplier = new OnDemandSupplier(this.numberOfSuppliers++, supplierName, productCategory, deliveringMethod, supplierContactInfo, supplierPaymentInfo);
        else if (supplyMethod == SupplyMethod.SCHEDULED)
            supplier = new ScheduledSupplier(this.numberOfSuppliers++, supplierName, productCategory, deliveringMethod, supplierContactInfo, supplierPaymentInfo, supplyDays);

        this.suppliersArrayList.add(supplier);
        supplier.supplierDTO.Insert();

        if (supplyMethod == SupplyMethod.SCHEDULED) {
            ScheduledSupplier scheduledSupplier = (ScheduledSupplier)supplier;
            for (Map.Entry<WeekDay, ScheduledOrder> entry: scheduledSupplier.getScheduledOrders().entrySet()) {
                WeekDay day = entry.getKey();
                ScheduledOrder scheduledOrder = entry.getValue();
                for (OrderProductData orderProductData : scheduledOrder.getProductsData()) {
                    SupplierDaysDTO dto = new SupplierDaysDTO(scheduledSupplier.getSupplierId(), day.toString(), orderProductData.getProductID(), orderProductData.getProductQuantity(), orderProductData.getProductQuantity());
                    dto.Insert();
                }
            }
        }

        return supplier.getSupplierId();
    }

    private Supplier getSupplierBySupplierID(int supplierID) {
        for (Supplier supplier : this.suppliersArrayList)
            if (supplier.supplierId == supplierID)
                return supplier;

        return null;
    }

    private Supplier getSupplierByProductsPrice(ArrayList<int[]> dataList) {
        Supplier cheapestSupplier = null;
        double cheapestPrice = 0;
        for (Supplier supplier : this.suppliersArrayList) {
            if (supplier.getSupplyMethod() != SupplyMethod.SCHEDULED)
                continue;
            ArrayList<SupplyContract> supplyContracts = supplier.getSupplierContracts();
            ArrayList<OrderProductData> orderProductData = buildProductDataArray(dataList, supplyContracts);
            if (supplyContracts == null || orderProductData == null)
                continue;

            double sum = 0;
            for (OrderProductData productData : orderProductData)
                sum += productData.getTotalPrice();

            if (sum < cheapestPrice) {
                cheapestSupplier = supplier;
                cheapestPrice = sum;
            }
        }

        return cheapestSupplier;
    }

    public boolean deleteSupplier(int supplierID) {
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null)
            return false;

        supplier.supplierDTO.Delete();
        this.supplyContractController.removeAllSupplierContracts(supplierID);
        this.orderController.removeAllSupplierOrders(supplierID);

        return this.suppliersArrayList.removeIf(s -> s.supplierId == supplierID);
    }

    // ********** UPDATE FUNCTIONS **********

    public boolean updateSupplierName(int supplierID, String supplierName) {
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null)
            return false;

        supplier.setSupplierName(supplierName);
        return true;
    }

    public boolean updateSupplierDeliveringMethod(int supplierID, DeliveringMethod deliveringMethod) {
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null)
            return false;

        supplier.setSupplierDeliveringMethod(deliveringMethod);
        return true;
    }

    public boolean updateSupplierContactInfo(int supplierID, String phoneNumber, String address, String email, String contactName) {
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null)
            return false;

        ContactInfo contactInfo = new ContactInfo(phoneNumber, address, email, contactName);
        supplier.setSupplierContactInfo(contactInfo);
        return true;
    }

    public boolean updateSupplierPaymentInfo(int supplierID, String bankAccount, PaymentMethod paymentMethod) {
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null)
            return false;

        PaymentInfo paymentInfo = new PaymentInfo(bankAccount, paymentMethod);
        supplier.setSupplierPaymentInfo(paymentInfo);
        return true;
    }

    // ********** GETTERS FUNCTIONS **********

    public ProductCategory getSupplierProductCategory(int supplierID) {
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier != null)
            return supplier.getSupplierProductCategory();
        return null;
    }

    public DeliveringMethod getSupplierDeliveringMethod(int supplierID) {
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier != null)
            return supplier.getSupplierDeliveringMethod();

        return null;
    }

    public ContactInfo getSupplierContactInfo(int supplierID){
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier != null)
            return supplier.getSupplierContactInfo();

        return null;
    }

    public SupplyMethod getSupplierSupplyMethod(int supplierID) {
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier != null)
            return supplier.getSupplyMethod();

        return null;
    }

    // ********** OUTPUT FUNCTIONS **********

    public String[] getAllSuppliersAsString() {
        String[] suppliersAsString = new String[this.suppliersArrayList.size()];
        for (int i = 0 ; i < suppliersAsString.length;i++)
            suppliersAsString[i] = this.suppliersArrayList.get(i).toString();

        return suppliersAsString;
    }

    public String getSupplierAsString(int supplierID) {
        Supplier supplier = this.getSupplierBySupplierID(supplierID);
        if (supplier != null)
            return supplier.toString();
        return null;
    }

    // --------------------------- CONTRACT FUNCTIONS ---------------------------

    public boolean registerNewContract(int supplierID, ArrayList<int[]> dataList) {
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null)
            return false;

        SupplyMethod supplyMethod = supplier.getSupplyMethod();

        SupplyContract contract = supplyContractController.registerNewContract(supplierID, dataList, supplyMethod);

        supplier.addSupplierContract(contract);

        return true;
    }

    // ********** GETTERS FUNCTIONS **********

    private ArrayList<SupplyContract> getAvailableContractsForOrder(int orderID) {
        int supplierID = this.orderController.getOrderSupplierID(orderID);
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null)
            return null;

        return supplier.getSupplierContracts();
    }

    // ********** OUTPUT FUNCTIONS **********

    public String getContractToString(int contractID) {
        return this.supplyContractController.getContractToString(contractID);
    }

    public String[] getAvailableContractsForOrderAsString(int orderID) {
        ArrayList<SupplyContract> supplyContractArrayList = this.getAvailableContractsForOrder(orderID);

        String[] result = new String[supplyContractArrayList.size()];
        for (int i = 0; i < supplyContractArrayList.size(); i++)
            result[i] = supplyContractArrayList.get(i).toString();

        return result;
    }

    public String[] GetSupplierContractsAsString(int supplierID) {
        Supplier supplier = getSupplierBySupplierID(supplierID);
        if (supplier == null)
            return null;

        ArrayList<SupplyContract> supplyContractArrayList = supplier.getSupplierContracts();

        String[] result = new String[supplyContractArrayList.size()];
        for (int i = 0; i < supplyContractArrayList.size(); i++)
            result[i] = supplyContractArrayList.get(i).toString();

        return result;
    }

    public String[] getAllContractToStrings(){
        return this.supplyContractController.getAllContractToStrings();
    }

    // --------------------------- ORDER FUNCTIONS ---------------------------

    public boolean registerNewOrder(ArrayList<int[]> dataList, Date creationDate, Date deliveryDate) {
        Supplier supplier = this.getSupplierByProductsPrice(dataList);
        if (supplier == null)
            return false;

        int supplierId = supplier.getSupplierId();

        DeliveringMethod deliveringMethod = this.getSupplierDeliveringMethod(supplierId);
        ContactInfo contactInfo = this.getSupplierContactInfo(supplierId);
        SupplyMethod supplyMethod = this.getSupplierSupplyMethod(supplierId);
        ArrayList<SupplyContract> supplyContracts = supplier.getSupplierContracts();

        return this.orderController.registerNewOrder(supplierId, dataList, supplyContracts, creationDate, deliveryDate, deliveringMethod, supplyMethod, contactInfo);
    }

    public boolean registerNewScheduledOrder(WeekDay day, ArrayList<int[]> dataList) {
        Supplier supplier = this.getSupplierByProductsPrice(dataList);
        if (supplier == null)
            return false;

        ArrayList<SupplyContract> supplyContracts = supplier.getSupplierContracts();
        ArrayList<OrderProductData> orderProductData = buildProductDataArray(dataList, supplyContracts);

        ScheduledOrder scheduledOrder = new ScheduledOrder(supplier.supplierId, day, orderProductData);

        ScheduledSupplier scheduledSupplier = ((ScheduledSupplier)supplier);
        scheduledSupplier.addScheduledOrder(day, scheduledOrder);

        return true;
    }

    public boolean deleteOrder(int orderID) {
        return this.orderController.deleteOrder(orderID);
    }

    public boolean orderExists(int orderID) {
        return this.orderController.orderExists(orderID);
    }

    // ********** UPDATE FUNCTIONS **********

    public boolean updateOrderContactInfo(int orderId, String phoneNumber, String address, String email, String contactName){
        return this.orderController.updateOrderContactInfo(orderId, phoneNumber, address, email, contactName);
    }

    public boolean updateOrderSupplyDate(int orderID, Date supplyDate){
        return this.orderController.updateOrderSupplyDate(orderID, supplyDate);
    }

    public boolean updateOrderStatus(int orderID, OrderStatus orderStatus) {
        return this.orderController.updateOrderStatus(orderID, orderStatus);
    }

    public boolean addProductsToOrder(int orderID, ArrayList<int[]> dataList) {
        ArrayList<SupplyContract> supplyContracts = getAvailableContractsForOrder(orderID);
        return this.orderController.addProductsToOrder(orderID,supplyContracts, dataList);
    }

    public boolean removeProductsFromOrder(int orderID, ArrayList<Integer> dataList) {
        return this.orderController.removeProductsFromOrder(orderID, dataList);
    }

    // ********** GETTERS FUNCTIONS **********

    public Date getOrderSupplyDate(int orderID){
        return this.orderController.getOrderSupplyDate(orderID);
    }

    // ********** OUTPUT FUNCTIONS **********

    public String getOrderAsString(int orderID) {
        return this.orderController.getOrderAsString(orderID);
    }

    public String[] getAllOrdersAsString() {
        return this.orderController.getAllOrdersAsString();
    }

}
