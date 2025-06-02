package SuppliersModule.DomainLayer;

import SuppliersModule.DataLayer.ScheduledOrderDataDTO;
import SuppliersModule.DataLayer.SupplierDaysDTO;
import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;
import SuppliersModule.DomainLayer.Enums.WeekDay;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class ScheduledSupplier extends Supplier {
    private EnumSet<WeekDay> supplyDays;

    private HashMap<WeekDay,ScheduledOrder> scheduledOrders;

    public ArrayList<SupplierDaysDTO> supplierDaysDTOS;

    public ScheduledSupplier(int supplierId, String supplierName, ProductCategory productCategory, DeliveringMethod supplierDeliveringMethod, ContactInfo supplierContactInfo, PaymentInfo supplierPaymentInfo, EnumSet<WeekDay> supplyDays) {
        super(supplierId, supplierName, productCategory, supplierDeliveringMethod, supplierContactInfo, supplierPaymentInfo);
        this.supplierDTO.supplyMethod = this.getSupplyMethod().toString();

        this.supplyDays = supplyDays;

        this.scheduledOrders = new HashMap<>();

        this.supplierDaysDTOS = new ArrayList<>();

        for (WeekDay day : supplyDays)
            this.supplierDaysDTOS.add(new SupplierDaysDTO(supplierId, day.toString()));
    }

    @Override
    public SupplyMethod getSupplyMethod() {
        return SupplyMethod.SCHEDULED;
    }

    public EnumSet<WeekDay> getSupplyDays() {
        return this.supplyDays;
    }

    public void setSupplyDays(EnumSet<WeekDay> supplyDays) {
        this.supplyDays = supplyDays;
    }

    public HashMap<WeekDay,ScheduledOrder> getScheduledOrders() {
        return this.scheduledOrders;
    }

    public void setScheduledOrders(HashMap<WeekDay,ScheduledOrder> scheduledOrders) {
        this.scheduledOrders = scheduledOrders;
    }

    public void addScheduledOrder(WeekDay day, ScheduledOrder scheduledOrder) {
        if (!this.scheduledOrders.containsKey(day))
            this.scheduledOrders.put(day,scheduledOrder);
        else
            this.scheduledOrders.get(day).addProductsData(scheduledOrder.getProductsData());
    }

    @Override
    public String toString() {
        return super.toString() + "\nAvailable days: " + this.supplyDays + "\nScheduled orders: " + this.scheduledOrders;
    }

    public static Date getNearestWeekdayDate(WeekDay targetDay) {
        LocalDate today = LocalDate.now();

        DayOfWeek targetDayOfWeek;
        int ordinal = targetDay.ordinal();

        if (ordinal == 0) {
            targetDayOfWeek = DayOfWeek.SUNDAY;
        } else {
            targetDayOfWeek = DayOfWeek.of(ordinal);
        }

        if (today.getDayOfWeek() == targetDayOfWeek) {
            return convertToDate(today);
        }

        LocalDate nextOccurrence = today.with(TemporalAdjusters.next(targetDayOfWeek));

        LocalDate previousOccurrence = today.with(TemporalAdjusters.previous(targetDayOfWeek));

        long daysUntilNext = java.time.temporal.ChronoUnit.DAYS.between(today, nextOccurrence);

        long daysSincePrevious = java.time.temporal.ChronoUnit.DAYS.between(previousOccurrence, today);

        LocalDate result = (daysUntilNext <= daysSincePrevious) ? nextOccurrence : previousOccurrence;
        return convertToDate(result);
    }

    /**
     * Helper method to convert LocalDate to java.util.Date
     */
    private static Date convertToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
