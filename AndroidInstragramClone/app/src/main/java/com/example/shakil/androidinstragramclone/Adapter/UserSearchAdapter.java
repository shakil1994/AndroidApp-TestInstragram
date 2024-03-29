package com.example.shakil.androidinstragramclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shakil.androidinstragramclone.Common.Common;
import com.example.shakil.androidinstragramclone.Fragments.ProfileFragment;
import com.example.shakil.androidinstragramclone.MainActivity;
import com.example.shakil.androidinstragramclone.Model.Notification;
import com.example.shakil.androidinstragramclone.Model.UserModel;
import com.example.shakil.androidinstragramclone.R;
import com.example.shakil.androidinstragramclone.WelcomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.MyViewHolder> {

    Context context;
    List<UserModel> userModelList;

    FirebaseUser firebaseUser;
    //======================
    private boolean isFragment;

    public UserSearchAdapter(Context context, List<UserModel> userModelList, boolean isFragment) {
        this.context = context;
        this.userModelList = userModelList;
        this.isFragment = isFragment;
    }
    //======================

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_search_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        holder.btn_follow.setVisibility(View.VISIBLE);

        holder.txt_userName.setText(userModelList.get(position).getUserName());
        holder.txt_fullName.setText(userModelList.get(position).getFullName());

        Glide.with(context).load(userModelList.get(position).getImageLink()).into(holder.image_profile);

        isFollowing(userModelList.get(position).getUid(), holder.btn_follow);

        if (userModelList.get(position).getUid().equals(firebaseUser.getUid())){
            holder.btn_follow.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            //=====================
            if (isFragment) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("PROFILEID", userModelList.get(position).getUid());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment());
            }
            else {
                Intent intent = new Intent(context, WelcomeActivity.class);
                intent.putExtra("PUBLISHERID", userModelList.get(position).getUid());
                context.startActivity(intent);
            }
            //===========================
        });

        holder.btn_follow.setOnClickListener(view -> {
            if (holder.btn_follow.getText().toString().equals("follow")){
                FirebaseDatabase.getInstance().getReference().child(Common.USER_FOLLOW).child(firebaseUser.getUid())
                        .child("following").child(userModelList.get(position).getUid()).setValue(true);

                FirebaseDatabase.getInstance().getReference().child(Common.USER_FOLLOW).child(userModelList.get(position).getUid())
                        .child("followers").child(firebaseUser.getUid()).setValue(true);

                addNotifications(userModelList.get(position).getUid());
            }
            else {

                FirebaseDatabase.getInstance().getReference().child(Common.USER_FOLLOW).child(firebaseUser.getUid())
                        .child("following").child(userModelList.get(position).getUid()).removeValue();

                FirebaseDatabase.getInstance().getReference().child(Common.USER_FOLLOW).child(userModelList.get(position).getUid())
                        .child("followers").child(firebaseUser.getUid()).removeValue();

            }
        });
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        Unbinder unbinder;

        @BindView(R.id.image_profile)
        CircleImageView image_profile;

        @BindView(R.id.txt_userName)
        TextView txt_userName;

        @BindView(R.id.txt_fullName)
        TextView txt_fullName;

        @BindView(R.id.btn_follow)
        Button btn_follow;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            unbinder = ButterKnife.bind(this, itemView);
        }
    }

    private void isFollowing(String userId, Button button){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Common.USER_FOLLOW).child(firebaseUser.getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userId).exists()){
                    button.setText("following");
                }
                else {
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNotifications(String userId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);

        Notification notification = new Notification();
        notification.setUserId(firebaseUser.getUid());
        notification.setText("started following you");
        notification.setPostId("");
        notification.setPost(false);

        reference.push().setValue(notification);
    }
}
