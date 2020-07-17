package com.example.whatsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    private  FirebaseAuth mAuth;
    private  DatabaseReference userRef;



    private List<Messages> userMessageList;

    public MessageAdapter(List<Messages> userMessageList) {
        this.userMessageList = userMessageList;




    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_layout, parent, false);

      mAuth=FirebaseAuth.getInstance();


        MessageViewHolder holder=new MessageViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {
                String messageSenderid=mAuth.getCurrentUser().getUid();
                Messages messages=userMessageList.get(position);
                String fromUserId=messages.getFrom();
                final String fromMessageType=messages.getType();

                userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("image")){
                            String  reciverImage=snapshot.child("image").getValue().toString();
                            Picasso.get().load(reciverImage).placeholder(R.drawable.profile_image).into(holder.reciverProfileImage);
                        }else{
                            Picasso.get().load(R.drawable.profile_image).into(holder.reciverProfileImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.receiverMessageTExt.setVisibility(View.GONE);
        holder.reciverProfileImage.setVisibility(View.GONE);
        holder.senderMessageTExt.setVisibility(View.GONE);
        holder.messageSenderPicture.setVisibility(View.GONE);
        holder.messageReceiverPicture.setVisibility(View.GONE);


        if(fromMessageType.equals("text")){

                        if(fromUserId.equals(messageSenderid)){
                            holder.senderMessageTExt.setVisibility(View.VISIBLE);
                            holder.senderMessageTExt.setBackgroundResource(R.drawable.sender_messages_layout);
                            holder.senderMessageTExt.setText(messages.getMessage()+"\n\n"+ messages.getTime()+" - "+messages.getDate());
                        }else{


                            holder.reciverProfileImage.setVisibility(View.VISIBLE);
                            holder.receiverMessageTExt.setVisibility(View.VISIBLE);

                            holder.receiverMessageTExt.setBackgroundResource(R.drawable.recever_message_layout);
                            holder.receiverMessageTExt.setText(messages.getMessage()+"\n\n"+ messages.getTime()+" - "+messages.getDate());

                        }


            }      else if(fromMessageType.equals("image")){

                        if(fromUserId.equals(messageSenderid)){
                            holder.messageSenderPicture.setVisibility(View.VISIBLE);
                            Picasso.get().load(messages.getMessage()).into(holder.messageSenderPicture);









                       }else{
                            holder.reciverProfileImage.setVisibility(View.VISIBLE);

                            holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                            Picasso.get().load(messages.getMessage()).into(holder.messageReceiverPicture);


                        }


            }  else if(fromMessageType.equals("pdf") || fromMessageType.equals("docx")){


            if(fromUserId.equals(messageSenderid)){
                holder.messageSenderPicture.setVisibility(View.VISIBLE);

                holder.messageSenderPicture.setBackgroundResource(R.drawable.file);
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/allishere-e08e7.appspot.com/o/file.png?alt=media&token=b7c0d176-a32e-41e5-a107-89a62339d76b")
                        .into(holder.messageReceiverPicture);


            }else{
                holder.reciverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setBackgroundResource(R.drawable.file);
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/allishere-e08e7.appspot.com/o/file.png?alt=media&token=b7c0d176-a32e-41e5-a107-89a62339d76b")
                        .into(holder.messageReceiverPicture);
            }

        }






        if(fromUserId.equals(messageSenderid)){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        if(userMessageList.get(position).getType().equals("pdf") || userMessageList.get(position).getType().equals("docx") ){
                                    CharSequence options[]=new CharSequence[]{

                                                "Delete for me",
                                                "Download and View This Document",
                                                "Cancle",
                                                "Delete For Everyone"

                                    };


                                    AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                                    builder.setTitle("Delete Message?");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                                if(which ==0){
                                                    deletesentMessages(position,holder);
                                                }else if(which==1){
                                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(userMessageList.get(position).getMessage()));
                                                    holder.itemView.getContext().startActivity(intent);
                                                }else if(which==2){

                                                }else  if(which==3){
                                                    deleteMessageForEveryone(position,holder);
                                                }
                                        }
                                    });
                                    builder.show();



                        }else  if(fromMessageType.equals("image")){
                            CharSequence options[]=new CharSequence[]{

                                    "Delete for me",
                                    "View This Image",
                                    "Cancle",
                                    "Delete For Everyone"

                            };


                            AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setTitle("Delete Message?");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which==0){
                                        deletesentMessages(position,holder);
                                    }else if(which==1){


                                        Intent intent=new Intent(holder.itemView.getContext(),ImageViewerActivity.class);
                                        intent.putExtra("image",userMessageList.get(position).getMessage());
                                        holder.itemView.getContext().startActivity(intent);


                                  }else if(which==2){

                                    }else  if(which==3){
                                        deleteMessageForEveryone(position,holder);
                                    }
                                }
                            });
                            builder.show();
                        }else  if(fromMessageType.equals("text")){
                            CharSequence options[]=new CharSequence[]{

                                    "Delete for me",
                                     "Cancle",
                                    "Delete For Everyone"

                            };


                            AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setTitle("Delete Message?");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which==0){
                                            deletesentMessages(position,holder);
                                    }else  if(which==2){
                                        deleteMessageForEveryone(position,holder);
                                    }
                                }
                            });
                            builder.show();
                        }
                }
            });
        }
        else{
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        if(userMessageList.get(position).getType().equals("pdf") || userMessageList.get(position).getType().equals("docx") ){
                                    CharSequence options[]=new CharSequence[]{

                                                "Delete for me",
                                                "Download and View This Document",
                                                "Cancle",
                                                "Delete For Everyone"

                                    };


                                    AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                                    builder.setTitle("Delete Message?");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                                if(which==0){
                                                    deleteRecivedMessages(position,holder);
                                                }else if(which==1){
                                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(userMessageList.get(position).getMessage()));
                                                    holder.itemView.getContext().startActivity(intent);
                                                }else if(which==2){

                                                }else  if(which==3){
                                                        deleteMessageForEveryone(position,holder);
                                                }
                                        }
                                    });
                                    builder.show();



                        }else  if(fromMessageType.equals("image")){
                            CharSequence options[]=new CharSequence[]{

                                    "Delete for me",
                                    "View This Image",
                                    "Cancle",
                                    "Delete For Everyone"

                            };


                            AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setTitle("Delete Message?");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which==0){
                                        deleteRecivedMessages(position,holder);
                                    }else if(which==1){

                                        Intent intent=new Intent(holder.itemView.getContext(),ImageViewerActivity.class);
                                        intent.putExtra("image",userMessageList.get(position).getMessage());
                                        holder.itemView.getContext().startActivity(intent);



                                  }else if(which==2){

                                    }else  if(which==3){
                                        deleteMessageForEveryone(position,holder);
                                    }
                                }
                            });
                            builder.show();
                        }else  if(fromMessageType.equals("text")){
                            CharSequence options[]=new CharSequence[]{

                                    "Delete for me",
                                     "Cancle",
                                    "Delete For Everyone"

                            };


                            AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setTitle("Delete Message?");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which==0){
                                        deleteRecivedMessages(position,holder);

                                    }else  if(which==2){
                                        deleteMessageForEveryone(position,holder);
                                    }
                                }
                            });
                            builder.show();
                        }
                }
            });
        }








    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }


    public class MessageViewHolder  extends  RecyclerView.ViewHolder{


        public  TextView senderMessageTExt,receiverMessageTExt;
        public  CircleImageView reciverProfileImage;
        public  ImageView messageSenderPicture,messageReceiverPicture;




        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);


            senderMessageTExt=itemView.findViewById(R.id.sender_message_Text);
            receiverMessageTExt=itemView.findViewById(R.id.receiver_message_TExt);
            reciverProfileImage=itemView.findViewById(R.id.message_profile_Image);
            messageReceiverPicture=itemView.findViewById(R.id.message_receiver_Imageview);
            messageSenderPicture=itemView.findViewById(R.id.message_sender_Imageview);




        }
    }




    private  void  deletesentMessages(final int position,final MessageViewHolder holder){

        DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages").child(userMessageList.get(position).getFrom())
                .child(userMessageList.get(position).getTo())
                .child(userMessageList.get(position).getMessageId())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                }
                else if(task.isSuccessful()){
                    Toast.makeText(holder.itemView.getContext(), "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });



    }



    private  void  deleteRecivedMessages(final int position,final MessageViewHolder holder){

        DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages").child(userMessageList.get(position).getTo())
                .child(userMessageList.get(position).getFrom())
                .child(userMessageList.get(position).getMessageId())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                }
                else if(task.isSuccessful()){
                    Toast.makeText(holder.itemView.getContext(), "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });



    }


    private  void  deleteMessageForEveryone(final int position,final MessageViewHolder holder){

        final DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages").child(userMessageList.get(position).getTo())
                .child(userMessageList.get(position).getFrom())
                .child(userMessageList.get(position).getMessageId())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    rootRef.child("Messages").child(userMessageList.get(position).getFrom())
                            .child(userMessageList.get(position).getTo())
                            .child(userMessageList.get(position).getMessageId())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(holder.itemView.getContext(), "Deleted Messages from everyone", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else if(task.isSuccessful()){
                    Toast.makeText(holder.itemView.getContext(), "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });



    }


}
