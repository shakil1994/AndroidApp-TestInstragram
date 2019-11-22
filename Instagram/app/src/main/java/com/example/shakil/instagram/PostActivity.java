package com.example.shakil.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shakil.instagram.Common.Common;
import com.example.shakil.instagram.Model.ImagePostModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

public class PostActivity extends AppCompatActivity {

    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.txt_post)
    TextView txt_post;

    @BindView(R.id.img_post)
    ImageView img_post;

    @BindView(R.id.edt_description)
    EditText edt_description;

    AlertDialog dialog;
    FirebaseStorage storage;
    StorageReference storageReference;

    Uri imageUri;
    String myUrl = "";
    StorageTask uploadTask;

    @OnClick(R.id.img_close)
    void onCloseClick(){
        startActivity(new Intent(PostActivity.this, HomeActivity.class));
        finish();
    }

    @OnClick(R.id.txt_post)
    void onPostClick(){
        uploadImage();
    }

    private void uploadImage() {
        if (imageUri != null){
            dialog.show();
            StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            uploadTask = reference.putFile(imageUri);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Uri downloadUri = (Uri) task.getResult();
                    myUrl = downloadUri.toString();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Common.USER_POSTS);
                    String postId = databaseReference.push().getKey();

                    ImagePostModel imagePostModel = new ImagePostModel();
                    imagePostModel.setPostId(postId);
                    imagePostModel.setPostImage(myUrl);
                    imagePostModel.setDescription(edt_description.getText().toString());
                    imagePostModel.setPublisher(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    databaseReference.child(postId).setValue(imagePostModel).addOnCompleteListener(task1 -> {
                        if (task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(this, "Congratulation !", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PostActivity.this, HomeActivity.class));
                            finish();
                        }
                    });
                }
                else {
                    dialog.dismiss();
                    Toast.makeText(this, "Failed !", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                dialog.dismiss();
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
        else {
            Toast.makeText(this, "Image no selected !", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ButterKnife.bind(this);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference(Common.USER_POSTS);

        CropImage.activity().setAspectRatio(1, 1).start(this);

        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            img_post.setImageURI(imageUri);
        }
        else {
            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }
}
