package cs486.nmnhut.gogo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static cs486.nmnhut.gogo.DatabaseHelper.currentUserID;
import static cs486.nmnhut.gogo.DatabaseHelper.turnNotificationAt;

public class trips extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public static String TripID;
    private static String HostID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            TripID = bundle.getString("TripID");
            HostID = bundle.getString("HostID");
        }

        setContentView(R.layout.activity_trips);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.


        mViewPager = findViewById(R.id.container);

        mViewPager.setAdapter(mSectionsPagerAdapter);


        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trips, menu);
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

    public static class FragmentPlan extends Fragment {
        RecyclerView listViewActivity;
        ArrayList<PlanItem> planItems;
        ArrayList<TripActivity> activities;
        FragmentPlanListAdapter adapter;
        LinearLayoutManager linearLayoutManager;

        Button btnSaveChange, btnNewTripActivity;

        public FragmentPlan() {

        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_plan, container, false);

            btnNewTripActivity = v.findViewById(R.id.btnNewTripActivity);
            btnSaveChange = v.findViewById(R.id.btnSaveChanges);

            onBtnSaveChangeClick();
            btnSaveChange.setVisibility(View.INVISIBLE);
            onButtonNewActivityClick();

            listViewActivity = v.findViewById(R.id.listViewActivity);
            activities = new ArrayList<>();
            linearLayoutManager = new LinearLayoutManager(container.getContext());
            listViewActivity.setLayoutManager(linearLayoutManager);
            adapter = new FragmentPlanListAdapter(activities);
            listViewActivity.setAdapter(adapter);

            populateActivityList();
            return v;
        }

        private void populateActivityList() {
            FirebaseDatabase db = FirebaseDatabase.getInstance();

            DatabaseReference ref = db.getReference("trip/" + TripID + "/plan");
            ref.child("activities").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<ArrayList<TripActivity>> t = new GenericTypeIndicator<ArrayList<TripActivity>>() {
                    };
                    if (dataSnapshot.exists()) {
                        activities = dataSnapshot.getValue(t);
                        adapter.notifyDataSetChanged();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void onButtonNewActivityClick() {
            if (!HostID.equals(currentUserID())) {
                btnNewTripActivity.setVisibility(View.INVISIBLE);
                return;
            }
            btnNewTripActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TripActivity tripActivity = new TripActivity();
                    tripActivity.setAlarm(false);
                    tripActivity.place = "";
                    tripActivity.startDate = "";
                    tripActivity.endDate = "";
                    btnSaveChange.setVisibility(View.VISIBLE);
                    activities.add(tripActivity);
                    adapter.notifyDataSetChanged();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference ref = db.getReference("trip/" + TripID + "/plan/activities");
                    ref.setValue(activities);


                }
            });
        }

        private void onBtnSaveChangeClick() {
            if (!HostID.equals(currentUserID()))
                return;
            btnSaveChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference ref = db.getReference("trip/" + TripID + "/plan/activities");
                    ref.setValue(activities);
                    v.setVisibility(View.INVISIBLE);
                }
            });
        }

        class PlanItem extends RecyclerView.ViewHolder {
            EditText txtStartTime, txtEndTime, txtPlace;
            ImageButton btnDeleteThisActivity;
            ToggleButton toggleBtnNotificationOnOff;

            public PlanItem(View itemView) {
                super(itemView);
                txtPlace = itemView.findViewById(R.id.txtPlace);
                txtStartTime = itemView.findViewById(R.id.txtTimeStart);
                txtEndTime = itemView.findViewById(R.id.txtTimeEnd);
                btnDeleteThisActivity = itemView.findViewById(R.id.btnDeleteThisActivity);
                toggleBtnNotificationOnOff = itemView.findViewById(R.id.toggleBtnNotification);
            }

            public EditText getTxtStartTime() {
                return txtStartTime;
            }

            public void setTxtStartTime(EditText txtStartTime) {
                this.txtStartTime = txtStartTime;
            }

            public EditText getTxtEndTime() {
                return txtEndTime;
            }

            public void setTxtEndTime(EditText txtEndTime) {
                this.txtEndTime = txtEndTime;
            }

            public EditText getTxtPlace() {
                return txtPlace;
            }

            public void setTxtPlace(EditText txtPlace) {
                this.txtPlace = txtPlace;
            }

            public ImageButton getBtnDeleteThisActivity() {
                return btnDeleteThisActivity;
            }

            public void setBtnDeleteThisActivity(ImageButton btnDeleteThisActivity) {
                this.btnDeleteThisActivity = btnDeleteThisActivity;
            }

            public ToggleButton getToggleBtnNotificationOnOff() {
                return toggleBtnNotificationOnOff;
            }

            public void setToggleBtnNotificationOnOff(ToggleButton toggleBtnNotificationOnOff) {
                this.toggleBtnNotificationOnOff = toggleBtnNotificationOnOff;
            }
        }

        public class FragmentPlanListAdapter extends RecyclerView.Adapter<PlanItem> {
            ArrayList<TripActivity> list;

            private final TextWatcher enableChangeButton = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    btnSaveChange.setVisibility(View.VISIBLE);
                }
            };

            public FragmentPlanListAdapter(ArrayList<TripActivity> list) {
                this.list = list;

            }

            @NonNull
            @Override
            public PlanItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_activity_item, parent, false);

                return new PlanItem(itemView);
            }

            @Override
            public void onBindViewHolder(@NonNull PlanItem holder, final int position) {
                EditText txtPlace = holder.getTxtPlace();
                EditText txtStartTime = holder.getTxtStartTime();
                EditText txtEndTime = holder.getTxtEndTime();
                ImageButton btnDeleteThisActivity = holder.getBtnDeleteThisActivity();
                final ToggleButton toggleBtnNotificationOnOff = holder.getToggleBtnNotificationOnOff();

                onDeleteActivityClick(position, btnDeleteThisActivity);

                onToggleNotificationClick(position, toggleBtnNotificationOnOff);
                if (HostID.equals(currentUserID())) {
                    txtPlace.setEnabled(false);
                    txtEndTime.setEnabled(false);
                    txtStartTime.setEnabled(false);
                    btnDeleteThisActivity.setVisibility(View.INVISIBLE);
                }
                txtPlace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment)
                                getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

                        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                            @Override
                            public void onPlaceSelected(Place place) {
                                // TODO: Get info about the selected place.

                            }

                            @Override
                            public void onError(Status status) {
                                // TODO: Handle the error.

                            }
                        });
                    }
                });
                txtEndTime.addTextChangedListener(enableChangeButton);
                txtStartTime.addTextChangedListener(enableChangeButton);

                btnDeleteThisActivity.setFocusable(false);
                toggleBtnNotificationOnOff.setFocusable(false);

                txtPlace.setText(list.get(position).getPlace());
                txtStartTime.setText(list.get(position).getStartDate());
                txtEndTime.setText(list.get(position).getEndDate());
            }

            private void onToggleNotificationClick(int position, ToggleButton toggleBtnNotificationOnOff) {
                final TripActivity tripActivity = activities.get(position);
                if (toggleBtnNotificationOnOff.isChecked() != tripActivity.alarm)
                    toggleBtnNotificationOnOff.setChecked(tripActivity.alarm);
                toggleBtnNotificationOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        tripActivity.setAlarm(isChecked);
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference ref = db.getReference("trip/" + TripID + "/plan/activities");
                        ref.setValue(activities);
                        turnNotificationAt(tripActivity.getStartDate(), isChecked);
                        String s;
                        if (isChecked)
                            s = "The application would notify you when the time comes";
                        else
                            s = "The application would not notify you about this activity";
                        Toast t = Toast.makeText(FragmentPlan.this.getContext(), s, Toast.LENGTH_SHORT);
                        t.show();
                    }
                });
            }

            private void onDeleteActivityClick(final int position, ImageButton btnDeleteThisActivity) {
                btnDeleteThisActivity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(FragmentPlan.this.getContext());
                        builder.setTitle("Confirm delete");
                        builder.setMessage("Are you sure to delete?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activities.remove(position);
                                FirebaseDatabase db = FirebaseDatabase.getInstance();
                                DatabaseReference ref = db.getReference("trip/" + TripID + "/plan/activities");
                                ref.setValue(activities);
                                adapter.notifyDataSetChanged();
                                Toast t = Toast.makeText(FragmentPlan.this.getContext(), "Activity deleted", Toast.LENGTH_SHORT);
                                t.show();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        builder.show();

                    }
                });
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        }
    }

    public static class FragmentMember extends Fragment implements OnMapReadyCallback {
        MapView mMap;
        GoogleMap googleMap;

        public FragmentMember() {

        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_member, container, false);
            mMap = v.findViewById(R.id.map);
            mMap.onCreate(savedInstanceState);
            mMap.onResume();

            mMap.getMapAsync(this);

            return v;
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            this.googleMap = googleMap;
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(0, 0))
                    .title("Marker"));
        }
    }


    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return new FragmentPlan();
            return new FragmentMember();
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


}
