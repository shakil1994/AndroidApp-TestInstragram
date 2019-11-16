package com.example.shakil.androidinstragramclone;

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
import com.example.shakil.androidinstragramclone.Adapter.CommentsAdapter;
import com.example.shakil.androidinstragramclone.Common.Common;
import com.example.shakil.androidinstragramclone.Model.CommentsModel;
import com.example.shakil.androidinstragramclone.Model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.img_profile)
    CircleImageView img_profile;

    @BindView(R.id.recycler_comments_list)
    RecyclerView recycler_comments_list;

    @BindView(R.id.edt_add_comment)
    EditText edt_add_comment;

    @BindView(R.id.txt_post)
    TextView txt_post;

    String postId, publisherId;

    FirebaseUser firebaseUser;

    CommentsAdapter adapter;
    List<CommentsModel> commentsModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        ButterKnife.bind(this);

        recycler_comments_list.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_comments_list.setLayoutManager(layoutManager);
        commentsModelList = new ArrayList<>();
        adapter = new CommentsAdapter(this, commentsModelList);
        recycler_comments_list.setAdapter(adapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> {
            finish();
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        postId = intent.getStringExtra("POSTID");
        publisherId = intent.getStringExtra("PUBLISHERID");

        txt_post.setOnClickListener(view -> {
            if (edt_add_comment.getText().equals("")) {
                Toast.makeText(this, "You can't send empty comment", Toast.LENGTH_SHORT).show();
            } else {
                addComment();
            }
        });

        getImage();

        readComments();
    }

    private void addComment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

        CommentsModel commentsModel = new CommentsModel();
        commentsModel.setComment(edt_add_comment.getText().toString());
        commentsModel.setPublisher(firebaseUser.getUid());

        reference.push().setValue(commentsModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Your comment send !", Toast.LENGTH_SHORT).show();
                edt_add_comment.setText("");
            }
        });

    }

    private void getImage(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCE).child(firebaseUser.getUid());

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

    private void readComments(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsModelList.clear();
                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()){
                    CommentsModel commentsModel = commentSnapshot.getValue(CommentsModel.class);
                    commentsModelList.add(commentsModel);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
