package com.example.shakil.instagram.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shakil.instagram.Adapter.NotificationAdapter;
import com.example.shakil.instagram.Common.Common;
import com.example.shakil.instagram.Model.NotificationModel;
import com.example.shakil.instagram.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    @BindView(R.id.appBar)
    AppBarLayout appBar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recycler_notification)
    RecyclerView recycler_notification;

    NotificationAdapter adapter;
    List<NotificationModel> notificationModelList;

    Unbinder unbinder;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_notification, container, false);

        unbinder = ButterKnife.bind(this, itemView);

        recycler_notification.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_notification.setLayoutManager(layoutManager);

        notificationModelList = new ArrayList<>();
        adapter = new NotificationAdapter(getContext(), notificationModelList);
        recycler_notification.setAdapter(adapter);

        readNotifications();

        return itemView;
    }

    private void readNotifications() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(Common.USER_NOTIFICATION).child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationModelList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    NotificationModel notificationModel = snapshot.getValue(NotificationModel.class);
                    notificationModelList.add(notificationModel);
                }

                Collections.reverse(notificationModelList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
