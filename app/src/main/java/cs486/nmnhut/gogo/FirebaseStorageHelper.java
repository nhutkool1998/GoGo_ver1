package cs486.nmnhut.gogo;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

class FirebaseStorageHelper {
    Context context;
    private StorageReference storageRef;
    String UID, state;

    FirebaseStorageHelper(Context context, String UID, String state) {
        storageRef = FirebaseStorage.getInstance().getReference();
        this.UID = UID;
        this.state = state;
        this.context = context;
    }

    void uploadToFirebase(String path, String filename, OnSuccessListener<UploadTask.TaskSnapshot> s, OnFailureListener f) {
        Uri file = Uri.fromFile(new File(path));
        StorageReference ref = storageRef.child(UID).child(state).child(filename);
        ref.putFile(file)
                .addOnSuccessListener(s)
                .addOnFailureListener(f);
    }

    void downloadFile(String filename, String path, OnSuccessListener<FileDownloadTask.TaskSnapshot> s, OnFailureListener f) {
        File localFile = null;
        try {
            localFile = File.createTempFile(filename, "png", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StorageReference ref = storageRef.child(UID).child(state).child(filename);

        ref.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });
    }


}
