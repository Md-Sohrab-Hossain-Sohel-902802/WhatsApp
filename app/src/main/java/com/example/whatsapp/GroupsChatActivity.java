package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupsChatActivity extends AppCompatActivity {



    private Toolbar mToolbar;
    private ImageButton sendMessageButton;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    private TextView displayTextMessage;


    private  String currentGroupName,currentUserid,currentUsername,currentDate,currentTime;

    private FirebaseAuth mAuth;
    
    
    private DatabaseReference usersRefrence,groupNameRefrence,groupMessagekeyref;











    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_chat);


        currentGroupName=getIntent().getStringExtra("groupName").toString();
        mAuth=FirebaseAuth.getInstance();
        currentUserid=mAuth.getCurrentUser().getUid();

        usersRefrence= FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameRefrence= FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        INitializetaionFields();

        getUserInfo();


        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    saveMessageInformation();
                    userMessageInput.setText("");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        







    }


    @Override
    protected void onStart() {
        super.onStart();

        groupNameRefrence.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if(snapshot.exists()){
                        DisplayMessages(snapshot);
                    }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    DisplayMessages(snapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


    private void INitializetaionFields() {


        mToolbar=findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);

        sendMessageButton=findViewById(R.id.send_message_button);
        userMessageInput=findViewById(R.id.input_group_message);
        displayTextMessage=findViewById(R.id.group_chat_text_display);
        mScrollView=findViewById(R.id.my_scroll_view);



    }


    private void getUserInfo() {
        
            usersRefrence.child(currentUserid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    
                    if(snapshot.exists()){
                            currentUsername=snapshot.child("name").getValue().toString();

                    }
                    
                    
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        
        
        
        
    }

    private void saveMessageInformation() {

        String message=userMessageInput.getText().toString();
        String messagekey=groupNameRefrence.push().getKey();



        if(message.isEmpty()){
            userMessageInput.setError("Write Something");
            userMessageInput.requestFocus();
            return;
        }else{
            //for current date
            Calendar calforDate  =Calendar.getInstance();
            SimpleDateFormat currentDateFormate=new SimpleDateFormat("MMM dd, yyy");
            currentDate=currentDateFormate.format(calforDate.getTime());
             //for current  time
                Calendar calforTime  =Calendar.getInstance();
            SimpleDateFormat currentTimeFormate=new SimpleDateFormat("hh:mm a");
            currentTime=currentTimeFormate.format(calforTime.getTime());


            HashMap<String, Object> groupMessagehas=new HashMap<>();


            groupNameRefrence.updateChildren(groupMessagehas);

            groupMessagekeyref=groupNameRefrence.child(messagekey);

            HashMap<String, Object> messageInfoMap=new HashMap<>();
            messageInfoMap.put("name",currentUsername);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);

        groupMessagekeyref.updateChildren(messageInfoMap);



        }



    }


    private void DisplayMessages(DataSnapshot snapshot) {


        Iterator iterator=snapshot.getChildren().iterator();
        while (iterator.hasNext()){
            String chatDate=(String)  ((DataSnapshot)iterator.next() ).getValue();
            String chatMessage=(String)  ((DataSnapshot)iterator.next() ).getValue();
            String chatName=(String)  ((DataSnapshot)iterator.next() ).getValue();
            String chatTime=(String)  ((DataSnapshot)iterator.next() ).getValue();





            displayTextMessage.append(chatName+ ": \n "+ chatMessage+"\n"+chatTime+"               "+chatDate+"\n\n\n");
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);


        }


    }




}