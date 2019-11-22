package com.example.shakil.instagram.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.shakil.instagram.Adapter.PostImageAdapter;
import com.example.shakil.instagram.Common.Common;
import com.example.shakil.instagram.Model.ImagePostModel;
import com.example.shakil.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
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
import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    Unbinder unbinder;

    @BindView(R.id.recycler_image_post)
    RecyclerView recycler_image_post;

    private PostImageAdapter adapter;
    private List<ImagePostModel> imagePostModels;
    private List<String> followingList;

    AlertDialog dialog;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_home, container, false);

        unbinder = ButterKnife.bind(this, itemView);

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

        recycler_image_post.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recycler_image_post.setLayoutManager(layoutManager);

        imagePostModels = new ArrayList<>();
        adapter = new PostImageAdapter(getContext(), imagePostModels);
        recycler_image_post.setAdapter(adapter);

        checkFollowing();

        return itemView;
    }

    private void checkFollowing() {
        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USER_FOLLOW)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot followingSnapshot : dataSnapshot.getChildren()){
                    followingList.add(followingSnapshot.getKey());
                }
                readPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readPosts() {
        dialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USER_POSTS);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imagePostModels.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    ImagePostModel imagePostModel = postSnapshot.getValue(ImagePostModel.class);
                    for (String id : followingList){
                        if (imagePostModel.getPublisher().equals(id)){
                            imagePostModels.add(imagePostModel);
                        }
                    }
                }
                adapter.notifyDataSetChanged();

                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
