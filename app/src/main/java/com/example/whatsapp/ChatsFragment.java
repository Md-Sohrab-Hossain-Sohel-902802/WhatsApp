package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragment extends Fragment {



    private  View view;

    private  RecyclerView chatList;
    private  FirebaseAuth mAuth;
    private  DatabaseReference chatsRef,userRef;
    private  String currentUserid;

    public ChatsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       view= inflater.inflate(R.layout.fragment_chats, container, false);

       mAuth=FirebaseAuth.getInstance();
       currentUserid=mAuth.getCurrentUser().getUid();


       chatsRef= FirebaseDatabase.getInstance().getReference().child("contacts").child(currentUserid);
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");

       chatList=view.findViewById(R.id.chats_list_REcyclerview);
       chatList.setHasFixedSize(true);
       chatList.setLayoutManager(new LinearLayoutManager(getContext()));








       return view;
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Contacts> options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatsRef,Contacts.class)
                .build();



        FirebaseRecyclerAdapter<Contacts,ChatsViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model) {
                    final String userIds=getRef(position).getKey();





                    userRef.child(userIds).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            if(snapshot.exists()){

                                if(snapshot.hasChild("image")){
                                    String ritImage=snapshot.child("image").getValue().toString();

                                    Picasso.get().load(ritImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                                }

                                final String name=snapshot.child("name").getValue().toString();
                                String status=snapshot.child("status").getValue().toString();


                                holder.nameTextview.setText(name);
                                holder.statusTextview.setText("Last Seen : ");

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(getContext(),ChatActivity.class);

                                        intent.putExtra("visit_user_id",userIds);
                                        intent.putExtra("visit_user_name",name);

                                        startActivity(intent);
                                    }
                                });


                            }




                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });







            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                ChatsViewHolder holder=new ChatsViewHolder(view);


                return  holder;
            }
        };

        chatList.setAdapter(adapter);
        adapter.startListening();


    }

    public static  class  ChatsViewHolder extends RecyclerView.ViewHolder{

        TextView nameTextview,statusTextview;
        CircleImageView profileImage;
        ImageView onlineStatus;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextview=itemView.findViewById(R.id.user_profile_name);
            statusTextview=itemView.findViewById(R.id.user_profile_status);
            profileImage=itemView.findViewById(R.id.users_profile_image);
            onlineStatus=itemView.findViewById(R.id.user_online_status);





        }
    }



}