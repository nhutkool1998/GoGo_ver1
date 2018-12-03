package cs486.nmnhut.gogo;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DatabaseHelper {
    static FirebaseDatabase db;
    static int count = 0;

    DatabaseHelper() {
        if (count == 0)
            db = FirebaseDatabase.getInstance();
    }

    public static void AcceptInvitation(mNotification invitation, String UserID) {
        //TODO: implement acitivity invitation
    }

    public static void DeclineInvitation(mNotification invitation, String userID) {
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

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("trip");
        DatabaseReference r = ref.push();
        r.setValue(myTrip);

        DatabaseReference ref2 = db.getReference("user/" + currentUserID() + "/trip");
        ref2.child(r.getKey()).setValue(true);
    }

    public static void turnNotificationAt(String time, boolean isOn) {

    }
    public static void RemoveMember(final String TripID, String MemberName) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("user");
        ref.orderByChild("Name").startAt(MemberName).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference ref = db.getReference("trip/" + TripID + "member");
                    ref.child(dataSnapshot.getKey()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void AddMember(final String TripID, String MemberName) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("user");
        ref.orderByChild("Name").startAt(MemberName).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference ref = db.getReference("trip/" + TripID + "/member");
                    ref.child(dataSnapshot.getKey()).setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void InviteMember(final String message, final String Inviter, final String TripID, final String HostID, String name) {
        if (currentUserID().equals(HostID)) {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref = db.getReference("user");
            ref.orderByKey().startAt("Name", name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference ref = db.getReference("notification/" + dataSnapshot.getKey());
                        mNotification notif = new mNotification(message, Inviter, mNotification.TRIP_INVITATION, TripID);
                        ref.push().setValue(notif);
                    } else
                        showFailToInviteMessage();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showFailToInviteMessage();
                }
            });
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


}
