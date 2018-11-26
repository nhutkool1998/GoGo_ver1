package cs486.nmnhut.gogo;

import android.content.Context;
import android.view.View;

public class mNotification {

    public static final int ACTIVITY_NOTIFICATION = 1;
    public static final int NEW_TRIP = 2;
    public static final int TRIP_INVITATION = 3;
    private String message;
    private int type;
    private String person;
    mNotification()
    {

    }

    mNotification(String message, String person, int type)
    {
        this.message = message;
        this.person = person;
        this.type =type;
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

