package com.example.shakil.instagram.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.shakil.instagram.Adapter.UserSearchAdapter;
import com.example.shakil.instagram.Common.Common;
import com.example.shakil.instagram.Model.UserModel;
import com.example.shakil.instagram.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    @BindView(R.id.appBar)
    AppBarLayout appBar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.edt_search)
    EditText edt_search;

    @BindView(R.id.recycler_search_result)
    RecyclerView recycler_search_result;

    Unbinder unbinder;

    UserSearchAdapter adapter;
    List<UserModel> userModels;


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_search, container, false);

        unbinder = ButterKnife.bind(this, itemView);

        recycler_search_result.setHasFixedSize(true);
        recycler_search_result.setLayoutManager(new LinearLayoutManager(getContext()));

        userModels = new ArrayList<>();
        adapter = new UserSearchAdapter(getContext(), userModels, true);
        recycler_search_result.setAdapter(adapter);

        readUsers();

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return itemView;
    }

    private void searchUsers(String s) {
        Query query = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCE)
                .orderByChild("userName").startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userModels.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    UserModel userModel = dataSnapshot1.getValue(UserModel.class);
                    userModels.add(userModel);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCE);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (edt_search.getText().toString().equals("")){
                    userModels.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        UserModel userModel = dataSnapshot1.getValue(UserModel.class);
                        userModels.add(userModel);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
