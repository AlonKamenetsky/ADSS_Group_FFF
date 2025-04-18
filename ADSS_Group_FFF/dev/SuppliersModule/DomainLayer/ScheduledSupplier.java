package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.WeekDay;

import java.util.EnumSet;

public class ScheduledSupplier extends Supplier {
    private EnumSet<WeekDay> supplyDays;
}
