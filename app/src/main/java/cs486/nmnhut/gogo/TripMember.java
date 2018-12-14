package cs486.nmnhut.gogo;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TripMember {
    String name;
    boolean alarm;
    ToaDo position;

    TripMember() {
        name = "";
        alarm = false;
        position = new ToaDo();
    }

    TripMember(String ID) {
        position = new ToaDo();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("position/" + ID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    position = dataSnapshot.getValue(ToaDo.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void setId(String id) {

        position = new ToaDo();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("position/" + id);
        /*ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                position = dataSnapshot.getValue(ToaDo.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

    public String getName() {
        return name;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public static class ToaDo {
        float lat;
        float lng;

        ToaDo() {
            lat = 0;
            lng = 0;
        }

        public float getLat() {
            return lat;
        }

        public void setLat(float lat) {
            this.lat = lat;
        }

        public float getLng() {
            return lng;
        }

        public void setLng(float lng) {
            this.lng = lng;
        }

        public double Distance(ToaDo t) {
            return Math.sqrt((t.lat - this.lat) * (t.lat - this.lat) + (t.lng - this.lng) * (t.lng - this.lng));
        }
    }
}
