package com.example.whatsapp;

import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
                String messageSenderid=mAuth.getCurrentUser().getUid();
                Messages messages=userMessageList.get(position);
                String fromUserId=messages.getFrom();
                String fromMessageType=messages.getType();

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

            if(fromMessageType.equals("text")){
                        holder.receiverMessageTExt.setVisibility(View.INVISIBLE);
                        holder.reciverProfileImage.setVisibility(View.INVISIBLE);
                         holder.senderMessageTExt.setVisibility(View.INVISIBLE);


                        if(fromUserId.equals(messageSenderid)){
                            holder.senderMessageTExt.setVisibility(View.VISIBLE);
                            holder.senderMessageTExt.setBackgroundResource(R.drawable.sender_messages_layout);
                            holder.senderMessageTExt.setText(messages.getMessage());
                        }else{


                            holder.reciverProfileImage.setVisibility(View.VISIBLE);
                            holder.receiverMessageTExt.setVisibility(View.VISIBLE);

                            holder.receiverMessageTExt.setBackgroundResource(R.drawable.recever_message_layout);
                            holder.receiverMessageTExt.setText(messages.getMessage());

                        }


            }



    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }


    public class MessageViewHolder  extends  RecyclerView.ViewHolder{


        public  TextView senderMessageTExt,receiverMessageTExt;
        public  CircleImageView reciverProfileImage;



        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);


            senderMessageTExt=itemView.findViewById(R.id.sender_message_Text);
            receiverMessageTExt=itemView.findViewById(R.id.receiver_message_TExt);
            reciverProfileImage=itemView.findViewById(R.id.message_profile_Image);





        }
    }
}
