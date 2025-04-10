package Domain;

import java.util.Date;

public class AvailabilityDL {
    private Date date;
    private ShiftDL.ShiftTime Type;


    public AvailabilityDL(Date date, ShiftDL.ShiftTime Type) {
        this.date = date;
        this.Type = Type;
    }


    public Date getDate() {
        return date;
    }

    public ShiftDL.ShiftTime getType() {
        return Type;
    }

}
