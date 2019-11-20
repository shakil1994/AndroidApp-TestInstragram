package com.example.shakil.androidinstragramclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OptionsActivity extends AppCompatActivity {

    @BindView(R.id.txt_settings)
    TextView txt_settings;

    @BindView(R.id.txt_logout)
    TextView txt_logout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Options");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
    }

    @OnClick(R.id.txt_logout)
    void onLogoutClick(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, MainActivity.class)
        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
