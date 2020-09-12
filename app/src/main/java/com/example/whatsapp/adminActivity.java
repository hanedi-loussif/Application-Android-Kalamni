package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class adminActivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private RecyclerView recycleviewadmin ;
    private DatabaseReference usersRef,userref;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        usersRef = FirebaseDatabase.getInstance().getReference().child("User");
        recycleviewadmin=(RecyclerView)findViewById(R.id.recyleviewadmin);
        recycleviewadmin.setLayoutManager(new LinearLayoutManager(this));
        mtoolbar = (Toolbar)findViewById(R.id.admintoolbar);
        setSupportActionBar(mtoolbar);

        getSupportActionBar().setTitle("admin");
        userref= FirebaseDatabase.getInstance().getReference().child("User");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<contacts> options = new FirebaseRecyclerOptions.Builder<contacts>()
                .setQuery(usersRef,contacts.class)
                .build();
        FirebaseRecyclerAdapter<contacts,adminviewholder> adapter = new FirebaseRecyclerAdapter<contacts, adminviewholder>(options) {
            @NonNull
            @Override
            public adminviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                adminviewholder viewholder = new adminviewholder(view);
                return viewholder;

            }

            @Override
            protected void onBindViewHolder(@NonNull adminviewholder holder, final int i, @NonNull contacts model) {
                holder.userName.setText(model.getName());
                holder.userStatus.setText(model.getStatus());
                holder.deletebutton.findViewById(R.id.requestcancel).setVisibility(View.VISIBLE);
                holder.deletebutton.setText("Delete");
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileimage);
                holder.deletebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String gettyperef = getRef(i).getKey();
                        usersRef.child(gettyperef).removeValue();
                        Toast.makeText(adminActivity.this, "userdeleted", Toast.LENGTH_SHORT).show();



                    }
                });



            }
        };
        recycleviewadmin.setAdapter(adapter);
        adapter.startListening();
        }


        private static class adminviewholder extends RecyclerView.ViewHolder{
        TextView userName,userStatus ;
        CircleImageView profileimage ;
            Button deletebutton ;

        public adminviewholder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userprofilename);
            userStatus = itemView.findViewById(R.id.userstatus);
            profileimage = itemView.findViewById(R.id.usersprofileimage);
            deletebutton =itemView.findViewById(R.id.requestcancel);

        }
    }
    }


