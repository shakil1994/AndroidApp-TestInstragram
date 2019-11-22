package com.example.shakil.instagram.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shakil.instagram.Adapter.PostImageAdapter;
import com.example.shakil.instagram.CommentsActivity;
import com.example.shakil.instagram.Common.Common;
import com.example.shakil.instagram.Model.ImagePostModel;
import com.example.shakil.instagram.R;
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

public class PostDetailsFragment extends Fragment {

    @BindView(R.id.recycler_photo)
    RecyclerView recycler_photo;

    Unbinder unbinder;

    String postId;
    PostImageAdapter adapter;
    List<ImagePostModel> imagePostModelList;

    public PostDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_post_details, container, false);

        unbinder = ButterKnife.bind(this, itemView);

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postId = preferences.getString("POSTID", "none");

        recycler_photo.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_photo.setLayoutManager(layoutManager);
        imagePostModelList = new ArrayList<>();
        adapter = new PostImageAdapter(getContext(), imagePostModelList);
        recycler_photo.setAdapter(adapter);

        loadPost();

        return itemView;
    }

    private void loadPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USER_POSTS).child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imagePostModelList.clear();
                ImagePostModel imagePostModel = dataSnapshot.getValue(ImagePostModel.class);
                imagePostModelList.add(imagePostModel);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
