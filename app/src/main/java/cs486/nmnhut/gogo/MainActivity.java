package cs486.nmnhut.gogo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static cs486.nmnhut.gogo.GeolocationHelper.MY_PERMISSIONS_REQUEST_FINE_LOCATION;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static ArrayList<String> mNotificationIDs = null;
    final int NetworkPermission = 100;
    final int LocationPermission = 101;
    LinearLayoutManager linearLayoutManager = null;
    final mNotification notification_viewCurrentTrips = new mNotification("GoGo", "The worst author", mNotification.ACTIVITY_NOTIFICATION);
    final mNotification notification_createNewTrips = new mNotification("GoGo", "The worst author", mNotification.NEW_TRIP);
    private final ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mNotification newNotification = dataSnapshot.getValue(mNotification.class);
            //   mNotificationArrayList.add(newNotification);
            notificationAdapter.add(newNotification);
            if (mNotificationIDs == null)
                mNotificationIDs = new ArrayList<>();
            mNotificationIDs.add(dataSnapshot.getKey());

        }
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mNotification newNotification = dataSnapshot.getValue(mNotification.class);
            int position = mNotificationIDs.indexOf(dataSnapshot.getKey());
            mNotificationIDs.set(position, dataSnapshot.getKey());
            notificationAdapter.list.set(position, newNotification);
            notificationAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            try {
                mNotification newNotification = dataSnapshot.getValue(mNotification.class);
                int position = mNotificationIDs.indexOf(dataSnapshot.getKey());
                mNotificationArrayList.remove(position);
                mNotificationIDs.remove(position);
                notificationAdapter.remove(position);
            } catch (Exception ex) {
                DoNothing();
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void DoNothing() {
    }

    static ArrayList<mNotification> mNotificationArrayList = null;
    NotificationAdapter notificationAdapter = null;
    GeolocationHelper geolocationHelper;
    RecyclerView notificationList;
    FirebaseDatabase db;
    long notificationCount = 0;
    String UID;//   mNotificationArrayList.add(newNotification);
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (savedInstanceState != null)
            return;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // DatabaseHelper.SampleTrip();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setUIVariables();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // mNotificationArrayList = new ArrayList<>();

        //setSampleData();


    }


    @Override
    protected void onStart() {
        super.onStart();
        checkPermissions_and_Initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //notificationList.setLayoutManager(linearLayoutManager);
        //notificationList.setAdapter(notificationAdapter);
    }

    void setGoGoNotification() {
        db = FirebaseDatabase.getInstance();
        String UID = DatabaseHelper.currentUserID();
        DatabaseReference ref = db.getReference("notif/" + UID);

        ref.child("newNotif").setValue(notification_createNewTrips);
        ref.child("currentNotif").setValue(notification_viewCurrentTrips);
    }


    private void setSampleData() {
        mNotification a = new mNotification("dd","ddd",mNotification.NEW_TRIP);
        mNotification b = new mNotification("Đi Đà Lạt hông?","Gấu",mNotification.TRIP_INVITATION);
        mNotification c = new mNotification("Đang ở Sài Gòn?","Gấu",mNotification.ACTIVITY_NOTIFICATION);
        mNotificationArrayList.add(a);
        mNotificationArrayList.add(b);
        mNotificationArrayList.add(c);
    }


    private void checkPermissions_and_Initialize() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            Toast t = Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT);
            t.show();
            requestPermissions(new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION}, NetworkPermission);
        }
        else
        {

            initialize();
        }
    }

    private void setUIVariables() {
        notificationList = findViewById(R.id.listNotification);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean ok = true;
        if (grantResults.length == 0)
        {
            setPermissionDeniedNotification();
            return;
        }

        if (requestCode == NetworkPermission) {
            for (int i = 0; i < grantResults.length; ++i) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    ok = false;
            }
        }
        if (!ok) {
            setPermissionDeniedNotification();
            return;
        }

        initialize();


    }

    private void setPermissionDeniedNotification() {
        //TODO: set permission denined
    }


    private void initialize() {
        setGoGoNotification();



        if (linearLayoutManager == null)
            linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        if (mNotificationArrayList == null)
            mNotificationArrayList = new ArrayList<>();
        if (notificationAdapter == null)
            notificationAdapter = new NotificationAdapter(mNotificationArrayList);
        if (mNotificationIDs == null)
            mNotificationIDs = new ArrayList<>();


        notificationList.setLayoutManager(linearLayoutManager);
        notificationList.setAdapter(notificationAdapter);
        // notificationAdapter.notifyDataSetChanged();
        geolocationHelper = new GeolocationHelper(this, DatabaseHelper.currentUserID());
        geolocationHelper.requestLocationUpdate();
        db = FirebaseDatabase.getInstance();
        UID = this.getIntent().getStringExtra("UID");
        DatabaseReference ref = db.getReference("notif/"+UID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // notificationCount = dataSnapshot.getChildrenCount();
                populateNotificationList(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (mNotificationIDs.isEmpty())
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    populateNotificationList(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    private void populateNotificationList(@NonNull DataSnapshot dataSnapshot) {
        mNotificationArrayList.clear();
        mNotificationIDs.clear();
        if (dataSnapshot.exists()) {
            for (DataSnapshot d : dataSnapshot.getChildren()) {
                mNotificationArrayList.add(d.getValue(mNotification.class));
                mNotificationIDs.add(d.getKey());
            }
        }
        notificationAdapter.update(mNotificationArrayList);
    }

    private void writeSampleData() {
        DatabaseReference ref = db.getReference("notif/" + UID);
        ref.setValue(mNotificationArrayList);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        TextView m = findViewById(R.id.txtUserEmail_main);
        m.setText(DatabaseHelper.getUserEmail());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(this, Camera.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, Gallery.class);
            startActivity(intent);

        } else if (id == R.id.nav_trip) {
            LaunchCurrentTrips();

        } else if (id == R.id.nav_logout) {
            geolocationHelper.stopLocationUpdates();
            LogOut();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void LogOut() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, Login.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        this.finish();
    }

    public void LaunchCurrentTrips()
    {
        //TODO: implement currentrip

        Intent detailsIntent = new Intent(MainActivity.this, TripListActivity.class);

        MainActivity.this.startActivity(detailsIntent);

    }

    public void ShowNewTripScreen()
    {
        currentUserID = DatabaseHelper.currentUserID();
        MyTrip myTrip = new MyTrip();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("trip");
        String key = ref.push().getKey();

        myTrip.hostID = currentUserID;

        myTrip.tripDescription = "Trip Description";
        TripMember t = new TripMember();
        t.alarm = false;
        t.position = new ToaDo(GeolocationHelper.getCurrentPosition());
        t.name = DatabaseHelper.getUserEmail();

        myTrip.members.put(currentUserID, t);

        DatabaseReference ref2 = db.getReference("user/" + currentUserID + "/trip");
        ref2.child(key).setValue(true);
        ref.child(key).setValue(myTrip);
        Intent intent = new Intent(this, TripDescriptionActivity.class);
        //  TripID = bundle.getString("TripID");
        // HostID = bundle.getString("HostID");
        intent.putExtra("TripID", key);
        intent.putExtra("HostID", currentUserID);
        startActivity(intent);
    }


    public void ShowChatBox()
    {
        //TODO: implement show chat box;
    }

    public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<mNotification> list;

        public NotificationAdapter(ArrayList<mNotification> items) {
            this.list = items;
        }

        @Override
        public int getItemCount() {
            return this.list.size();
        }

        @Override
        public int getItemViewType(int position) {
            return list.get(position).getType();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            RecyclerView.ViewHolder viewHolder;
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

            switch (viewType) {
                case mNotification.ACTIVITY_NOTIFICATION:
                    View v1 = inflater.inflate(R.layout.list_activity_notification, viewGroup, false);
                    viewHolder = new ViewHolder_current_trip(v1);
                    break;

                case mNotification.TRIP_INVITATION:
                    View v = inflater.inflate(R.layout.list_trip_invite, viewGroup, false);
                    viewHolder = new ViewHolder_invitation(v);
                    break;

                default:
                    View v2 = inflater.inflate(R.layout.list_new_trip, viewGroup, false);
                    viewHolder = new ViewHolder_new_trip(v2);
                    break;
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            switch (viewHolder.getItemViewType()) {
                case mNotification.ACTIVITY_NOTIFICATION:
                    ViewHolder_current_trip vh1 = (ViewHolder_current_trip) viewHolder;
                    configure_current_trip(vh1, position);
                    break;
                case mNotification.NEW_TRIP:
                    ViewHolder_new_trip vh2 = (ViewHolder_new_trip) viewHolder;
                    configure_new_trip(vh2, position);
                    break;

                case mNotification.TRIP_KICKED:
                    ViewHolder_new_trip vh5 = (ViewHolder_new_trip) viewHolder;
                    configure_trip_kicked(vh5, position);
                    break;
                case mNotification.TRIP_INVITATION:
                    ViewHolder_invitation vh3 = (ViewHolder_invitation) viewHolder;
                    configure_invitation(vh3, position);
                    break;
            }
        }

        private void configure_invitation(ViewHolder_invitation vh3, final int position) {
            TextView txtInviter = vh3.getTxtInviter();
            txtInviter.setText(list.get(position).getPerson());

            TextView txtInvitation = vh3.getTxtInvitation();
            txtInvitation.setText(list.get(position).getMessage());

            Button btnAccept = vh3.getBtnAccept();
            final String notificationID = mNotificationIDs.get(position);
            final mNotification notif = list.get(position);
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHelper.AcceptInvitation(notif, notificationID);
                    Toast t = Toast.makeText(MainActivity.this, "Accepted", Toast.LENGTH_SHORT);
                    t.show();
                }
            });

            Button btnDecline = vh3.getBtnDelcine();
            btnDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNotification n = list.get(position);
                    DatabaseHelper.DeclineInvitation(list.get(position), mNotificationIDs.get(position));
                }
            });
            btnAccept.setFocusable(false);
            btnDecline.setFocusable(false);
        }

        private void configure_new_trip(ViewHolder_new_trip vh2, int position) {
            Button btnNewTrip = vh2.getBtnNewTrip();

            btnNewTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowNewTripScreen();
                }
            });
            btnNewTrip.setFocusable(false);

        }

        private void configure_trip_kicked(ViewHolder_new_trip vh2, int position) {
            Button btnNewTrip = vh2.getBtnNewTrip();
            TextView txt = vh2.getTxtMessage();
            String m = "You are removed from a trip by " + list.get(position).getPerson();
            txt.setText(m);
            btnNewTrip.setVisibility(View.INVISIBLE);
        }

        private void configure_current_trip(ViewHolder_current_trip vh1, int position) {

            Button btnCurrentTrip = vh1.getBtnCurrentTrip();

            btnCurrentTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LaunchCurrentTrips();
                }
            });
            btnCurrentTrip.setFocusable(false);


        }

        void add(mNotification notification) {
            list.add(0, notification);
            notifyItemInserted(0);
            notifyItemRangeChanged(0, getItemCount());

        }

        void update(ArrayList<mNotification> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        void remove(int index) {
            list.remove(index);
            notifyDataSetChanged();
        }
    }
}
