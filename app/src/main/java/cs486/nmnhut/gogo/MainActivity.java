package cs486.nmnhut.gogo;

import android.Manifest;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final int NetworkPermission = 100;
    final int LocationPermission = 101;

    ArrayList<mNotification> mNotificationArrayList;

    RecyclerView notificationList;
    FirebaseDatabase db;

    String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setUIVariables();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mNotificationArrayList = new ArrayList<>();

       setSampleData();

       checkPermissions_and_Initialize();

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
            Toast t = Toast.makeText(this,"Permission Not granted",Toast.LENGTH_SHORT);
            t.show();
            requestPermissions(new String[] {Manifest.permission.INTERNET},NetworkPermission);
        }
        else
        {
            Toast t = Toast.makeText(this,"Granted",Toast.LENGTH_SHORT);
            t.show();
            initialize();
        }
    }

    private void setUIVariables() {
        notificationList = findViewById(R.id.listNotification);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NetworkPermission && grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            initialize();
        }
        else
        {
            setPermissionDeniedNotification();
        }


    }

    private void setPermissionDeniedNotification() {

    }

    private void initialize() {

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mNotification newNotification = dataSnapshot.getValue(mNotification.class);
                mNotificationArrayList.add(newNotification);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                GenericTypeIndicator<ArrayList<mNotification>> t = new GenericTypeIndicator<ArrayList<mNotification>>() {};
                mNotificationArrayList = dataSnapshot.getValue(t);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        db = FirebaseDatabase.getInstance();
        UID = this.getIntent().getStringExtra("UID");
        DatabaseReference ref = db.getReference("notif/"+UID);

        ref.addChildEventListener(childEventListener);

        NotificationAdapter notificationAdapter = new NotificationAdapter(mNotificationArrayList);
        notificationList.setLayoutManager(new LinearLayoutManager(this));

        notificationList.setAdapter(notificationAdapter);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_trip) {

        } else if (id == R.id.nav_logout) {



        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void LaunchCurrentTrip()
    {
        //TODO: implement currentrip
    }

    public static void ShowNewTripScreen()
    {
        //TODO: implement show new trip screen
    }

    public static void AcceptInvitation(mNotification invitation)
    {
        //TODO: implement acitivity invitation
    }

    public static void ShowChatBox()
    {
        //TODO: implement show chat box;
    }
}
