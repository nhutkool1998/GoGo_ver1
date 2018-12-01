package cs486.nmnhut.gogo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class TripListActivity extends AppCompatActivity {
    static boolean firstrun = false;
    final int NetworkPermission = 100;
    ListView TripListView;
    HashMap<String, MyTrip> myTrips;
    ArrayList<TripItem> l;
    int previous_count = 0;
    TripListAdapter tripListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);

        TripListView = findViewById(R.id.listViewTripList);
        firstrun = true;
        checkPermissions_and_Initialize();
    }

    private void checkPermissions_and_Initialize() {
        if (ContextCompat.checkSelfPermission(TripListActivity.this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            Toast t = Toast.makeText(this, "Permission Not granted", Toast.LENGTH_SHORT);
            t.show();
            requestPermissions(new String[]{Manifest.permission.INTERNET}, NetworkPermission);
        } else {
            Toast t = Toast.makeText(this, "Granted", Toast.LENGTH_SHORT);
            t.show();
            initialize();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NetworkPermission && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initialize();
        } else {
            setPermissionDeniedNotification();
        }


    }

    private void setPermissionDeniedNotification() {
    }

    private void initialize() {
        myTrips = new HashMap<>();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        final ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //get value
                myTrips.put(dataSnapshot.getKey(), dataSnapshot.getValue(MyTrip.class));
                refreshList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        DatabaseReference ref = db.getReference("user/" + DatabaseHelper.currentUserID());
        ref.child("trip").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot d : dataSnapshot.getChildren()) { //for each trip
                    final String TripID = d.getKey();

                    FirebaseDatabase db2 = FirebaseDatabase.getInstance();
                    DatabaseReference ref2 = db2.getReference("trip/");

                    if (!myTrips.containsKey(TripID)) //if new trip added
                        ref2.child(TripID).addValueEventListener(valueEventListener);
                }
                for (String key : myTrips.keySet()) {
                    FirebaseDatabase db2 = FirebaseDatabase.getInstance();
                    DatabaseReference ref2 = db2.getReference("trip/");
                    if (!dataSnapshot.hasChild(key)) { //if the trip is no longer available in database, remove
                        myTrips.remove(key);
                        ref2.child(key).removeEventListener(valueEventListener);
                    }
                }
                refreshList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void refreshList() {
        l = new ArrayList<>();
        for (String s : myTrips.keySet()) {
            MyTrip temp = myTrips.get(s);
            l.add(new TripItem(temp.tripDescription, s));
        }
        TripListView.setAdapter(new TripListAdapter(l, this));
        TripListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TripListActivity.this, trips.class);
                intent.putExtra("TripID", l.get(position).TripID);
                startActivity(intent);
            }
        });
    }

    class TripItem {
        String TripMessage;
        String TripID;


        public TripItem(String tripMessage, String tripID) {
            TripMessage = tripMessage;
            TripID = tripID;
        }
    }

    class TripListAdapter extends ArrayAdapter<TripItem> {
        ArrayList<TripItem> list;
        Context context;

        public TripListAdapter(ArrayList<TripItem> list, Context context) {
            super(TripListActivity.this, R.layout.list_trip_item, list);
            this.list = list;
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                convertView = layoutInflater.inflate(R.layout.list_trip_item, parent, false);
                viewHolder.txtMessage = convertView.findViewById(R.id.txtTripMessage);
                viewHolder.btnRemove = convertView.findViewById(R.id.btnRemoveTrip);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final TripItem temp = list.get(position);
            viewHolder.txtMessage.setText(temp.TripMessage);
            viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHelper.RemoveTrip(temp.TripID);
                }
            });
            return convertView;
        }

        private class ViewHolder {
            Button btnRemove;
            TextView txtMessage;
        }
    }
}
