package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class findFriendsActivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private RecyclerView recycleviewfriends ;
    private DatabaseReference usersRef ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        usersRef = FirebaseDatabase.getInstance().getReference().child("User");

        recycleviewfriends=(RecyclerView)findViewById(R.id.recyleviewfriends);
        recycleviewfriends.setLayoutManager(new LinearLayoutManager(this));

        mtoolbar = (Toolbar)findViewById(R.id.fiendfriendstoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//add back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("find friends");


    }


    @Override
    protected void onStart(){

        super.onStart();
        FirebaseRecyclerOptions<contacts> options = new FirebaseRecyclerOptions.Builder<contacts>()
                .setQuery(usersRef,contacts.class)
                .build();
        FirebaseRecyclerAdapter<contacts,findfriendsviewholder> adapter = new FirebaseRecyclerAdapter<contacts, findfriendsviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull findfriendsviewholder holder, final int position, @NonNull contacts model) {
                holder.userName.setText(model.getName());
                holder.userStatus.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileimage);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visited_user_id =getRef(position).getKey();
                        Intent profileintent = new Intent(findFriendsActivity.this,profileActivity.class);
                        profileintent.putExtra("visited_user_id",visited_user_id);
                        startActivity(profileintent);
                    }
                });

            }

            @NonNull
            @Override
            public findfriendsviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                findfriendsviewholder viewholder = new findfriendsviewholder(view);
                return viewholder;
            }
        };
        recycleviewfriends.setAdapter(adapter);
        adapter.startListening();

    }
    private static class findfriendsviewholder extends RecyclerView.ViewHolder{
        TextView userName,userStatus ;
        CircleImageView profileimage ;
        public findfriendsviewholder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userprofilename);
            userStatus = itemView.findViewById(R.id.userstatus);
            profileimage = itemView.findViewById(R.id.usersprofileimage);
        }
    }

    }
