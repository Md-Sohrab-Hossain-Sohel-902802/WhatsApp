package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {



    private  String reciveUserId,current_State,currentUserid;


    private  CircleImageView userProfileImage;
    private TextView userprofileName,userProfileStatus;
    private  Button sendMessageRequestButton,cancelMessageRequestButton;

    private DatabaseReference userRef,chatRequestRef,contactRef,notificationRef;

    private  FirebaseAuth mAuth;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        reciveUserId=getIntent().getStringExtra("uid");


        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestRef= FirebaseDatabase.getInstance().getReference().child("chat Request");
        contactRef= FirebaseDatabase.getInstance().getReference().child("contacts");
        notificationRef= FirebaseDatabase.getInstance().getReference().child("Notification");

        mAuth=FirebaseAuth.getInstance();
        currentUserid=mAuth.getCurrentUser().getUid();



        userProfileImage=findViewById(R.id.visit_profile_image);
        userprofileName=findViewById(R.id.visit_user_name);
        userProfileStatus=findViewById(R.id.visit_profile_status);
        sendMessageRequestButton=findViewById(R.id.send_message_request_button);
        cancelMessageRequestButton=findViewById(R.id.decline_message_request_button);
        current_State="new";
            RetriveUserInfo();







    }

    @Override
    protected void onStart() {
        super.onStart();





    }

    private void RetriveUserInfo() {

        userRef.child(reciveUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){

                        if(snapshot.hasChild("image")){
                            Picasso.get().load(snapshot.child("image").getValue().toString()).placeholder(R.drawable.profile_image).into(userProfileImage);
                            String name=snapshot.child("name").getValue().toString();
                            String status=snapshot.child("status").getValue().toString();

                            userprofileName.setText(name);
                            userProfileStatus.setText(status);

                            ManageChatRequest();


                        }else{
                            String name=snapshot.child("name").getValue().toString();
                            String status=snapshot.child("status").getValue().toString();

                            userprofileName.setText(name);
                            userProfileStatus.setText(status);
                            ManageChatRequest();

                        }



                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void ManageChatRequest() {



        chatRequestRef.child(currentUserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.hasChild(reciveUserId)){
                            String request_Type=snapshot.child(reciveUserId).child("request_type").getValue().toString();
                            if(request_Type.equals("send")){
                                current_State="request_sent";
                                sendMessageRequestButton.setText("Cancel Chat Request");
                            }  else if(request_Type.equals("received")){
                                current_State="request_received";
                                sendMessageRequestButton.setText("Accept Chat Request");
                                cancelMessageRequestButton.setVisibility(View.VISIBLE);
                                cancelMessageRequestButton.setEnabled(true);
                                cancelMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                            CancleChatRequest();
                                    }
                                });
                            }

                }else{
                            contactRef.child(currentUserid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.hasChild(reciveUserId)){
                                            current_State="friends";
                                            sendMessageRequestButton.setText("Remove this contact");
                                        }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        if(!currentUserid.equals(reciveUserId)){

            sendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                            sendMessageRequestButton.setEnabled(false);

                            if(current_State.equals("new")){

                                sendChatRequest();

                            }if(current_State.equals("request_sent")){
                                CancleChatRequest();
                    }if(current_State.equals("request_received")){
                                AcceptChatRequest();
                    }if(current_State.equals("friends")){
                               RemoveSpecificContacts();
                    }



                }
            });

        }else{


                sendMessageRequestButton.setVisibility(View.INVISIBLE);


        }







    }

    private void RemoveSpecificContacts() {

        contactRef.child(currentUserid).child(reciveUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    contactRef.child(reciveUserId).child(currentUserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                sendMessageRequestButton.setEnabled(true);
                                current_State="new";
                                sendMessageRequestButton.setText("Send Message");

                                cancelMessageRequestButton.setVisibility(View.INVISIBLE);
                                cancelMessageRequestButton.setEnabled(false);

                            }
                        }
                    });
                }
            }
        });




    }

    private void AcceptChatRequest() {

        contactRef.child(currentUserid).child(reciveUserId)
                .child("Contacts").setValue("saved")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                contactRef.child(reciveUserId).child(currentUserid)
                                        .child("Contacts").setValue("saved")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        chatRequestRef.child(currentUserid).child(reciveUserId).removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(task.isSuccessful()){
                                                                            chatRequestRef.child(reciveUserId).child(currentUserid).removeValue()
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if(task.isSuccessful()){
                                                                                                    sendMessageRequestButton.setEnabled(true);
                                                                                                    current_State="friends";
                                                                                                    sendMessageRequestButton.setText("Remove This Contact");
                                                                                                    cancelMessageRequestButton.setEnabled(false);
                                                                                                    cancelMessageRequestButton.setVisibility(View.GONE);
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                });
                                                }
                                            }
                                        });


                            }
                        }
                    });




    }

    private void CancleChatRequest() {

        chatRequestRef.child(currentUserid).child(reciveUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    chatRequestRef.child(reciveUserId).child(currentUserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                    sendMessageRequestButton.setEnabled(true);
                                    current_State="new";
                                    sendMessageRequestButton.setText("Send Message");

                                    cancelMessageRequestButton.setVisibility(View.INVISIBLE);
                                    cancelMessageRequestButton.setEnabled(false);

                            }
                        }
                    });
                }
            }
        });


    }

    private void sendChatRequest() {

        chatRequestRef.child(currentUserid).child(reciveUserId).child("request_type").setValue("send").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                        chatRequestRef.child(reciveUserId).child(currentUserid).child("request_type").setValue("received")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){


                                            HashMap<String, String> chatNotification=new HashMap<>();
                                            chatNotification.put("from",currentUserid);
                                            chatNotification.put("type","request");

                                            notificationRef.child(reciveUserId).push()
                                                    .setValue(chatNotification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        sendMessageRequestButton.setEnabled(true);
                                                        current_State="request_sent";
                                                        sendMessageRequestButton.setText("Cancel chat,Request");
                                                    }
                                                }
                                            });




                                        }
                                    }
                                });

                }
            }
        });



    }
}