package cs486.nmnhut.gogo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import static cs486.nmnhut.gogo.DatabaseHelper.InviteMember;
import static cs486.nmnhut.gogo.DatabaseHelper.RemoveMember;
import static cs486.nmnhut.gogo.DatabaseHelper.currentUserID;
import static cs486.nmnhut.gogo.DatabaseHelper.getUserEmail;

public class TripDescriptionActivity extends AppCompatActivity {


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
        if (savedInstanceState != null) {
            TripID = savedInstanceState.getString("TripID");
            HostID = savedInstanceState.getString("HostID");
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
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("TripID", TripID);
        outState.putString("HostID", HostID);
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
        int click_position;
        Button btnSaveChange, btnNewTripActivity, btnOptimize, btnEdit, btnChat;
        final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
        private PendingIntent alarmIntent;
        private AlarmManager alarmManager;
        boolean editable = false;
        private View.OnTouchListener txtStartTime_Touch;

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString("TripID", TripID);
            outState.putString("HostID", HostID);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                   /* adapter.list.get(click_position).place = place.getAddress().toString();
                    adapter.notifyDataSetChanged();*/
                    activities.get(click_position).place = place.getName().toString();
                    activities.get(click_position).toaDo = new ToaDo(place.getLatLng());
                    adapter = new FragmentPlanListAdapter(activities);
                    btnSaveChange.setVisibility(View.VISIBLE);
                    listViewActivity.setAdapter(adapter);
                }
            }
        }

        public FragmentPlan() {

        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (savedInstanceState != null) {
                TripID = savedInstanceState.getString("TripID");
                HostID = savedInstanceState.getString("HostID");
            }
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            View v = inflater.inflate(R.layout.fragment_plan, container, false);

            if (savedInstanceState != null) {
                TripID = savedInstanceState.getString("TripID");
                HostID = savedInstanceState.getString("HostID");
            }

            setHostPrivilege();

            btnNewTripActivity = v.findViewById(R.id.btnNewTripActivity);
            btnSaveChange = v.findViewById(R.id.btnSaveChanges);
            btnOptimize = v.findViewById(R.id.btnOptimize);
            btnEdit = v.findViewById(R.id.btnEdit);
            btnChat = v.findViewById(R.id.btnChatbox);


            onBtnSaveChangeClick();
            btnSaveChange.setVisibility(View.INVISIBLE);
            onButtonNewActivityClick();
            onButtonChatClick();

            onButtonOptimizeClick();
            onButtonEditClick();


            listViewActivity = v.findViewById(R.id.listViewActivity);
            activities = new ArrayList<>();
            linearLayoutManager = new LinearLayoutManager(container.getContext());
            listViewActivity.setLayoutManager(linearLayoutManager);
            adapter = new FragmentPlanListAdapter(activities);
            listViewActivity.setAdapter(adapter);

            populateActivityList();

            final ItemTouchHelper.Callback _ithCallback = new ItemTouchHelper.Callback() {
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    Collections.swap(adapter.list, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    btnSaveChange.setVisibility(View.VISIBLE);
                    return true;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    //TODO
                }

                @Override
                public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                    return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                            ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
                }

            };

            ItemTouchHelper ith = new ItemTouchHelper(_ithCallback);
            ith.attachToRecyclerView(listViewActivity);
            return v;
        }

        private void setHostPrivilege() {
            if (!HostID.equals(DatabaseHelper.currentUserID())) {
                btnEdit.setEnabled(false);
                btnNewTripActivity.setEnabled(false);
            }
        }

        private void onButtonChatClick() {
            btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FragmentPlan.this.getContext(), ChatActivity.class);
                    intent.putExtra("tripID", TripID);
                    startActivity(intent);
                }
            });
        }

        private void onButtonEditClick() {
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editable = !editable;
                    if (editable == true)
                        btnEdit.setText("Stop");
                    else
                        btnEdit.setText("Edit");
                }
            });
        }

        private void onButtonOptimizeClick() {
            btnOptimize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // OptimizeWaypoint();
                    newOptimize();
                }
            });
        }

        private void populateActivityList() {
            FirebaseDatabase db = FirebaseDatabase.getInstance();

            DatabaseReference ref = db.getReference("trip/" + TripID + "/plan");
            ref.child("activities").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<ArrayList<TripActivity>> t = new GenericTypeIndicator<ArrayList<TripActivity>>() {
                    };
                    if (dataSnapshot.exists()) {
                        activities = dataSnapshot.getValue(t);
                        adapter = new FragmentPlanListAdapter(activities);
                        listViewActivity.setAdapter(adapter);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        String toJSArray(String place, double lat, double lng) {
//            JSONArray a = new JSONArray();
//            JSONObject o = new JSONObject();
//            try {
//                o.put("address",place);
//                o.put("lat",lat);
//                o.put("lng",lng);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

            String res = "{\"address\":";
            //{"address":"The Hague, The Netherlands","lat":"52.05429","lng":"4.248618"}
            res = res + "\"" + place + "\",";
            res += "\"lat\":" + "\"" + lat + "\",";
            res += "\"lng\":" + "\"" + lng + "\"}";
            return res;
        }

        HashMap<String, String> getStringParams() {
            HashMap<String, String> res = new HashMap<>();
            String locations = "[";
            if (activities.isEmpty())
                return null;
            for (int i = 0; i < activities.size() - 1; ++i) {
                TripActivity t = activities.get(i);
                locations += toJSArray(t.place, t.toaDo.lat, t.toaDo.lng) + ",";
            }
            TripActivity t = activities.get(activities.size() - 1);
            locations += toJSArray(t.place, t.toaDo.lat, t.toaDo.lng) + "]";
            res.put("locations", locations);
            return res;
        }

        void newOptimize() {
            if (activities.size() <= 3)
                return;
            DirectionFinderListener directionFinderListener = new DirectionFinderListener() {
                @Override
                public void onDirectionFinderStart() {

                }

                @Override
                public void onDirectionFinderSuccess(List<cs486.nmnhut.gogo.Route> route) {
                    try {
                        if (activities.size() <= 3)
                            return;
                        List<Integer> waypoints = route.get(0).waypointOrder;
                        ArrayList<TripActivity> newList = new ArrayList<>();
                        newList.add(activities.get(0));
                        for (int i = 0; i < waypoints.size(); ++i) {
                            newList.add(activities.get(waypoints.get(i) + 1));
                        }
                        newList.add(activities.get(activities.size() - 1));
                        adapter.list = activities;
                        adapter.notifyDataSetChanged();
                        btnSaveChange.setVisibility(View.VISIBLE);
                    } catch (Exception ex) {

                    }
                }
            };

            try {
                List<String> waypoints = new ArrayList<>();
                int l = activities.size();
                String start = activities.get(0).place;
                String end = activities.get(l - 1).place;
                for (int i = 0; i < l; ++i)
                    waypoints.add(activities.get(i).place);
                DirectionFinder directionFinder = new DirectionFinder(directionFinderListener, start, end, waypoints);
                directionFinder.execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


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
                    adapter.list = activities;
                    adapter.notifyDataSetChanged();


                }
            });
        }

        private void onBtnSaveChangeClick() {
            if (HostID == null)
                return;
            if (!HostID.equals(currentUserID()))
                return;
            btnSaveChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tripDescription = "Trip Description ";
                    if (!activities.isEmpty()) {
                        for (TripActivity t : activities) {
                            if (t.place == null) {
                                Toast toast = Toast.makeText(getContext(), "Error: Invalid place", Toast.LENGTH_SHORT);
                                toast.show();
                                return;
                            }
                        }
                        tripDescription = "From " + activities.get(0).place + "\n" + "To: " + activities.get(activities.size() - 1).place;
                    }
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference ref = db.getReference("trip/" + TripID + "/plan/activities");
                    ref.setValue(activities);

                    DatabaseReference ref2 = db.getReference("trip/" + TripID);
                    ref2.child("tripDescription").setValue(tripDescription);

                    btnSaveChange.setVisibility(View.INVISIBLE);
                }
            });
        }

        @NonNull
        public View.OnTouchListener getTxtPlace_Touch(@NonNull final PlanItem holder) {
            return new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (!editable)
                        return false;
                    if (event.getAction() != MotionEvent.ACTION_DOWN)
                        return false;
                    v.setEnabled(false);
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                        .build(getActivity());
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                    click_position = holder.getAdapterPosition();
                    v.setEnabled(true);
                    return false;
                }

            };
        }

        @NonNull
        public View.OnTouchListener getTxtEndTime_Touch(final int position, final EditText txtStartTime, final EditText txtEndTime) {
            return new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View v, MotionEvent event) {
                    if (!editable)
                        return false;
                    if (event.getAction() != MotionEvent.ACTION_DOWN)
                        return false;
                    final View dialogView = View.inflate(getActivity(), R.layout.datetimepick_dialog, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    v.setEnabled(false);
                    dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
                            TimePicker timePicker = dialogView.findViewById(R.id.time_picker);
                            Calendar calendar1 = Calendar.getInstance();
                            datePicker.setMinDate(calendar1.getTime().getDate());


                            Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                                    datePicker.getMonth(),
                                    datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(),
                                    timePicker.getCurrentMinute());


                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String s = sdf.format(calendar.getTime());

                            String start = txtStartTime.getText().toString();
                            if (start.compareTo(s) <= 0) {
                                txtEndTime.setText(s);
                                activities.get(position).endDate = s;
                                alertDialog.dismiss();
                                btnSaveChange.setVisibility(View.VISIBLE);
                            } else {
                                Toast t = Toast.makeText(v.getContext(), "End time must be after start time", Toast.LENGTH_SHORT);
                                t.show();
                            }
                        }
                    });
                    alertDialog.setView(dialogView);
                    alertDialog.show();
                    v.setEnabled(true);
                    return false;
                }
            };
        }

        public View.OnTouchListener getTxtStartTime_Touch(final int position, final EditText txtStartTime) {
            return new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View v, MotionEvent event) {
                    if (!editable)
                        return false;
                    if (event.getAction() != MotionEvent.ACTION_DOWN)
                        return false;
                    final View dialogView = View.inflate(getActivity(), R.layout.datetimepick_dialog, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    v.setEnabled(false);
                    dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
                            TimePicker timePicker = dialogView.findViewById(R.id.time_picker);

                            Calendar calendar1 = Calendar.getInstance();

                            int day = calendar1.getTime().getDay();
                            int month = calendar1.getTime().getMonth();
                            int year = calendar1.getTime().getYear();

                            datePicker.setMinDate(calendar1.getTime().getDate());

                            Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                                    datePicker.getMonth(),
                                    datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(),
                                    timePicker.getCurrentMinute());

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String s = sdf.format(calendar.getTime());


                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String now = simpleDateFormat.format(calendar1.getTime());

                            if (now.compareTo(s) <= 0) {
                                txtStartTime.setText(s);
                                activities.get(position).startDate = s;
                                alertDialog.dismiss();
                                btnSaveChange.setVisibility(View.VISIBLE);
                            } else {
                                Toast t = Toast.makeText(v.getContext(), "Time and date must be in the future!", Toast.LENGTH_SHORT);
                                t.show();
                            }


                        }
                    });
                    alertDialog.setView(dialogView);
                    alertDialog.show();
                    v.setEnabled(true);
                    return false;
                }
            };
        }

        class PlanItem extends RecyclerView.ViewHolder {
            EditText txtStartTime, txtEndTime, txtPlace;
            ImageButton btnDeleteThisActivity;
            ToggleButton toggleBtnNotificationOnOff;
            View.OnTouchListener txtStartTime_Touch, txtEndTime_Touch, txtPlace_Touch;
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

            public EditText getTxtEndTime() {
                return txtEndTime;
            }

            public EditText getTxtPlace() {
                return txtPlace;
            }

            public ImageButton getBtnDeleteThisActivity() {
                return btnDeleteThisActivity;
            }

            public ToggleButton getToggleBtnNotificationOnOff() {
                return toggleBtnNotificationOnOff;
            }

        }

        public class FragmentPlanListAdapter extends RecyclerView.Adapter<PlanItem> {
            ArrayList<TripActivity> list;


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

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onBindViewHolder(@NonNull final PlanItem holder, final int position) {
                EditText txtPlace = holder.getTxtPlace();
                final EditText txtStartTime = holder.getTxtStartTime();
                final EditText txtEndTime = holder.getTxtEndTime();
                ImageButton btnDeleteThisActivity = holder.getBtnDeleteThisActivity();
                final ToggleButton toggleBtnNotificationOnOff = holder.getToggleBtnNotificationOnOff();

                txtEndTime.setInputType(InputType.TYPE_NULL);
                txtPlace.setInputType(InputType.TYPE_NULL);
                txtStartTime.setInputType(InputType.TYPE_NULL);

                onDeleteActivityClick(position, btnDeleteThisActivity);
                onToggleNotificationClick(position, toggleBtnNotificationOnOff, holder);
                if (!HostID.equals(currentUserID())) {
                    txtPlace.setEnabled(false);
                    //
                    txtEndTime.setEnabled(false);
                    txtStartTime.setEnabled(false);
                    btnDeleteThisActivity.setVisibility(View.INVISIBLE);
                }

                //txtEndTime.addTextChangedListener(enableChangeButton);
                // txtStartTime.addTextChangedListener(enableChangeButton);
                holder.txtStartTime_Touch = getTxtStartTime_Touch(position, txtStartTime);
                holder.txtEndTime_Touch = getTxtEndTime_Touch(position, txtStartTime, txtEndTime);
                holder.txtPlace_Touch = getTxtPlace_Touch(holder);
                txtStartTime.setOnTouchListener(holder.txtStartTime_Touch);


                txtEndTime.setOnTouchListener(holder.txtEndTime_Touch);

                txtPlace.setOnTouchListener(holder.txtPlace_Touch);

                btnDeleteThisActivity.setFocusable(false);
                toggleBtnNotificationOnOff.setFocusable(false);

                txtPlace.setText(list.get(position).getPlace());
                txtStartTime.setText(list.get(position).getStartDate());
                txtEndTime.setText(list.get(position).getEndDate());
            }

            private void onToggleNotificationClick(final int position, ToggleButton toggleBtnNotificationOnOff, final PlanItem holder) {
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
                        Context context = getActivity().getApplicationContext();
                        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(context, AlarmActivity.class);
                        intent.putExtra("Place", activities.get(position).getPlace());
                        intent.putExtra("StartTime", activities.get(position).getStartDate());
                        intent.putExtra("EndTime", activities.get(position).getEndDate());
                        PendingIntent alarmIntent = PendingIntent.getActivity(context, position, intent, 0);

                        String s;
                        if (isChecked) {
                            s = "The application would notify you when the time comes";
                            String tripID_save = TripID;
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date d = sdf.parse(holder.getTxtStartTime().getText().toString(), new ParsePosition(0));
                            calendar.setTime(d);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
                            TripID = tripID_save;
                        } else {
                            if (alarmManager != null)
                                alarmManager.cancel(FragmentPlan.this.alarmIntent);
                            s = "The application would not notify you a®bout this activity";
                        }
                        Toast t = Toast.makeText(FragmentPlan.this.getContext(), s, Toast.LENGTH_SHORT);
                        btnSaveChange.setVisibility(View.VISIBLE);
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
        GoogleMap map;
        HashMap<String, TripMember> tripMembers;
        Spinner spinner;
        MySpinnerAdapter adapter;
        ArrayList<String> listMemberName;
        boolean firstrun;

        public FragmentMember() {

        }

        boolean isInTrip(String username) {
            return tripMembers.containsKey(username);
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            tripMembers = new HashMap<>();

        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_member, container, false);
            mMap = v.findViewById(R.id.map);
            mMap.onCreate(savedInstanceState);
            mMap.onResume();
            // firstrun = true;
            mMap.getMapAsync(this);

            listMemberName = new ArrayList<>();

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref = db.getReference("trip/" + TripID + "/members");
            initializeMemberFragment(container, v);
            return v;
        }

        private void initializeMemberFragment(@Nullable ViewGroup container, View v) {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref = db.getReference("trip/" + TripID + "/members");
            firstrun = true;
            final ValueEventListener positionEvent = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        String UID = dataSnapshot.getKey();
                        String latS = dataSnapshot.child("lat").getValue(String.class);
                        float lat = Float.parseFloat(latS);

                        String lngS = dataSnapshot.child("lng").getValue(String.class);
                        float lng = Float.parseFloat(lngS);
                        tripMembers.get(UID).position.setLat(lat);
                        tripMembers.get(UID).position.setLng(lng);
                    } catch (Exception ex) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()) {
                        TripMember t = dataSnapshot.getValue(TripMember.class);
                        tripMembers.put(dataSnapshot.getKey(), t);
                        if (listMemberName != null) {
                            listMemberName.add(t.name);
                            adapter = new MySpinnerAdapter(getContext(), R.layout.spinner_item, listMemberName);
                            spinner.setAdapter(adapter);
                        }
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference ref = db.getReference("position/" + dataSnapshot.getKey());
                        ref.addValueEventListener(positionEvent);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()) {
                        tripMembers.put(dataSnapshot.getKey(), dataSnapshot.getValue(TripMember.class));

                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        TripMember t = dataSnapshot.getValue(TripMember.class);
                        tripMembers.remove(dataSnapshot.getKey());
                        if (listMemberName != null) {
                            listMemberName.remove(t.name);
                            adapter = new MySpinnerAdapter(getContext(), R.layout.spinner_item, listMemberName);
                            spinner.setAdapter(adapter);
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            DatabaseReference ref = db.getReference("position/" + dataSnapshot.getKey());
                            ref.removeEventListener(positionEvent);
                        }
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            if (!tripMembers.isEmpty())
                for (String k : tripMembers.keySet()) {
                    listMemberName.add(tripMembers.get(k).name);
                }
            listMemberName.add("Invite friend...");
            spinner = v.findViewById(R.id.spinnerMember);
//            adapter = new MySpinnerAdapter(container.getContext(),R.layout.spinner_item,listMemberName);
//            spinner.setAdapter(adapter);
            firstrun = true;
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (listMemberName.get(position).equals("Invite friend..."))
                        if (!firstrun) {
                            showAddMemberDialog();

                        }
                    else
                        showMemberPosition(listMemberName.get(position));
                    firstrun = false;

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    int position = spinner.getSelectedItemPosition();
                    if (listMemberName.get(position).equals("Invite friend...")) {
                        if (!firstrun) {
                            showAddMemberDialog();
                        }
                    }
                    else
                        showMemberPosition(listMemberName.get(position));
                    firstrun = false;
                }
            });
        }

        private void showAddMemberDialog() {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("GoGo");
            alert.setMessage("Enter email:");

// Set an EditText view to get user input
            final EditText input = new EditText(getContext());
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = input.getText().toString();
                    //TODO: fds
                    int res = InviteMember("Hey guy, Join my trip - by " + getUserEmail(), TripID, HostID, value);
                    if (res == 0) {
                        Toast t = Toast.makeText(getContext(), "Invitation sent", Toast.LENGTH_SHORT);
                        t.show();
                    }
                    if (res == -1) {
                        Toast t = Toast.makeText(getContext(), "Invitation fail. No such user exist", Toast.LENGTH_SHORT);
                        t.show();
                    }
                    if (res == -2) {
                        Toast t = Toast.makeText(getContext(), "Invitation fail. You is not the host", Toast.LENGTH_SHORT);
                        t.show();
                    }
                    return;
                }
            });

            alert.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            return;
                        }
                    });
            alert.show();
        }

        private void showMemberPosition(String name) {
            try {
                String key = DatabaseHelper.getUserIDfromName(name);
                ToaDo t = tripMembers.get(key).position;
                LatLng latLng = t.getLatLng();
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, map.getMaxZoomLevel()));
            } catch (Exception ex) {

            }
        }

        @Override
        public void onMapReady(final GoogleMap googleMap) {
            this.map = googleMap;
            final Handler UI_HANDLER = new Handler();
            final GoogleMap gg = googleMap;
            updateMemberMap();
            final Runnable UI_UPDATE_RUNNABLE = new Runnable() {
                @Override
                public void run() {
                    try {
                        updateMemberMap();
                        UI_HANDLER.postDelayed(this, 60000);
                    } catch (Exception ex) {

                    }
                }
            };
            UI_HANDLER.postDelayed(UI_UPDATE_RUNNABLE, 60000);
        }

        public void updateMemberMap() {
            try {
                map.clear();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                ToaDo td = tripMembers.get(currentUserID()).position;
                for (String k : tripMembers.keySet()) {
                    TripMember t = tripMembers.get(k);

                    LatLng ln = new LatLng(t.position.lat, t.position.lng);
                    map.addMarker(new MarkerOptions()
                            .position(ln)
                            .title(t.name).snippet(t.name));
                    builder.include(ln);
                    double d = td.Distance(t.position);
                    if (t.alarm && d > 1000) {
                        MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.alert);
                        mp.start();
                    }
                }

                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 200);
                map.moveCamera(cu);
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));

            } catch (Exception ex) {

            }
        }

        public class MySpinnerAdapter extends ArrayAdapter<String> implements SpinnerAdapter {
            ArrayList<String> list;
            Context context;

            public MySpinnerAdapter(@NonNull Context context, int resource, ArrayList<String> l) {
                super(context, R.layout.spinner_item, R.id.txtSpinnerMemberName, l);
                this.list = l;
                this.context = context;
            }

            @Override
            public int getCount() {
                return list.size();
            }

            @Nullable
            @Override
            public String getItem(int position) {
                return list.get(position);
            }

            @NonNull
            @Override
            public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(context);
                    convertView = inflater.inflate(R.layout.spinner_item, parent, false);
                }
                TextView txtMember = convertView.findViewById(R.id.txtSpinnerMemberName);
                ImageButton btnActionSpinner = convertView.findViewById(R.id.btnSpinnerAction);
                txtMember.setText(list.get(position));
                if (!list.get(position).equals("Invite friend...")) {
                    btnActionSpinner.setVisibility(View.VISIBLE);
                }
                btnActionSpinner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        RemoveMember(TripID, list.get(position));
                    }
                });
                return convertView;
            }
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
