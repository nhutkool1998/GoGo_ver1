package cs486.nmnhut.gogo;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

class MyTrip {
    String tripDescription;
    String hostID;
    String hostName;
    HashMap<String, TripMember> members;
    TripPlan plan;

    MyTrip() {
        members = new HashMap<>();
        plan = new TripPlan();
    }

    MyTrip(String TripID) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("trip/" + TripID);
        members = new HashMap<>();
        plan = new TripPlan();
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

    public HashMap<String, TripMember> getMembers() {
        return members;
    }

    public TripPlan getPlan() {
        return plan;
    }


}
