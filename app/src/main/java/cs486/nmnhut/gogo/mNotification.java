package cs486.nmnhut.gogo;

public class mNotification {

    public static final int ACTIVITY_NOTIFICATION = 4;
    public static final int NEW_TRIP = 3;
    public static final int TRIP_INVITATION = 0;
    public static final int TRIP_KICKED = 1;
    public static final int INVIATION_ACCEPTED = 5;
    public static final int INVITATIOND_DECLINE = 6;
    private String message;
    private int type;
    private String person;
    private String tripID;
    mNotification()
    {

    }

    mNotification(String message, String person, int type) {
        this.message = message;
        this.person = person;
        this.type =type;
        tripID = "0";
    }

    mNotification(String message, String person, int type, String TripID) {
        this.message = message;
        this.person = person;
        this.type = type;
        this.tripID = TripID;
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


    public String getTripID() {
        return tripID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass())
            return false;
        mNotification n = (mNotification) obj;
        if (!n.message.equals(this.message))
            return false;
        if (!n.person.equals(this.person))
            return false;
        return n.type == this.type;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

