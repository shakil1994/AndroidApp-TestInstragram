package com.example.shakil.androidinstragramclone.Fragments;


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
import android.widget.Button;
import android.widget.EditText;

import com.example.shakil.androidinstragramclone.Adapter.UserSearchAdapter;
import com.example.shakil.androidinstragramclone.Common.Common;
import com.example.shakil.androidinstragramclone.Model.UserModel;
import com.example.shakil.androidinstragramclone.R;
import com.firebase.ui.auth.data.model.User;
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

    @BindView(R.id.search_app_bar)
    AppBarLayout search_app_bar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.edt_search)
    EditText edt_search;

    @BindView(R.id.recycler_search_result)
    RecyclerView recycler_search_result;

    UserSearchAdapter adapter;
    List<UserModel> userModelList;

    private Unbinder unbinder;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View itemView = inflater.inflate(R.layout.fragment_search, container, false);

        unbinder = ButterKnife.bind(this, itemView);

        initViews();

        readUser();

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

    private void searchUsers(String toLowerCase) {
        Query query = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCE).orderByChild(Common.currentUser.getUserName()).startAt(toLowerCase).endAt(toLowerCase + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userModelList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel user = snapshot.getValue(UserModel.class);
                    userModelList.add(user);
                }
                recycler_search_result.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (edt_search.getText().toString().equals("")) {
                    userModelList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        userModelList.add(user);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void initViews() {
        userModelList = new ArrayList<>();
        recycler_search_result.setHasFixedSize(true);
        recycler_search_result.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserSearchAdapter(getContext(), userModelList);
        recycler_search_result.setAdapter(adapter);
    }

}
