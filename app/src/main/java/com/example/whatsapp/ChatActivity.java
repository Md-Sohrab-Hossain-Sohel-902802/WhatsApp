package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ChatActivity extends AppCompatActivity {


    private  String messageReciverId;
    private  String messageReciverName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        messageReciverId=getIntent().getStringExtra("visit_user_id");
        messageReciverName=getIntent().getStringExtra("visit_user_name");




    }
}