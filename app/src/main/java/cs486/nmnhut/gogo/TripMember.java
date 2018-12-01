package cs486.nmnhut.gogo;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

class TripMember {
    String ID;
    String Name;
    boolean Alarm;
    ToaDo position;

    TripMember() {

    }

    TripMember(String ID) {
        this.ID = ID;
        position = new ToaDo();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("position/" + ID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                position = dataSnapshot.getValue(ToaDo.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
        position = new ToaDo();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("position/" + ID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                position = dataSnapshot.getValue(ToaDo.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String getName() {
        return Name;
    }

    public boolean isAlarm() {
        return Alarm;
    }

    private class ToaDo {
        float Lat;
        float Lng;

        ToaDo() {

        }

        public float getLat() {
            return Lat;
        }

        public void setLat(float lat) {
            Lat = lat;
        }

        public float getLng() {
            return Lng;
        }

        public void setLng(float lng) {
            Lng = lng;
        }
    }
}
