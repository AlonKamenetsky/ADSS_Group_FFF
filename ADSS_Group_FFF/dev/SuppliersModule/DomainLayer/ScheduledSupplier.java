package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;
import SuppliersModule.DomainLayer.Enums.WeekDay;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.EnumSet;

public class ScheduledSupplier extends Supplier {
    private EnumSet<WeekDay> supplyDays;

    public ScheduledSupplier(int supplierId, String supplierName, ProductCategory productCategory, DeliveringMethod supplierDeliveringMethod, ContactInfo supplierContactInfo, PaymentInfo supplierPaymentInfo) {
        super(supplierId, supplierName, productCategory, supplierDeliveringMethod, supplierContactInfo, supplierPaymentInfo);
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

    @Override
    public String toString() {
        return super.toString() + "\nAvailable days: " + this.supplyDays;
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
