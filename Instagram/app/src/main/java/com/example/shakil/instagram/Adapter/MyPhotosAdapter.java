package com.example.shakil.instagram.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shakil.instagram.Fragments.PostDetailsFragment;
import com.example.shakil.instagram.Fragments.ProfileFragment;
import com.example.shakil.instagram.Model.ImagePostModel;
import com.example.shakil.instagram.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyPhotosAdapter extends RecyclerView.Adapter<MyPhotosAdapter.MyViewHolder> {

    Context context;
    List<ImagePostModel> imagePostModelList;

    public MyPhotosAdapter(Context context, List<ImagePostModel> imagePostModelList) {
        this.context = context;
        this.imagePostModelList = imagePostModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_my_photos_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(imagePostModelList.get(position).getPostImage()).into(holder.img_post);

        holder.img_post.setOnClickListener(view -> {
            SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
            editor.putString("POSTID", imagePostModelList.get(position).getPostId());
            editor.apply();

            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PostDetailsFragment()).commit();
        });
    }

    @Override
    public int getItemCount() {
        return imagePostModelList.size();
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
