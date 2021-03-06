package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.ProtectionDomain;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {




    private Toolbar mToolbar;

    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private  TabsAccessorAdapter myTabAccessorAdapter;




    //<-----------------Firebase Variables----------------------->

    private FirebaseAuth mAuth;
    private  DatabaseReference rootRef;

    String currentUserid;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    //<----------------------------Setup The Action Bar here------------------------------------>
        mToolbar=findViewById(R.id.main_app_ber);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("WhatsApp");

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading.....");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        //<--------------------------------Setup View Pager ------------------------------------>
        mAuth = FirebaseAuth.getInstance();
       rootRef= FirebaseDatabase.getInstance().getReference();

        myViewPager=findViewById(R.id.main_tabs_pager);
        myTabAccessorAdapter=new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabAccessorAdapter);
        myTabLayout=findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);








    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.optionmenu,menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.main_logout_option){
            updateUserstatus("offline");
            mAuth.signOut();
            setdUserToLoginActivity();

        }else  if(item.getItemId()==R.id.main_settings_option){
                sendUsertoSettingActivity();
        }else  if(item.getItemId()==R.id.main_find_friends_option){

            Intent intent=new Intent(MainActivity.this,FindFriends.class);
            startActivity(intent);

        }else if(item.getItemId()==R.id.main_create_group_option){
                RequestNewGroup();
        }






        return  true;

    }

    private void RequestNewGroup() {


        final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name");

        final EditText groupNameField=new EditText(MainActivity.this);
        groupNameField.setHint("Android Developer");
        builder.setView(groupNameField);



        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    String groupName=groupNameField.getText().toString();
                    
                    
                    if(groupName.isEmpty()){
                        Toast.makeText(MainActivity.this, "Please Enter A Group Name", Toast.LENGTH_SHORT).show();
                    }else{
                            CreateNewGroup(groupName);
                    }
                    
                    
                    
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


    builder.show();



    }

    private void CreateNewGroup(final String groupName) {


            rootRef.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, groupName+"  group is Created Successfully.", Toast.LENGTH_SHORT).show();
                        }
                }
            });




    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser==null){
            setdUserToLoginActivity();
        }else{

            updateUserstatus("online");

            VerifyUserExistance();
            
        }







    }


    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null){
            updateUserstatus("offline");
        }





    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null){
            updateUserstatus("offline");
        }

    }

    private void setdUserToLoginActivity() {

        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        finish();

    }
    private void sendUsertoSettingActivity() {

        Intent intent=new Intent(MainActivity.this,SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        finish();
    }



    private void VerifyUserExistance() {

        String currentUserid=mAuth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentUserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.child("name").exists())){
                        progressDialog.dismiss();

                }else{
                     sendUsertoSettingActivity();
                     progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private  void updateUserstatus(String state){

        String saveCurrentTime,saveCurrentDate;
        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat currentDate=new SimpleDateFormat("MM dd,yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");
        saveCurrentTime=currentTime.format(calendar.getTime());



        HashMap<String, Object> onlineMap=new HashMap<>();
        onlineMap.put("time",saveCurrentTime);
        onlineMap.put("date",saveCurrentDate);
        onlineMap.put("state",state);



    currentUserid=mAuth.getCurrentUser().getUid();

    rootRef.child("Users").child(currentUserid).child("userState")
            .updateChildren(onlineMap);













    }


 




}