package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {




    private Toolbar mToolbar;

    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private  TabsAccessorAdapter myTabAccessorAdapter;




    //<-----------------Firebase Variables----------------------->

    private FirebaseUser currentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    //<----------------------------Setup The Action Bar here------------------------------------>
        mToolbar=findViewById(R.id.main_app_ber);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("WhatsApp");


        //<--------------------------------Setup View Pager ------------------------------------>


        myViewPager=findViewById(R.id.main_tabs_pager);
        myTabAccessorAdapter=new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabAccessorAdapter);
        myTabLayout=findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);








    }


    @Override
    protected void onStart() {
        super.onStart();


        if(currentUser==null){
            setdUserToLoginActivity();
        }







    }

    private void setdUserToLoginActivity() {

        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);

    }
}