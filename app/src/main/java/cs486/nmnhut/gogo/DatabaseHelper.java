package cs486.nmnhut.gogo;

import android.app.Notification;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper {
    static FirebaseDatabase db;
    static int count = 0;
    static int finishedUserList = 0;
    static HashMap<String, String> res = new HashMap<>();

    DatabaseHelper() {
        if (count == 0)
            db = FirebaseDatabase.getInstance();
        count++;
    }

    public static void AcceptInvitation(mNotification invitation, String notificationID) {
        //TODO: implement acitivity invitation
        AddMember(invitation.getTripID(), FirebaseAuth.getInstance().getCurrentUser().getEmail());

    }

    public static void DeclineInvitation(mNotification invitation, String notificationID) {
        //TODO: implement acitivity invitation
    }

    public static void SampleTrip() {
        TripPlan tripPlan = new TripPlan();
        tripPlan.startDate = "2017/11/31";
        tripPlan.endDate = "2018/11/31";

        ArrayList<TripActivity> tripActivities = new ArrayList<>();
        TripActivity tripActivity = new TripActivity();
        tripActivity.startDate = tripPlan.getStartDate();
        tripActivity.endDate = tripPlan.endDate;
        tripActivity.place = "HCMC";

        MyTrip myTrip = new MyTrip();
        myTrip.hostID = currentUserID();
        myTrip.tripDescription = "tripDescription";
        myTrip.hostName = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        tripActivities.add(tripActivity);
        tripPlan.activities = tripActivities;
        myTrip.plan = tripPlan;
        HashMap<String, TripMember> m = new HashMap<>();
        int i = 0;

        for (String k : res.keySet()) {
            TripMember tripMember = new TripMember();

            tripMember.position = new TripMember.ToaDo();
            tripMember.setId(k);
            tripMember.position.setLat(80 + i);
            tripMember.position.setLng(80 + i);
            tripMember.name = res.get(k);
            ++i;
            m.put(k, tripMember);
        }
        myTrip.members = m;
        DatabaseReference ref = db.getReference("trip");
        DatabaseReference r = ref.push();
        r.setValue(myTrip);

        DatabaseReference ref2 = db.getReference("user/" + currentUserID() + "/trip");
        ref2.child(r.getKey()).setValue(true);

        DatabaseReference ref3 = db.getReference();
        ref3.child("position").child(currentUserID()).child("lat").setValue("100");
        ref3.child("position").child(currentUserID()).child("lng").setValue("100");

    }


    public static void RemoveMember(final String TripID, String MemberName) {
        String UID = res.get(MemberName);
        if (UID != null) {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref = db.getReference("trip/" + TripID + "members");
            ref.child(UID).removeValue();
        }
    }

    public static void AddMember(final String TripID, String MemberName) {
        String UID = res.get(MemberName);
        if (UID != null) {
            DatabaseReference ref = db.getReference("trip/" + TripID + "/members");
            TripMember t = new TripMember();
            t.name = MemberName;
            ref.child(UID).setValue(t);
        }
    }

    public static void InviteMember(final String message, final String Inviter, final String TripID, final String HostID, String name) {
        if (currentUserID().equals(HostID)) {
            DatabaseReference ref = db.getReference("notif");
            mNotification notification = new mNotification(message, Inviter, mNotification.TRIP_INVITATION, TripID);
            ref.child(currentUserID()).push().setValue(notification);
        }
    }

    private static void showFailToInviteMessage() {
    }

    public static void RemoveTrip(String TripID) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("trip");
        try {
            ref.child(TripID).removeValue();
        } catch (Exception ex) {

        }
    }

    public static String currentUserID() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser().getUid();
    }

    public static void getUserMap() {
        res.put(currentUserID(), "nmnhut@apcs.vn");
        if (finishedUserList == 1)
            return;
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("userlist");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    res = (HashMap<String, String>) dataSnapshot.getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        finishedUserList = 1;
    }


}
