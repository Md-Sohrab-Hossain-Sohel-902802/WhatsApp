package com.example.whatsapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestFragment extends Fragment {



    View view;
    private RecyclerView myRequestList;
    private  DatabaseReference chatRequestRef,userRef,contactsRef;

    private FirebaseAuth mAuth;
    private  String currentUserid;


    public RequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_request, container, false);


        mAuth=FirebaseAuth.getInstance();
        currentUserid=mAuth.getCurrentUser().getUid();

        chatRequestRef= FirebaseDatabase.getInstance().getReference("chat Request");
        userRef= FirebaseDatabase.getInstance().getReference("Users");
        contactsRef= FirebaseDatabase.getInstance().getReference("contacts");


        myRequestList=view.findViewById(R.id.chat_request_list);
        myRequestList.setHasFixedSize(true);
        myRequestList.setLayoutManager(new LinearLayoutManager(getContext()));







        return  view;

    }


    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Contacts> options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatRequestRef.child(currentUserid),Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,RequestViewholder> adapter=new FirebaseRecyclerAdapter<Contacts, RequestViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestViewholder holder, int position, @NonNull Contacts model) {
                holder.acceptButton.setVisibility(View.VISIBLE);
                holder.cancelButton.setVisibility(View.VISIBLE);


                final  String list_user_id=getRef(position).getKey();


                final DatabaseReference getTypeRef=getRef(position).child("request_type").getRef();
                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String type=snapshot.getValue().toString();
                            if(type.equals("received")){
                                userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.hasChild("image")){
                                            final  String requestUserImage=snapshot.child("image").getValue().toString();
                                            Picasso.get().load(requestUserImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                                        }
                                        Picasso.get().load(R.drawable.profile_image).into(holder.profileImage);

                                        final  String requestUsername=snapshot.child("name").getValue().toString();
                                        final  String requestUserStatus=snapshot.child("status").getValue().toString();

                                        holder.nameTextview.setText(requestUsername);
                                        holder.statusTextview.setText(requestUserStatus);


                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {


                                                CharSequence options[]=new CharSequence[]{
                                                        "Accept",
                                                        "Cancel"
                                                };

                                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                builder.setTitle(requestUsername+" chat Request");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        if(which==0){
                                                            contactsRef.child(currentUserid).child(list_user_id).child("Contact")
                                                                    .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if(task.isSuccessful()){
                                                                        contactsRef.child(list_user_id).child(currentUserid).child("Contact")
                                                                                .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                if(task.isSuccessful()){
                                                                                    chatRequestRef.child(currentUserid).child(list_user_id).removeValue()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if(task.isSuccessful()){
                                                                                                        chatRequestRef.child(list_user_id).child(currentUserid).removeValue()
                                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                        if(task.isSuccessful()){
                                                                                                                            Toast.makeText(getContext(), "New contact  saved", Toast.LENGTH_SHORT).show();
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
                                                        }else if(which==1){
                                                            chatRequestRef.child(currentUserid).child(list_user_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                chatRequestRef.child(list_user_id).child(currentUserid).removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if(task.isSuccessful()){
                                                                                                    Toast.makeText(getContext(), " Chat Request Removed", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    });
                                                        }





                                                    }
                                                });
                                                builder.show();







                                            }
                                        });









                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }else{

                                holder.cancelButton.setVisibility(View.GONE);
                                holder.acceptButton.setText("You Sent Chat Request");
                                holder.acceptButton.setEnabled(false);
                                holder.cancelButton.setEnabled(false);

                                userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.hasChild("image")){

                                            final  String requestUsername=snapshot.child("name").getValue().toString();
                                            final  String requestUserStatus=snapshot.child("status").getValue().toString();
                                            final  String requestUserImage=snapshot.child("image").getValue().toString();



                                            holder.nameTextview.setText(requestUsername);
                                            holder.statusTextview.setText(requestUserStatus);
                                            Picasso.get().load(requestUserImage).into(holder.profileImage);



                                        }else{
                                            final  String requestUsername=snapshot.child("name").getValue().toString();
                                            final  String requestUserStatus=snapshot.child("status").getValue().toString();

                                            holder.nameTextview.setText(requestUsername);
                                            holder.statusTextview.setText(requestUserStatus);
                                            Picasso.get().load(R.drawable.profile_image).into(holder.profileImage);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });










                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });






            }

            @NonNull
            @Override
            public RequestViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);

                RequestViewholder requestViewholder=new RequestViewholder(view);
                return  requestViewholder;


            }
        };

        myRequestList.setAdapter(adapter);
        adapter.startListening();





    }


    public static  class  RequestViewholder extends RecyclerView.ViewHolder{

        TextView nameTextview,statusTextview;
        CircleImageView profileImage;
        ImageView onlineStatus;
        Button acceptButton;
        Button cancelButton;


        public RequestViewholder(@NonNull View itemView) {
            super(itemView);


            nameTextview=itemView.findViewById(R.id.user_profile_name);
            statusTextview=itemView.findViewById(R.id.user_profile_status);
            profileImage=itemView.findViewById(R.id.users_profile_image);
            onlineStatus=itemView.findViewById(R.id.user_online_status);


            acceptButton=itemView.findViewById(R.id.request_accept_button);
            cancelButton=itemView.findViewById(R.id.request_cancle_button);


        }
    }







}