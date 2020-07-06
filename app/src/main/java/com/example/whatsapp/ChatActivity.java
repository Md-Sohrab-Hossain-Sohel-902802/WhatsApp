package com.example.whatsapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {


    private  String messageReciverId;
    private  String messageReciverName;

    private  TextView userName,userLastseen;
    private  CircleImageView userImage;
    private String messageReciverImage;

    private Toolbar chatToolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        messageReciverId=getIntent().getStringExtra("visit_user_id");
        messageReciverName=getIntent().getStringExtra("visit_user_name");
        messageReciverImage=getIntent().getStringExtra("visit_user_image");

        Initialize();

        userName.setText(messageReciverName);
        Picasso.get().load(messageReciverImage).placeholder(R.drawable.profile_image).into(userImage);






    }

    private void Initialize() {





        chatToolbar=findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolbar);


        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView =layoutInflater.inflate(R.layout.custom_chat_ber,null);
        actionBar.setCustomView(actionBarView);

        userImage=findViewById(R.id.custom_profile_image);
        userName=findViewById(R.id.custom_profile_name);
        userLastseen=findViewById(R.id.custom_user_last_seen);







    }
}