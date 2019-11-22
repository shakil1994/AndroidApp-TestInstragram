package com.example.shakil.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shakil.instagram.Adapter.DisplayCommentsAdapter;
import com.example.shakil.instagram.Common.Common;
import com.example.shakil.instagram.Model.CommentsModel;
import com.example.shakil.instagram.Model.NotificationModel;
import com.example.shakil.instagram.Model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recycler_display_comments)
    RecyclerView recycler_display_comments;

    @BindView(R.id.img_profile)
    CircleImageView img_profile;

    @BindView(R.id.edt_add_comment)
    EditText edt_add_comment;

    @BindView(R.id.txt_post)
    TextView txt_post;

    FirebaseUser firebaseUser;

    String postId, publisherId;

    DisplayCommentsAdapter adapter;
    List<CommentsModel> commentsModels;

    @OnClick(R.id.txt_post)
    void onTextPostClick(){
        if (edt_add_comment.getText().toString().equals("")){
            Toast.makeText(this, "You can't send empty comment", Toast.LENGTH_SHORT).show();
        }
        else {
            addComment();
        }
    }

    private void addComment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USER_COMMENTS).child(postId);

        CommentsModel commentsModel = new CommentsModel();
        commentsModel.setComment(edt_add_comment.getText().toString());
        commentsModel.setPublisher(firebaseUser.getUid());

        reference.push().setValue(commentsModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                addNotifications();

                Toast.makeText(this, "Your Comment send!", Toast.LENGTH_SHORT).show();
                edt_add_comment.setText("");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        ButterKnife.bind(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recycler_display_comments.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_display_comments.setLayoutManager(layoutManager);
        commentsModels = new ArrayList<>();
        adapter = new DisplayCommentsAdapter(this, commentsModels);
        recycler_display_comments.setAdapter(adapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        Intent intent = getIntent();
        postId = intent.getStringExtra("POSTID");
        publisherId = intent.getStringExtra("PUBLISHERID");

        getImage();

        displayComments();
    }

    private void displayComments() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USER_COMMENTS).child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsModels.clear();
                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()){
                    CommentsModel commentsModel = commentSnapshot.getValue(CommentsModel.class);
                    commentsModels.add(commentsModel);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(Common.USER_REFERENCE).child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                Glide.with(getApplicationContext()).load(userModel.getImageLink()).into(img_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNotifications() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USER_NOTIFICATION).child(publisherId);

        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setUserId(firebaseUser.getUid());
        notificationModel.setText("commented: " + edt_add_comment.getText().toString());
        notificationModel.setPostId(postId);
        notificationModel.setPost(true);

        reference.push().setValue(notificationModel);
    }
}
