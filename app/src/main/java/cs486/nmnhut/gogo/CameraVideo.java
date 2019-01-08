package cs486.nmnhut.gogo;


import android.location.Address;
import android.location.Geocoder;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static cs486.nmnhut.gogo.DatabaseHelper.currentUserID;
import static cs486.nmnhut.gogo.GeolocationHelper.getCurrentPosition;

public class CameraVideo extends AppCompatActivity {
    String videoName;
    String videoPath;

    //firebase section:
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceVideoName;
    GeolocationHelper geo;

    // array ten anh va video;
    public List<VideoName> videoNameList;

    //geokey:
    Geocoder geocoder;
    List<Address> addresses = new ArrayList<>();
    private static

    final int REQUEST_ID_READ_WRITE_PERMISSION = 99;
    private static final int REQUEST_ID_VIDEO_CAPTURE = 101;
    private static final String TAG = "CameraVideo";

    // ham lay ten anh tren database ve:
    @Override
    protected void onStart() {
        super.onStart();

        // for video section:
        databaseReferenceVideoName = FirebaseDatabase.getInstance().getReference("MediaFolder").child("videoFolder");

        databaseReferenceVideoName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                videoNameList.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        VideoName temp = s.getValue(VideoName.class);
                        videoNameList.add(temp);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_video);

        // tao ra thu muc MediaFolder de luu tru ten anh va video tren firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("MediaFolder");

        videoNameList = new ArrayList<>();

        askPermission();


    }

    // yeu cau truy cap bo nho trong cua dien thoai
    private void askPermission() {
        // Với Android Level >= 23 bạn phải hỏi người dùng cho phép đọc/ghi dữ liệu vào thiết bị.
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            // Kiểm tra quyền đọc/ghi dữ liệu vào thiết bị lưu trữ ngoài.
            int readPermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int cameraPerission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA);
            if (!(writePermission == PackageManager.PERMISSION_GRANTED &&
                    readPermission == PackageManager.PERMISSION_GRANTED &&
                    cameraPerission == PackageManager.PERMISSION_GRANTED)) {

                // Nếu không có quyền, cần nhắc người dùng cho phép.
                this.requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_ID_READ_WRITE_PERMISSION
                );
                return;
            } else captureVideo();


            if (cameraPerission != PackageManager.PERMISSION_GRANTED) {

                // Nếu không có quyền, cần nhắc người dùng cho phép.
                this.requestPermissions(
                        new String[]{Manifest.permission.CAMERA,},
                        REQUEST_ID_VIDEO_CAPTURE
                );
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case REQUEST_ID_READ_WRITE_PERMISSION: {

                // Chú ý: Nếu yêu cầu bị hủy, mảng kết quả trả về là rỗng.
                // Người dùng đã cấp quyền (đọc/ghi).
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();
                    captureVideo();
                }
                // Hủy bỏ hoặc bị từ chối.
                else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case GeolocationHelper.MY_PERMISSIONS_REQUEST_FINE_LOCATION:
                geo.requestLocationUpdate();
        }
    }

    // ham yeu cau truy bo nho trong dien thoai se goi ham video nay de bat dau quay phim
    private void captureVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        File dir = Environment.getExternalStorageDirectory();

        String res = Environment.getExternalStorageState();
        Context context = getApplicationContext();
        CharSequence text = res;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); // cau lenh nay de lay ngay

        File videosFolder = new File(Environment.getExternalStorageDirectory(), "GOGOvideos"); // tao thu muc ten GOGOvideos

        videosFolder.mkdirs(); // tao ra 1 folder de chua anh

        videoName = "GOGO" + "_" + timeStamp + ".mp4";

        pushVideoNameToFirebase(videoName);

        File video = new File(videosFolder, videoName);

        // String savePath = dir.getAbsolutePath() + "/namuCameraVideo.mp4"; // dong nay de luu ten video nhu the nao do
        //File videoFile = new File(savePath);

        Uri videoUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".my.package.name.provider", video);

        videoPath = videosFolder + "/" + videoName;

        videoPath = video.getAbsolutePath();

        //dong nay de luu video vao thu muc GOGOvideo
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);

        //dong nay de luu anh vao thu muc the nho
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoPath);


        this.startActivityForResult(intent, REQUEST_ID_VIDEO_CAPTURE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ID_VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {

                // thong bao video luu vao dau
                //Uri videoUri = data.getData()

                Log.i("MyLog", "Video saved to: " + videoPath);
                Toast.makeText(this, "Video saved to:\n" +
                        videoPath, Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Action Cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Action Failed",
                        Toast.LENGTH_LONG).show();
            }
            finish();
        }


    }

    // dua ten video len firebase dang text nhu path sau: /MediaFolder/videoFolder
    private void pushVideoNameToFirebase(String videoName) {
        if (!TextUtils.isEmpty(videoName)) {

            String UID = currentUserID();
            String State = getStateBaseOnLatLng();

            DatabaseReference dataref = FirebaseDatabase.getInstance()
                    .getReference("MediaFolder").child("videoFolder").child(UID);

            dataref.child(State).push().setValue(videoName);

        } else {
            Toast.makeText(this, "Video name is empty", Toast.LENGTH_SHORT).show();
        }
        return;
    }

    public String getStateBaseOnLatLng() {


        geocoder = new Geocoder(this, Locale.getDefault());

        LatLng latLng = getCurrentPosition();

        try {
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // vi tri nha t
        // 10.7545373
        // 106.6314118

        //vi tri truong khoa hoc tu nhien:
        // 10.7629183
        // 106.679983

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

        return state; // lay duoc thanh pho
    }

}

