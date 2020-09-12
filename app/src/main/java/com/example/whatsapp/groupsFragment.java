package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.Set;


public class groupsFragment extends Fragment {
    private View goupfragmentview ;
    private ListView list_view ;
    private ArrayAdapter<String> arrayAdapter ;
    private ArrayList<String> List_of_group = new ArrayList<>();
    private DatabaseReference groupref ;


    public groupsFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        goupfragmentview=inflater.inflate(R.layout.fragment_groups, container, false);
        InitializeFields();
        groupref = FirebaseDatabase.getInstance().getReference().child("groups");
        retrivegroups();
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String crurentGroupname = parent.getItemAtPosition(position).toString();
                Intent goupchat = new Intent(getContext(),GroupChatActivity.class);
                goupchat.putExtra("groupName",crurentGroupname);
                startActivity(goupchat);

            }
        });
        return goupfragmentview ;
    }
    public void InitializeFields(){
        list_view=(ListView)goupfragmentview.findViewById(R.id.list_View);
        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,List_of_group);
        list_view.setAdapter(arrayAdapter);
    }
    public void retrivegroups(){
       groupref.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               Set<String> set = new HashSet<>();
               Iterator iterator = dataSnapshot.getChildren().iterator() ;
               while (iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());
               }
               List_of_group.clear();
               List_of_group.addAll(set);
               arrayAdapter.notifyDataSetChanged();


               }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    }
