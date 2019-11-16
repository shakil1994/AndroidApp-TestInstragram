package com.example.shakil.androidinstragramclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shakil.androidinstragramclone.Common.Common;
import com.example.shakil.androidinstragramclone.MainActivity;
import com.example.shakil.androidinstragramclone.Model.CommentsModel;
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

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {

    private Context context;
    private List<CommentsModel> commentsModelList;

    private FirebaseUser firebaseUser;

    public CommentsAdapter(Context context, List<CommentsModel> commentsModelList) {
        this.context = context;
        this.commentsModelList = commentsModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_display_comments_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        holder.txt_comment.setText(commentsModelList.get(position).getComment());
        getUserInfo(holder.img_profile, holder.txt_userName, commentsModelList.get(position).getPublisher());

        holder.txt_comment.setOnClickListener(v -> {
            Intent intent = new Intent(context, WelcomeActivity.class);
            intent.putExtra("PUBLISHERID", commentsModelList.get(position).getPublisher());
            context.startActivity(intent);
        });

        holder.img_profile.setOnClickListener(v -> {
            Intent intent = new Intent(context, WelcomeActivity.class);
            intent.putExtra("PUBLISHERID", commentsModelList.get(position).getPublisher());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return commentsModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        Unbinder unbinder;

        @BindView(R.id.img_profile)
        CircleImageView img_profile;

        @BindView(R.id.txt_userName)
        TextView txt_userName;

        @BindView(R.id.txt_comment)
        TextView txt_comment;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            unbinder = ButterKnife.bind(this, itemView);
        }
    }
    private void getUserInfo(CircleImageView imageView, TextView userName, String publisherId){
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
}
