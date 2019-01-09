package cs486.nmnhut.gogo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.util.ArrayList;

import static cs486.nmnhut.gogo.DatabaseHelper.currentUserID;

public class GalleryRecyclerView extends AppCompatActivity {

    private static final String TAG = "GalleryRecyclerView";
    final int REQUEST_ID_READ_WRITE_PERMISSION = 99;

    private TextView textView;

    private ArrayList<String> mImageUrls;
    private ArrayList<String> mImageNameList;
    private String stateName, UID;

    DatabaseReference databaseReferenceGalleryRecyclerView;
    GalleryRecyclerViewAdapter adapter;

    @Override
    protected void onStart() {
        super.onStart();

//        ValueEventListener valueEventListener = databaseReferenceGalleryRecyclerView.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                mImageNameList.clear();
//               // String s = dataSnapshot.child("HCM").child("namle").getValue(String.class);
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//                    String filename = snapshot.getValue(String.class);
//
//                    Log.d(TAG, "onDataChange: onDataChange is running");
//
//                    mImageNameList.add(filename);
//
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        databaseReferenceGalleryRecyclerView.addValueEventListener(valueEventListener);

        //initImageBitmaps();


    }

    void DownloadData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_recycler_view);

        // check quyen tru cap du lieu cua dien thoai
        askPermisson();
        Intent intent = getIntent();
        stateName = intent.getStringExtra("state");
        mImageUrls = new ArrayList<>();
        mImageNameList = new ArrayList<>();

        UID = currentUserID();

        databaseReferenceGalleryRecyclerView = FirebaseDatabase.getInstance().getReference("MediaFolder").child("imageFolder").child(UID).child(stateName);

        databaseReferenceGalleryRecyclerView.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "onChildAdded: running");
                    String path = getGOGOimageFolderPath();
                    String filename = dataSnapshot.getValue(String.class);
                    final String imagePath = path + "/" + filename;
                    Log.d(TAG, "onChildAdded: imagePath: " + imagePath);
                    File file = new File(imagePath);
                    if (file.exists())
                        adapter.add(imagePath);
                    else {
                        FirebaseStorageHelper helper = new FirebaseStorageHelper(GalleryRecyclerView.this, UID, stateName);
                        helper.downloadFile(filename, path, new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                adapter.add(imagePath);
                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mImageUrls.clear();
                if (dataSnapshot.exists()) {

                    Log.d(TAG, "onChildChanged: running");
                    String path = getGOGOimageFolderPath();
                    String imageName = dataSnapshot.getValue(String.class);
                    String imagePath = path + "/" + imageName;
                    Log.d(TAG, "onChildChanged: imagePath: " + imagePath);
                    adapter.update(imagePath, s);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "onChildRemoved: ");
                    String path = getGOGOimageFolderPath();
                    String filename = dataSnapshot.getValue(String.class);
                    String imagePath = path + "/" + filename;
                    Log.d(TAG, "onChildRemoved: imagePath: " + imagePath);
                    adapter.remove(imagePath);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("BugBugBugBug", databaseError.toString());
            }
        });

        initRecyclerView();

        //co state roi thi lam gi lam di =))
        //t di danh lol :v
        // danh cc
        //project nhu cc
        // doi nhu loz
        // code nhu buoi

       /* databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mImageNameList.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {

                        String temp =  s.getKey();

                        mImageNameList.add(temp);
                        //Log.d(TAG, "onDataChange: State : " + temp);
                    }
                }

                // list view hien thi tat ca nhung thanh pho ma nguoi dung da di qua
//                StateList adapterListView = new StateList(Gallery.this,stateList);
//                listViewStates.setAdapter(adapterListView);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

*/

    }

    private void initImageBitmaps() {
        Log.d(TAG, "initImageBitmaps: preparing bitmaps");

        // da co duong dan toi thu muc GOGOimages roi
        // bay gio can loc anh ra

        String path = getGOGOimageFolderPath();
        Log.d(TAG, "initImageBitmaps: Path to image folder: " + path);

        File directory = new File(path);
        File[] files = directory.listFiles();

//        Log.d(TAG, "initImageBitmaps: Length of file: " + files.length);
//
//        for(int i =0;i<files.length; i++)
//        {
//            Log.d(TAG, "onCreate: File Path: " + files[i].getAbsolutePath());
//
//
//            mImageUrls.add(files[i].getAbsolutePath());
//        }

        File file = new File(path);

        for (int i = 0; i < mImageNameList.size(); ++i) {
            String imagePath = path + "/" + mImageNameList.get(i);
            file = new File(imagePath);
            if (file.exists()) {
                Log.d(TAG, "initImageBitmaps: imageURL: " + imagePath);
                mImageUrls.add(imagePath);
            }
            Log.d(TAG, "initImageBitmaps: imagePath + mImageNameList = " + imagePath);
            mImageUrls.add(imagePath);
        }

        initRecyclerView();
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init Recyclerview.");
        RecyclerView recyclerView = findViewById(R.id.recyclerv_view);
        adapter = new GalleryRecyclerViewAdapter(this, mImageUrls);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void askPermisson() {
        // Với Android Level >= 23 bạn phải hỏi người dùng cho phép đọc/ghi dữ liệu vào thiết bị.
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            // Kiểm tra quyền đọc/ghi dữ liệu vào thiết bị lưu trữ ngoài.
            int readPermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (writePermission != PackageManager.PERMISSION_GRANTED ||
                    readPermission != PackageManager.PERMISSION_GRANTED) {

                // Nếu không có quyền, cần nhắc người dùng cho phép.
                this.requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_ID_READ_WRITE_PERMISSION
                );
                return;
            }
        }
    }

    String getGOGOimageFolderPath() {
        File folder = Environment.getExternalStorageDirectory();

        if (!folder.exists()) {
            folder.mkdir();
        }


        String path = folder.getAbsolutePath() + "/" + "GOGOimages";

        Toast.makeText(this, path, Toast.LENGTH_LONG).show();

        return path;
    }
}
