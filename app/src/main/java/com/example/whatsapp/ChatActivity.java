package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    String saveCurrentTime,saveCurrentDate;

    private Toolbar chatToolbar;
    private  ProgressDialog progressDialog;

    private  ImageButton sendMessageButton,sendFilesButton;
    private  EditText messageInputText;
    private  FirebaseAuth mAuth;
    String messageSenderId;
    private  DatabaseReference rootRef;

    private  final  List<Messages> messagesList=new ArrayList<>();

    private  MessageAdapter adapter;

    private  RecyclerView userMessagesREcyclerview;

    private  String typeOFFile="",imageUri;

    private  Uri fileUri;
    private StorageTask uploadTask;



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


        sendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    CharSequence options[]=new CharSequence[]{
                            "Image",
                            "PDF File",
                            "Ms Word Files"
                    };

                    AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this);
                    builder.setTitle("Choose an option..");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                if(which==0){
                                        typeOFFile="image";
                                        Intent intent=new Intent();
                                        intent.setAction(Intent.ACTION_GET_CONTENT);
                                        intent.setType("image/*");
                                       startActivityForResult(intent.createChooser(intent,"Select  Your Image"),438);




                                }else if(which==1){
                                        typeOFFile="pdf";
                                    Intent intent=new Intent();
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    intent.setType("application/pdf");
                                    startActivityForResult(intent.createChooser(intent,"Select  PDF File"),438);


                                }else{
                                        typeOFFile="docx";
                                    Intent intent=new Intent();
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    intent.setType("application/*");
                                    startActivityForResult(intent.createChooser(intent,"Select  Ms Word File"),438);


                                }
                        }
                    });
                    builder.show();


            }
        });
        DisplaylastSeen();








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
//I am addded an comment

        sendMessageButton=findViewById(R.id.send_message_btn);
        messageInputText=findViewById(R.id.input_message);

        sendFilesButton=findViewById(R.id.send_files_btn);

        adapter=new MessageAdapter(messagesList);
        userMessagesREcyclerview=findViewById(R.id.private_message_list_of_users);
        userMessagesREcyclerview.setHasFixedSize(true);
        userMessagesREcyclerview.setLayoutManager(new LinearLayoutManager(this));

        userMessagesREcyclerview.setAdapter(adapter);






    }


    private  void sendmessage(){


      Calendar calendar=Calendar.getInstance();

        SimpleDateFormat currentDate=new SimpleDateFormat("MM dd,yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");
        saveCurrentTime=currentTime.format(calendar.getTime());





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
                    messageTextBody.put("to",messageReciverId);
                    messageTextBody.put("messageId",messagePushID);
                    messageTextBody.put("time",saveCurrentTime);
                    messageTextBody.put("date",saveCurrentDate);





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


        rootRef.child("Messages").child(messageSenderId).child(messageReciverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                messagesList.clear();
               for (DataSnapshot snapshot: snapshot1.getChildren()){
                   Messages messages=snapshot.getValue(Messages.class);
                   messagesList.add(messages);
                   adapter.notifyDataSetChanged();
                   userMessagesREcyclerview.smoothScrollToPosition(userMessagesREcyclerview.getAdapter().getItemCount());

               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


/*

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
        });*/


    }

    private  void DisplaylastSeen(){
            rootRef.child("Users").child(messageSenderId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.child("userState").hasChild("state")){
                        String state=snapshot.child("userState").child("state").getValue().toString();
                        String date=snapshot.child("userState").child("date").getValue().toString();
                        String time=snapshot.child("userState").child("time").getValue().toString();

                        if(state.equals("online")){
                           userLastseen.setText("online");

                        }else if(state.equals("offline")){
                            userLastseen.setText("Last seen : "+date+"      "+time);

                        }




                    }else{
                        userLastseen.setText("offline");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==438 && resultCode==RESULT_OK && data.getData()!=null){

            progressDialog=new ProgressDialog(ChatActivity.this);
            progressDialog.setTitle("Sending Image..");
            progressDialog.setMessage("Please Wait");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


            fileUri=data.getData();

            if(!typeOFFile.equals("image")){
                StorageReference storageReference=FirebaseStorage.getInstance().getReference().child("DocumentFiles");

                final String messageSEnderRef="Messages/"+messageSenderId+"/"+messageReciverId;
                final String messageREciverREf="Messages/"+messageReciverId+"/"+messageSenderId;

                final DatabaseReference  userMessagekeyRef=rootRef.child("Messages")
                        .child(messageSenderId).child(messageReciverId).push();

                String messagePushID=userMessagekeyRef.getKey();
                final StorageReference filePath=storageReference.child(messagePushID+"."+typeOFFile);


                uploadTask=filePath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uri=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uri.isSuccessful());
                        Uri url=uri.getResult();

                        String messagePushID=userMessagekeyRef.getKey();
                        Map messageTextBody=new HashMap();
                        messageTextBody.put("message",url.toString());
                        messageTextBody.put("name",fileUri.getLastPathSegment());
                        messageTextBody.put("from",messageSenderId);
                        messageTextBody.put("type",typeOFFile);
                        messageTextBody.put("to",messageReciverId);
                        messageTextBody.put("messageId",messagePushID);
                        messageTextBody.put("time",saveCurrentTime);
                        messageTextBody.put("date",saveCurrentDate);

                        Map messageBodyDetails=new HashMap();
                        messageBodyDetails.put(messageSEnderRef+"/"+messagePushID,messageTextBody);
                        messageBodyDetails.put(messageREciverREf+"/"+messagePushID,messageTextBody);

                        rootRef.updateChildren(messageBodyDetails).
                            addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    progressDialog.dismiss();
                                    Toast.makeText(ChatActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(ChatActivity.this, "Something problem", Toast.LENGTH_SHORT).show();

                            }
                        });




                    }
                });















            }else if(typeOFFile.equals("image")){
                StorageReference storageReference=FirebaseStorage.getInstance().getReference().child("messageImage");


                String messageSEnderRef="Messages/"+messageSenderId+"/"+messageReciverId;
                String messageREciverREf="Messages/"+messageReciverId+"/"+messageSenderId;
                DatabaseReference  userMessagekeyRef=rootRef.child("Messages")
                        .child(messageSenderId).child(messageReciverId).push();

                String messagePushID=userMessagekeyRef.getKey();
                final StorageReference filePath=storageReference.child(messagePushID+".jpg");


                uploadTask=filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if(!task.isSuccessful()){
                            Toast.makeText(ChatActivity.this, "Error: "+task.getException(), Toast.LENGTH_SHORT).show();
                        }




                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>(){
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Uri downloadUri=task.getResult();
                            imageUri=downloadUri.toString();
                        sendImage(imageUri);






                        }
                    }
                });




            }else{
                Toast.makeText(this, "No Selected Image is here.", Toast.LENGTH_SHORT).show();
            }





        }











    }


    public void sendImage(String image){

        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat currentDate=new SimpleDateFormat("MM dd,yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");
        saveCurrentTime=currentTime.format(calendar.getTime());

            String messageSEnderRef="Messages/"+messageSenderId+"/"+messageReciverId;
            String messageREciverREf="Messages/"+messageReciverId+"/"+messageSenderId;
            DatabaseReference  userMessagekeyRef=rootRef.child("Messages")
                    .child(messageSenderId).child(messageReciverId).push();

            String messagePushID=userMessagekeyRef.getKey();
            Map messageTextBody=new HashMap();
            messageTextBody.put("message",image);
            messageTextBody.put("name",fileUri.getLastPathSegment());
            messageTextBody.put("from",messageSenderId);
            messageTextBody.put("type",typeOFFile);
            messageTextBody.put("to",messageReciverId);
            messageTextBody.put("messageId",messagePushID);
            messageTextBody.put("time",saveCurrentTime);
            messageTextBody.put("date",saveCurrentDate);





            Map messageBodyDetails=new HashMap();
            messageBodyDetails.put(messageSEnderRef+"/"+messagePushID,messageTextBody);
            messageBodyDetails.put(messageREciverREf+"/"+messagePushID,messageTextBody);

            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        Toast.makeText(ChatActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();

                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(ChatActivity.this, "Something problem", Toast.LENGTH_SHORT).show();
                    }

                }
            });






    }
}