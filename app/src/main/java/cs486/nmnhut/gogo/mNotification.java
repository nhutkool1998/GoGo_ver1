package cs486.nmnhut.gogo;

import android.content.Context;
import android.view.View;

public class mNotification {

    public static final int ACTIVITY_NOTIFICATION = 4;
    public static final int NEW_TRIP = 3;
    public static final int TRIP_INVITATION = 0;
    public static final int TRIP_KICKED = 1;
    private String message;
    private int type;
    private String person;
    private String TripID;
    mNotification()
    {

    }

    mNotification(String message, String person, int type) {
        this.message = message;
        this.person = person;
        this.type =type;
        TripID = "0";
    }

    mNotification(String message, String person, int type, String TripID) {
        this.message = message;
        this.person = person;
        this.type = type;
        this.TripID = TripID;
    }


    public int getType()
    {
        return type;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message =  message;
    }

    public String getPerson() {
        return person;
    }


}

