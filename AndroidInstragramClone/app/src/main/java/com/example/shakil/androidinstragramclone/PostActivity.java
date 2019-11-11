package com.example.shakil.androidinstragramclone;

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

import com.example.shakil.androidinstragramclone.Model.PostModel;
import com.google.android.gms.tasks.Continuation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

public class PostActivity extends AppCompatActivity {

    AlertDialog dialog;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;

    @BindView(R.id.txt_post)
    TextView txt_post;

    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.img_post)
    ImageView img_post;

    @BindView(R.id.edt_description)
    EditText edt_description;

    Uri imageUri;
    String myUrl = "";
    StorageTask uploadTask;

    @OnClick(R.id.img_close)
    void onCloseLick(){
        startActivity(new Intent(PostActivity.this, MainActivity.class));
        finish();
    }

    @OnClick(R.id.txt_post)
    void onPostLick(){
        uploadImage();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ButterKnife.bind(this);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("Posts");

        CropImage.activity().setAspectRatio(1, 1).start(PostActivity.this);

        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
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

                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Posts");
                    String postId = reference1.push().getKey();

                    PostModel postModel = new PostModel();
                    postModel.setPostId(postId);
                    postModel.setPostImage(myUrl);
                    postModel.setDescription(edt_description.getText().toString());
                    postModel.setPublisher(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    reference1.child(postId).setValue(postModel)
                            .addOnCompleteListener(task1 -> {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    Toast.makeText(this, "Congratulation !", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                                    finish();
                                }
                            });
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
            Toast.makeText(this, "Image not selected!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            img_post.setImageURI(imageUri);
        }
        else {
            Toast.makeText(this, "Something wrong!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
