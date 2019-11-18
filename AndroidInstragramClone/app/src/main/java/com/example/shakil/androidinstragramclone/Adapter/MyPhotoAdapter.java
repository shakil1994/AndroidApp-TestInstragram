package com.example.shakil.androidinstragramclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shakil.androidinstragramclone.Model.PostModel;
import com.example.shakil.androidinstragramclone.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyPhotoAdapter extends RecyclerView.Adapter<MyPhotoAdapter.MyViewHolder> {

    Context context;
    List<PostModel> postModelList;

    public MyPhotoAdapter(Context context, List<PostModel> postModelList) {
        this.context = context;
        this.postModelList = postModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_photos_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(postModelList.get(position).getPostImage()).into(holder.img_post);
    }

    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_post)
        ImageView img_post;

        Unbinder unbinder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}
