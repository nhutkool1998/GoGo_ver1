package cs486.nmnhut.gogo;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

public class Camera extends AppCompatActivity {
    String imageName;
    String imagePath;

    List<String> stateList;
    FirebaseStorageHelper firebaseStorageHelper;
    //firebase section:
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceImageName;

    // array ten anh va video;
    public List<ImageName> imagesList;

    //geokey:
    Geocoder geocoder;
    List<Address> addresses = new ArrayList<>();

    private static

    final int REQUEST_ID_READ_WRITE_PERMISSION = 99;
    private static final int REQUEST_ID_IMAGE_CAPTURE = 100;
    private static final String TAG = "Camera";
    GeolocationHelper geo;
    private String state;
    private String uid;


    // ham lay ten anh tren database ve:
    @Override
    protected void onStart() {
        super.onStart();


        // for image section:
        uid = DatabaseHelper.currentUserID();

        geo = new GeolocationHelper(this, uid);

        databaseReferenceImageName = FirebaseDatabase.getInstance().getReference("MediaFolder").child("imageFolder").child(uid);

        databaseReferenceImageName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                stateList.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {

                        //  String temp = (String) s.getValue();

                        // stateList.add(temp);
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
        setContentView(R.layout.activity_camera);

        // tao ra thu muc MediaFolder de luu tru ten anh va video tren firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("MediaFolder");

        imagesList = new ArrayList<>();
        stateList = new ArrayList<>();

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
                    readPermission == PackageManager.PERMISSION_GRANTED
                    && cameraPerission == PackageManager.PERMISSION_GRANTED)) {

                // Nếu không có quyền, cần nhắc người dùng cho phép.
                this.requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        REQUEST_ID_READ_WRITE_PERMISSION
                );
                return;
            } else
                captureImage();

//            if (cameraPerission != PackageManager.PERMISSION_GRANTED) {
//
//                // Nếu không có quyền, cần nhắc người dùng cho phép.
//                this.requestPermissions(
//                        new String[]{Manifest.permission.CAMERA,},
//                        REQUEST_ID_IMAGE_CAPTURE
//                );
//                return;
//            }

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
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();
                    captureImage();
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

    private void captureImage() {

        //camera stuff
        Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); // cau lenh nay de lay ngay

        //folder stuff
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "GOGOimages");

        // thong bao coi getExternalStorageDirectory co chay duoc hay khong
        String res = Environment.getExternalStorageState();
        Context context = getApplicationContext();
        CharSequence text = res;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        imagesFolder.mkdirs(); // tao ra 1 folder de chua anh

        // cho nay custome ten anh luu vao thu muc minh tao ra la GOGOimages nam trong bo nho trong cua dien thoai


        this.imageName = "GOGO" + "_" + timeStamp + ".png";

        //imageName = "GOGO" + "_" + getCityBaseOnLatLng() + ".png";


        //dong nay de luu ten anh dua tren map key
        //imageName = getImageName() + ".png";

        File image = new File(imagesFolder, imageName);

        // co dc duong dan toi file anh roi
        //imagePath = imagesFolder  + imageName;

        // lay duong dan
        this.imagePath = image.getAbsolutePath();

        Uri uriSavedImage = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".my.package.name.provider", image);

        //luu anh vao thu vien anh bang intent.putextra()
        // dong nay luu truc tiep zo the nho dc lun ko can quan duong dan la ji
        // cai cho nay phu thuoc vao user chon luu anh tren dau san roi

        //dong nay de luu video vao thu muc GOGOvideo
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        //dong nay de luu anh vao thu muc the nho
        //imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath);

        startActivityForResult(imageIntent, REQUEST_ID_IMAGE_CAPTURE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // xu li result cua camera
        if (requestCode == REQUEST_ID_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                // neu chup hinh thanh con thi lam ...

                Log.i("MyLog", "Image saved to: " + imagePath);
                Toast.makeText(this, "Image saved to:\n" +
                        imagePath, Toast.LENGTH_LONG).show();
                String UID = uid;
                if (state == null)
                    state = getStateBaseOnLatLng();
                String State = state;
                firebaseStorageHelper = new FirebaseStorageHelper(this, UID, State);
                firebaseStorageHelper.uploadToFirebase(imagePath, imageName, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast t = Toast.makeText(Camera.this, "Success", Toast.LENGTH_SHORT);
                        t.show();
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast t = Toast.makeText(Camera.this, "Failed", Toast.LENGTH_SHORT);
                        t.show();
                    }


                });
                pushImageNameToFirebase(imageName);


            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Action canceled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Action Failed", Toast.LENGTH_LONG).show();
            }
        }
        finish();
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("UID", currentUserID());
//        startActivity(intent);
    }

    // dua ten anh len firebase dang text nhu path sau: /MediaFolder/imageFolder
    private void pushImageNameToFirebase(String imageName) {
        if (!TextUtils.isEmpty(imageName)) {
//            // lay id cua buc anh nay
//            String imageid = databaseReferenceImageName.push().getKey();
//            //tao 1 bien image
//            ImageName img = new ImageName(imageid, imageName);
//
//            String UID = currentUserID();
//            String State = getStateBaseOnLatLng();
//
//            databaseReferenceImageName.child(State).push().setValue(img);
//
//            //DatabaseReference ref = FirebaseDatabase.getInstance().getReference("MediaFolder/imageFolder"+UID);
//            //ref.child(State).push().setValue(img);
            // lay user id voi tinh thanh :
            if (uid == null)
                uid = currentUserID();
            if (state == null)
                state = getStateBaseOnLatLng();
            String imagePath = getGOGOimageFolderPath() + "/" + imageName;

            DatabaseReference dataref = FirebaseDatabase.getInstance()
                    .getReference("MediaFolder").child("imageFolder").child(uid);

            dataref.child(state).push().setValue(imageName);


        } else {
            Toast.makeText(this, "Image name is empty", Toast.LENGTH_SHORT).show();
        }
        return;

    }

    // cu moi lan chay ham nay se duoc gi ?
    // - Lay duoc ten thanh pho dua tren toa do cua user duoi dang String

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

        // truong KHTN
        //  10.7629183
        //  106.679983

        // kien giang 10.0148053,105.0791533    9.5377393,105.2318893

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

        return state; // lay duoc thanh pho
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

