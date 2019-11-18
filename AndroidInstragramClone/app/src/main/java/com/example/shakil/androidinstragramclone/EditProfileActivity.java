package com.example.shakil.androidinstragramclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.example.shakil.androidinstragramclone.Common.Common;
import com.example.shakil.androidinstragramclone.Model.PostModel;
import com.example.shakil.androidinstragramclone.Model.UserModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class EditProfileActivity extends AppCompatActivity {

    @BindView(R.id.appBar)
    AppBarLayout appBar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.txt_save)
    TextView txt_save;

    @BindView(R.id.img_profile)
    CircleImageView img_profile;

    @BindView(R.id.txt_change_photo)
    TextView txt_change_photo;

    @BindView(R.id.edt_fullName)
    MaterialEditText edt_fullName;

    @BindView(R.id.edt_userName)
    MaterialEditText edt_userName;

    @BindView(R.id.edt_bio)
    MaterialEditText edt_bio;

    FirebaseUser firebaseUser;
    Uri imageUri;
    StorageTask uploadTask;
    StorageReference storageReference;

    AlertDialog dialog;

    String myUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ButterKnife.bind(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCE).child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                edt_fullName.setText(userModel.getFullName());
                edt_userName.setText(userModel.getUserName());
                edt_bio.setText(userModel.getBio());
                Glide.with(getApplicationContext()).load(userModel.getImageLink()).into(img_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
    }

    @OnClick(R.id.img_close)
    void onCloseClick(){
        finish();
    }

    @OnClick(R.id.txt_change_photo)
    void onChangePhotoClick(){
        CropImage.activity().setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(this);
    }

    @OnClick(R.id.img_profile)
    void onPhotoViewClick(){
        CropImage.activity().setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(this);
    }

    @OnClick(R.id.txt_save)
    void onSaveClick(){
        updateInfo(edt_fullName.getText().toString(), edt_userName.getText().toString(), edt_bio.getText().toString());
    }

    private void updateInfo(String fullName, String userName, String bio) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCE).child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("fullName", fullName);
        hashMap.put("userName", userName);
        hashMap.put("bio", bio);

        reference.updateChildren(hashMap);
        finish();
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        if (imageUri != null){
            dialog.show();
            StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            uploadTask = reference.putFile(imageUri);
            uploadTask.continueWithTask((Continuation) task -> {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Uri downloadUri = (Uri) task.getResult();
                    myUrl = downloadUri.toString();

                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCE).child(firebaseUser.getUid());

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("imageLink", myUrl);

                    reference1.updateChildren(hashMap);
                    dialog.dismiss();
                }
                else {
                    dialog.dismiss();
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                dialog.dismiss();
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
        else {
            Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            uploadImage();

            img_profile.setImageURI(imageUri);
        }
        else {
            Toast.makeText(this, "Something wrong!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EditProfileActivity.this, WelcomeActivity.class));
            finish();
        }
    }
}
