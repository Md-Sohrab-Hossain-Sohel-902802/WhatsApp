package com.example.whatsapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GroupsFragment extends Fragment {

    private  View view;
    private ListView listview;
    private  ArrayAdapter <String> arrayAdapter;
    private  ArrayList<String> list_of_groups=new ArrayList<>();

    private  DatabaseReference groupRef;

    public GroupsFragment() {


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view= inflater.inflate(R.layout.fragment_groups, container, false);

         groupRef= FirebaseDatabase.getInstance().getReference().child("Groups");

         InitialzeFields();


         REtriveAndDisplayGroups();







         return  view;
    }


    private void InitialzeFields() {


        listview=view.findViewById(R.id.list_view);
        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list_of_groups);
        listview.setAdapter(arrayAdapter);




    }


    private void REtriveAndDisplayGroups() {
            groupRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Set<String> set=new HashSet<>();

                            Iterator iterator=snapshot.getChildren().iterator();

                            while (iterator.hasNext()){{
                                    set.add(((DataSnapshot) iterator.next()).getKey());
                            }

                            list_of_groups.clear();

                                list_of_groups.addAll(set);
                                arrayAdapter.notifyDataSetChanged();
                            }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


    }

}