package cs486.nmnhut.gogo;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

class MyTrip {
    String tripDescription;
    String hostID;
    String hostName;
    ArrayList<TripMember> members;
    TripPlan plan;

    MyTrip() {

    }

    MyTrip(String TripID) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("trip/" + TripID);
    }

    public String getTripDescription() {
        return tripDescription;
    }

    public String getHostID() {
        return hostID;
    }

    public String getHostName() {
        return hostName;
    }

    public ArrayList<TripMember> getMembers() {
        return members;
    }

    public TripPlan getPlan() {
        return plan;
    }


}
