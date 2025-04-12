package Domain;

import java.util.Date;

public class Availability {
    private Date date;
    private Shift.ShiftTime Type;


    public Availability(Date date, Shift.ShiftTime Type) {
        this.date = date;
        this.Type = Type;
    }


    public Date getDate() {
        return date;
    }

    public Shift.ShiftTime getType() {
        return Type;
    }

}
