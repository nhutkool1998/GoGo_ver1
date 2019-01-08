package cs486.nmnhut.gogo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static cs486.nmnhut.gogo.DatabaseHelper.currentUserID;

public class Gallery extends AppCompatActivity {

    private static final String TAG = "Gallery";

    // list viwe thanh pho
    private List<String> stateList;
    private ListView listViewStates;
    private StateList adapter;
    DatabaseReference databaseReference;

    @Override
    protected void onStart() {
        super.onStart();

        listViewStates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //
                Intent intent = new Intent(Gallery.this, GalleryRecyclerView.class);
                intent.putExtra("state", stateList.get(position));
                startActivity(intent);
            }

        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        String UID = currentUserID();
        databaseReference = FirebaseDatabase.getInstance().getReference("MediaFolder").child("imageFolder").child(UID);

        listViewStates = findViewById(R.id.listViewStates);
        stateList = new ArrayList<>();
        adapter = new StateList(this, stateList);
        listViewStates.setAdapter(adapter);
        // t nói là on item click listener
        //tại sao lại là on cick listener

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                stateList.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {

                        String temp = s.getKey();

                        stateList.add(temp);
                        //Log.d(TAG, "onDataChange: State : " + temp);
                    }
                }

                adapter.update(stateList);
                // list view hien thi tat ca nhung thanh pho ma nguoi dung da di qua
//                StateList adapterListView = new StateList(Gallery.this,stateList);
//                listViewStates.setAdapter(adapterListView);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
