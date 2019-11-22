package com.example.shakil.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.shakil.instagram.CommentsActivity;
import com.example.shakil.instagram.Common.Common;
import com.example.shakil.instagram.FollowersActivity;
import com.example.shakil.instagram.Fragments.PostDetailsFragment;
import com.example.shakil.instagram.Fragments.ProfileFragment;
import com.example.shakil.instagram.Model.ImagePostModel;
import com.example.shakil.instagram.Model.NotificationModel;
import com.example.shakil.instagram.Model.UserModel;
import com.example.shakil.instagram.R;
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

public class PostImageAdapter extends RecyclerView.Adapter<PostImageAdapter.MyViewHolder> {

    Context context;
    List<ImagePostModel> imagePostModelList;

    FirebaseUser firebaseUser;

    public PostImageAdapter(Context context, List<ImagePostModel> imagePostModelList) {
        this.context = context;
        this.imagePostModelList = imagePostModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_post_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Glide.with(context).load(imagePostModelList.get(position).getPostImage())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(holder.img_post);

        if (imagePostModelList.get(position).getDescription().equals("")){
            holder.txt_description.setVisibility(View.GONE);
        }
        else {
            holder.txt_description.setVisibility(View.VISIBLE);
            holder.txt_description.setText(imagePostModelList.get(position).getDescription());
        }

        publisherInfo(holder.img_profile, holder.txt_userName, holder.txt_publisher, imagePostModelList.get(position).getPublisher());

        getLiked(imagePostModelList.get(position).getPostId(), holder.img_like);

        countLikes(holder.txt_likes, imagePostModelList.get(position).getPostId());

        getAllComments(imagePostModelList.get(position).getPostId(), holder.txt_comments);

        isSaved(imagePostModelList.get(position).getPostId(), holder.img_save);

        holder.img_like.setOnClickListener(view -> {
            if (holder.img_like.getTag().equals("like")){
                FirebaseDatabase.getInstance().getReference().child(Common.USER_LIKES).child(imagePostModelList.get(position).getPostId())
                        .child(firebaseUser.getUid()).setValue(true);

                addNotifications(imagePostModelList.get(position).getPublisher(), imagePostModelList.get(position).getPostId());
            }
            else {
                FirebaseDatabase.getInstance().getReference().child(Common.USER_LIKES).child(imagePostModelList.get(position).getPostId())
                        .child(firebaseUser.getUid()).removeValue();
            }
        });

        holder.img_save.setOnClickListener(view -> {
            if (holder.img_save.getTag().equals("save")){
                FirebaseDatabase.getInstance().getReference().child(Common.USER_SAVES)
                        .child(firebaseUser.getUid())
                        .child(imagePostModelList.get(position).getPostId())
                        .setValue(true);
            }
            else {
                FirebaseDatabase.getInstance().getReference().child(Common.USER_SAVES)
                        .child(firebaseUser.getUid())
                        .child(imagePostModelList.get(position).getPostId())
                        .removeValue();
            }
        });

        holder.img_comment.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentsActivity.class);
            intent.putExtra("POSTID", imagePostModelList.get(position).getPostId());
            intent.putExtra("PUBLISHERID", imagePostModelList.get(position).getPublisher());
            context.startActivity(intent);
        });

        holder.txt_comments.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentsActivity.class);
            intent.putExtra("POSTID", imagePostModelList.get(position).getPostId());
            intent.putExtra("PUBLISHERID", imagePostModelList.get(position).getPublisher());
            context.startActivity(intent);
        });

        holder.img_profile.setOnClickListener(v -> {
            SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
            editor.putString("PROFILEID", imagePostModelList.get(position).getPostId());
            editor.apply();

            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment()).commit();
        });

        holder.txt_userName.setOnClickListener(v -> {
            SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
            editor.putString("PROFILEID", imagePostModelList.get(position).getPostId());
            editor.apply();

            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment()).commit();
        });

        holder.txt_publisher.setOnClickListener(v -> {
            SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
            editor.putString("PROFILEID", imagePostModelList.get(position).getPostId());
            editor.apply();

            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment()).commit();
        });

        holder.img_post.setOnClickListener(view -> {
            SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
            editor.putString("POSTID", imagePostModelList.get(position).getPostId());
            editor.apply();

            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PostDetailsFragment()).commit();
        });

        holder.txt_likes.setOnClickListener(v -> {
            Intent intent = new Intent(context, FollowersActivity.class);
            intent.putExtra("ID", imagePostModelList.get(position).getPostId());
            intent.putExtra("TITLE", "likes");
            context.startActivity(intent);
        });
    }

    private void addNotifications(String userId, String postId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USER_NOTIFICATION).child(userId);

        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setUserId(firebaseUser.getUid());
        notificationModel.setText("liked your post");
        notificationModel.setPostId(postId);
        notificationModel.setPost(true);

        reference.push().setValue(notificationModel);
    }

    private void isSaved(String postId, ImageView imgSave) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Common.USER_SAVES)
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).exists()){
                    imgSave.setImageResource(R.drawable.ic_bookmark_black_24dp);
                    imgSave.setTag("saved");
                }
                else {
                    imgSave.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                    imgSave.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllComments(String postId, TextView comments) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Common.USER_COMMENTS).child(postId);

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

    private void countLikes(TextView txt_likes, String postId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Common.USER_LIKES).child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txt_likes.setText(dataSnapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getLiked(String postId, ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Common.USER_LIKES).child(postId);

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

    private void publisherInfo(CircleImageView img_profile, TextView txt_userName, TextView txt_publisher, String publisherId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCE).child(publisherId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                Glide.with(context).load(userModel.getImageLink()).into(img_profile);
                txt_userName.setText(userModel.getUserName());
                txt_publisher.setText(userModel.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagePostModelList.size();
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
}
