package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {



    private  String reciveUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        reciveUserId=getIntent().getStringExtra("uid");
        Toast.makeText(this, ""+reciveUserId, Toast.LENGTH_SHORT).show();




    }
}