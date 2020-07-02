package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FindFriends extends AppCompatActivity {



    private  RecyclerView findFriendsRecyclerLIst;
    private Toolbar mToolbar;

    private  DatabaseReference usersREf;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);


        usersREf= FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar=findViewById(R.id.find_Friends_Toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("Find Friends");

        findFriendsRecyclerLIst=findViewById(R.id.find_friends_Recycler_List);
        findFriendsRecyclerLIst.setHasFixedSize(true);

        findFriendsRecyclerLIst.setLayoutManager(new LinearLayoutManager(this));









    }


    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Contacts> options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(usersREf, Contacts.class)
                .build();



        FirebaseRecyclerAdapter<Contacts,FindFriendsViewHolder>  adapter=new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, final int position, @NonNull Contacts model) {


                    holder.nameTextview.setText(model.getName());
                    holder.statusTextview.setText(model.getStatus());


                    if(model.getImage()==null){
                        Picasso.get().load(R.drawable.profile_image).into(holder.profileImage);
                    }else{
                        Picasso.get().load(model.getImage()).into(holder.profileImage);
                    }

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                String visit_userid=getRef(position).getKey();
                            Intent intent=new Intent(FindFriends.this,ProfileActivity.class);
                            intent.putExtra("uid",visit_userid);
                            startActivity(intent);



                        }
                    });




            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);

                FindFriendsViewHolder holder=new FindFriendsViewHolder(view);
                return holder;
            }
        };

        findFriendsRecyclerLIst.setAdapter(adapter);
        adapter.startListening();










    }


    public static  class  FindFriendsViewHolder extends RecyclerView.ViewHolder{

         TextView nameTextview,statusTextview;
         CircleImageView profileImage;
         ImageView onlineStatus;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);


            nameTextview=itemView.findViewById(R.id.user_profile_name);
            statusTextview=itemView.findViewById(R.id.user_profile_status);
            profileImage=itemView.findViewById(R.id.users_profile_image);
            onlineStatus=itemView.findViewById(R.id.user_online_status);






        }
    }







}