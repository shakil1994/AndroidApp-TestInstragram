package com.example.shakil.androidinstragramclone.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shakil.androidinstragramclone.Common.Common;
import com.example.shakil.androidinstragramclone.Model.CommentsModel;
import com.example.shakil.androidinstragramclone.Model.PostModel;
import com.example.shakil.androidinstragramclone.Model.UserModel;
import com.example.shakil.androidinstragramclone.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    @BindView(R.id.appBar)
    AppBarLayout appBar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.txt_userName)
    TextView txt_userName;

    @BindView(R.id.img_menu)
    ImageView img_menu;

    @BindView(R.id.img_profile)
    CircleImageView img_profile;

    @BindView(R.id.txt_posts)
    TextView txt_posts;

    @BindView(R.id.txt_followers)
    TextView txt_followers;

    @BindView(R.id.txt_following)
    TextView txt_following;

    @BindView(R.id.btn_profile)
    Button btn_profile;

    @BindView(R.id.txt_fullName)
    TextView txt_fullName;

    @BindView(R.id.txt_bio)
    TextView txt_bio;

    @BindView(R.id.img_photos)
    ImageButton img_photos;

    @BindView(R.id.img_saved_photos)
    ImageButton img_saved_photos;

    @BindView(R.id.recycler_photos)
    RecyclerView recycler_photos;

    @BindView(R.id.recycler_saved_photos)
    RecyclerView recycler_saved_photos;

    @OnClick(R.id.btn_profile)
    void onBtnProfileClick() {
        String btn = btn_profile.getText().toString();

        if (btn.equals("Edit Profile")){
            //Go to Edit Profile
        }
        else if (btn.equals("follow")){
            FirebaseDatabase.getInstance().getReference().child(Common.USER_FOLLOW).child(firebaseUser.getUid())
                    .child("following").child(profileId).setValue(true);

            FirebaseDatabase.getInstance().getReference().child(Common.USER_FOLLOW).child(profileId)
                    .child("followers").child(firebaseUser.getUid()).setValue(true);
        }
        else if (btn.equals("following")){
            FirebaseDatabase.getInstance().getReference().child(Common.USER_FOLLOW).child(firebaseUser.getUid())
                    .child("following").child(profileId).removeValue();

            FirebaseDatabase.getInstance().getReference().child(Common.USER_FOLLOW).child(profileId)
                    .child("followers").child(firebaseUser.getUid()).removeValue();
        }
    }

    Unbinder unbinder;

    FirebaseUser firebaseUser;
    String profileId;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_profile, container, false);

        unbinder = ButterKnife.bind(this, itemView);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = preferences.getString("PROFILEID", "none");

        userInfo();
        getFollowers();
        getNrPosts();

        if (profileId.equals(firebaseUser.getUid())){
            btn_profile.setText("Edit Profile");
        }
        else {
            checkFollow();
            img_saved_photos.setVisibility(View.GONE);
        }

        return itemView;
    }

    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCE).child(profileId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null){
                    return;
                }

                UserModel userModel = dataSnapshot.getValue(UserModel.class);

                Glide.with(getContext()).load(userModel.getImageLink()).into(img_profile);
                txt_userName.setText(userModel.getUserName());
                txt_fullName.setText(userModel.getFullName());
                txt_bio.setText(userModel.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Common.USER_FOLLOW).child(firebaseUser.getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileId).exists()){
                    btn_profile.setText("following");
                }else {
                    btn_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowers(){
        DatabaseReference followersReference = FirebaseDatabase.getInstance().getReference()
                .child(Common.USER_FOLLOW).child(profileId).child("followers");

        followersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txt_followers.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference followingReference = FirebaseDatabase.getInstance().getReference()
                .child(Common.USER_FOLLOW).child(profileId).child("following");

        followingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txt_following.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getNrPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PostModel postModel = snapshot.getValue(PostModel.class);
                    if (postModel.getPublisher().equals(profileId)){
                        i++;
                    }
                }
                txt_posts.setText("" + i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
