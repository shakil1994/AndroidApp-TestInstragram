package com.example.shakil.androidinstragramclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shakil.androidinstragramclone.CommentsActivity;
import com.example.shakil.androidinstragramclone.Common.Common;
import com.example.shakil.androidinstragramclone.Model.PostModel;
import com.example.shakil.androidinstragramclone.Model.UserModel;
import com.example.shakil.androidinstragramclone.R;
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
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostImageAdapter extends RecyclerView.Adapter<PostImageAdapter.MyViewHolder> {

    Context context;
    List<PostModel> postModelList;

    public PostImageAdapter(Context context, List<PostModel> postModelList) {
        this.context = context;
        this.postModelList = postModelList;
    }

    FirebaseUser firebaseUser;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_post_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Glide.with(context).load(postModelList.get(position).getPostImage()).into(holder.img_post);

        if (postModelList.get(position).getDescription().equals("")) {
            holder.txt_description.setVisibility(View.GONE);
        } else {
            holder.txt_description.setVisibility(View.VISIBLE);
            holder.txt_description.setText(postModelList.get(position).getDescription());
        }

        publisherInfo(holder.img_profile, holder.txt_userName, holder.txt_publisher, postModelList.get(position).getPublisher());

        isLiked(postModelList.get(position).getPostId(), holder.img_like);
        nrLikes(holder.txt_likes, postModelList.get(position).getPostId());

        getAllComments(postModelList.get(position).getPostId(), holder.txt_comments);

        isSaved(postModelList.get(position).getPostId(), holder.img_save);

        holder.img_like.setOnClickListener(view -> {
            if (holder.img_like.getTag().equals("like")){
                FirebaseDatabase.getInstance().getReference().child("Likes").child(postModelList.get(position).getPostId())
                        .child(firebaseUser.getUid()).setValue(true);
            }
            else {
                FirebaseDatabase.getInstance().getReference().child("Likes").child(postModelList.get(position).getPostId())
                        .child(firebaseUser.getUid()).removeValue();
            }
        });

        holder.img_comment.setOnClickListener(view -> {
            Intent intent = new Intent(context, CommentsActivity.class);
            intent.putExtra("POSTID", postModelList.get(position).getPostId());
            intent.putExtra("PUBLISHERID", postModelList.get(position).getPublisher());
            context.startActivity(intent);
        });

        holder.txt_comments.setOnClickListener(view -> {
            Intent intent = new Intent(context, CommentsActivity.class);
            intent.putExtra("POSTID", postModelList.get(position).getPostId());
            intent.putExtra("PUBLISHERID", postModelList.get(position).getPublisher());
            context.startActivity(intent);
        });

        holder.img_save.setOnClickListener(v -> {
            if (holder.img_save.getTag().equals("save")){
                FirebaseDatabase.getInstance().getReference().child("Saves")
                        .child(firebaseUser.getUid())
                        .child(postModelList.get(position).getPostId())
                        .setValue(true);
            }
            else {
                FirebaseDatabase.getInstance().getReference().child("Saves")
                        .child(firebaseUser.getUid())
                        .child(postModelList.get(position).getPostId())
                        .removeValue();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        Unbinder unbinder;

        @BindView(R.id.img_profile)
        CircleImageView img_profile;

        @BindView(R.id.txt_userName)
        TextView txt_userName;

        @BindView(R.id.img_post)
        ImageView img_post;

        @BindView(R.id.img_like)
        ImageView img_like;

        @BindView(R.id.img_comment)
        ImageView img_comment;

        @BindView(R.id.img_save)
        ImageView img_save;

        @BindView(R.id.txt_likes)
        TextView txt_likes;

        @BindView(R.id.txt_publisher)
        TextView txt_publisher;

        @BindView(R.id.txt_description)
        TextView txt_description;

        @BindView(R.id.txt_comments)
        TextView txt_comments;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            unbinder = ButterKnife.bind(this, itemView);
        }
    }

    private void publisherInfo(ImageView image, TextView userName, TextView publisher, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCE).child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                Glide.with(context).load(userModel.getImageLink()).into(image);
                userName.setText(userModel.getUserName());
                publisher.setText(userModel.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isLiked(String postId, ImageView imageView){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_favorite_red_24dp);
                    imageView.setTag("liked");
                }
                else {
                    imageView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void nrLikes(TextView likes, String postId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllComments(String postId, TextView comments){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText("View All " + dataSnapshot.getChildrenCount() + " Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isSaved(String postId, ImageView imageView){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).exists()){
                    imageView.setImageResource(R.drawable.ic_bookmark_black_24dp);
                    imageView.setTag("saved");
                }
                else {
                    imageView.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
