package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {


    private  String messageReciverId;
    private  String messageReciverName;

    private  TextView userName,userLastseen;
    private  CircleImageView userImage;
    private String messageReciverImage;

    private Toolbar chatToolbar;

    private  ImageButton sendMessageButton;
    private  EditText messageInputText;
    private  FirebaseAuth mAuth;
    String messageSenderId;
    private  DatabaseReference rootRef;

    private  final  List<Messages> messagesList=new ArrayList<>();

    private  MessageAdapter adapter;

    private  RecyclerView userMessagesREcyclerview;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth=FirebaseAuth.getInstance();
        messageSenderId=mAuth.getCurrentUser().getUid();
        rootRef= FirebaseDatabase.getInstance().getReference();

        messageReciverId=getIntent().getStringExtra("visit_user_id");
        messageReciverName=getIntent().getStringExtra("visit_user_name");
        messageReciverImage=getIntent().getStringExtra("visit_user_image");

        Initialize();

        userName.setText(messageReciverName);
        Picasso.get().load(messageReciverImage).placeholder(R.drawable.profile_image).into(userImage);



        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmessage();
            }
        });










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


        sendMessageButton=findViewById(R.id.send_message_btn);
        messageInputText=findViewById(R.id.input_message);

        adapter=new MessageAdapter(messagesList);
        userMessagesREcyclerview=findViewById(R.id.private_message_list_of_users);
        userMessagesREcyclerview.setHasFixedSize(true);
        userMessagesREcyclerview.setLayoutManager(new LinearLayoutManager(this));

        userMessagesREcyclerview.setAdapter(adapter);






    }


    private  void sendmessage(){
                String messageTExt=messageInputText.getText().toString();
                if(messageTExt.isEmpty()){
                    Toast.makeText(this, "Message Box is empty" , Toast.LENGTH_SHORT).show();
                }else{
                    String messageSEnderRef="Messages/"+messageSenderId+"/"+messageReciverId;
                    String messageREciverREf="Messages/"+messageReciverId+"/"+messageSenderId;
                    DatabaseReference  userMessagekeyRef=rootRef.child("Messages")
                            .child(messageSenderId).child(messageReciverId).push();

                    String messagePushID=userMessagekeyRef.getKey();
                    Map messageTextBody=new HashMap();
                    messageTextBody.put("message",messageTExt);
                    messageTextBody.put("from",messageSenderId);
                    messageTextBody.put("type","text");

                    Map messageBodyDetails=new HashMap();
                    messageBodyDetails.put(messageSEnderRef+"/"+messagePushID,messageTextBody);
                     messageBodyDetails.put(messageREciverREf+"/"+messagePushID,messageTextBody);

                     rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                         @Override
                         public void onComplete(@NonNull Task task) {

                             if(task.isSuccessful()){

                                 Toast.makeText(ChatActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();

                             }else{
                                 Toast.makeText(ChatActivity.this, "Something problem", Toast.LENGTH_SHORT).show();
                             }
                             messageInputText.setText("");



                         }
                     });


                }


    }

    @Override
    protected void onStart() {
        super.onStart();

        messagesList.clear();

        rootRef.child("Messages").child(messageSenderId).child(messageReciverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Messages messages=snapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        adapter.notifyDataSetChanged();
                          userMessagesREcyclerview.smoothScrollToPosition(userMessagesREcyclerview.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
}