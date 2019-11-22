package com.example.shakil.instagram.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shakil.instagram.Common.Common;
import com.example.shakil.instagram.Fragments.PostDetailsFragment;
import com.example.shakil.instagram.Fragments.ProfileFragment;
import com.example.shakil.instagram.Model.ImagePostModel;
import com.example.shakil.instagram.Model.NotificationModel;
import com.example.shakil.instagram.Model.UserModel;
import com.example.shakil.instagram.R;
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

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    Context context;
    List<NotificationModel> notificationModelList;

    public NotificationAdapter(Context context, List<NotificationModel> notificationModelList) {
        this.context = context;
        this.notificationModelList = notificationModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_notification_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_comment.setText(notificationModelList.get(position).getText());

        getUserInfo(holder.img_profile, holder.txt_userName, notificationModelList.get(position).getUserId());

        if (notificationModelList.get(position).isPost()){
            holder.img_post.setVisibility(View.VISIBLE);
            getPostImage(holder.img_post, notificationModelList.get(position).getPostId());
        }
        else {
            holder.img_post.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (notificationModelList.get(position).isPost()){
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("POSTID", notificationModelList.get(position).getPostId());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PostDetailsFragment()).commit();
            }
            else {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("PROFILEID", notificationModelList.get(position).getPostId());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_post)
        ImageView img_post;

        @BindView(R.id.img_profile)
        CircleImageView img_profile;

        @BindView(R.id.txt_userName)
        TextView txt_userName;

        @BindView(R.id.txt_comment)
        TextView txt_comment;

        Unbinder unbinder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            unbinder = ButterKnife.bind(this, itemView);
        }
    }

    private void getUserInfo(CircleImageView imageView, TextView userName, String publisherId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Common.USER_REFERENCE).child(publisherId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                Glide.with(context).load(userModel.getImageLink()).into(imageView);
                userName.setText(userModel.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPostImage(ImageView imageView, String postId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Common.USER_POSTS).child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ImagePostModel imagePostModel = dataSnapshot.getValue(ImagePostModel.class);
                Glide.with(context).load(imagePostModel.getPostImage()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
