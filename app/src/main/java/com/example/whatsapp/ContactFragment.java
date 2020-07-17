package com.example.whatsapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ContactFragment extends Fragment {

    private  View view;

    private  RecyclerView contactRecyclerview;
    private  DatabaseReference contactsREf,userRef;
    private  String currentUserid;
    private  FirebaseAuth mAuth;




    public ContactFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    view= inflater.inflate(R.layout.fragment_contact, container, false);


        mAuth=FirebaseAuth.getInstance();
        currentUserid=mAuth.getCurrentUser().getUid();
    contactsREf= FirebaseDatabase.getInstance().getReference().child("contacts").child(currentUserid);
    userRef= FirebaseDatabase.getInstance().getReference().child("Users");




    contactRecyclerview=view.findViewById(R.id.contact_list_RecyclerViewid);
    contactRecyclerview.setHasFixedSize(true);
    contactRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        onStart();





    return  view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsREf,Contacts.class)
                    .build();

        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model) {

                String usersId=getRef(position).getKey();

                userRef.child(usersId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(snapshot.hasChild("image")){
                                String image=snapshot.child("image").getValue().toString();
                                String name=snapshot.child("name").getValue().toString();
                                String status=snapshot.child("status").getValue().toString();


                                holder.nameTextview.setText(name);
                                holder.statusTextview.setText(status);
                                Picasso.get().load(image).placeholder(R.drawable.profile_image).into(holder.profileImage);



                            }else{
                                String name=snapshot.child("name").getValue().toString();
                                String status=snapshot.child("status").getValue().toString();


                                holder.nameTextview.setText(name);
                                holder.statusTextview.setText(status);
                                Picasso.get().load(R.drawable.profile_image).into(holder.profileImage);

                            }
                            if(snapshot.child("userState").hasChild("state")){
                                String state=snapshot.child("userState").child("state").getValue().toString();
                                String date=snapshot.child("userState").child("date").getValue().toString();
                                String time=snapshot.child("userState").child("time").getValue().toString();

                                if(state.equals("online")){
                                    holder.statusTextview.setText("online");
                                    holder.onlineStatus.setVisibility(View.VISIBLE);

                                }else if(state.equals("offline")){
                                    holder.statusTextview.setText("Last seen : "+date+"      "+time);

                                }




                            }else{
                                holder.statusTextview.setText("offline");
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
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);

                ContactsViewHolder contactsViewHolder=new ContactsViewHolder(view);

                 return  contactsViewHolder;

            }
        };


        contactRecyclerview.setAdapter(adapter);
        adapter.startListening();

    }

   public static  class  ContactsViewHolder extends  RecyclerView.ViewHolder {

       TextView nameTextview,statusTextview;
       CircleImageView profileImage;
       ImageView onlineStatus;



       public ContactsViewHolder(@NonNull View itemView) {
           super(itemView);



           nameTextview=itemView.findViewById(R.id.user_profile_name);
           statusTextview=itemView.findViewById(R.id.user_profile_status);
           profileImage=itemView.findViewById(R.id.users_profile_image);
           onlineStatus=itemView.findViewById(R.id.user_online_status);



       }
   }




}