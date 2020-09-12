package com.example.whatsapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class contactsFragment extends Fragment {

    private View contactView ;
    private RecyclerView mycontactslist ;
    private DatabaseReference contactsRef, usersref;
    private FirebaseAuth mAuth;
    private String currentuserid ;
    public contactsFragment() {

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactView= inflater.inflate(R.layout.fragment_contacts, container, false);


        usersref= FirebaseDatabase.getInstance().getReference().child("User");
        mAuth=FirebaseAuth.getInstance();
        currentuserid =mAuth.getCurrentUser().getUid();
        contactsRef= FirebaseDatabase.getInstance().getReference().child("contact").child(currentuserid);

        mycontactslist= (RecyclerView)contactView.findViewById(R.id.recylercontact);
        mycontactslist.setLayoutManager(new LinearLayoutManager(getContext()));
        return contactView ;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<contacts>()
                .setQuery(contactsRef,contacts.class)
                .build();
        FirebaseRecyclerAdapter<contacts,contactsviewholder> adapters = new FirebaseRecyclerAdapter<contacts, contactsviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final contactsviewholder holder, int i, @NonNull contacts contacts) {
            String usersId = getRef(i).getKey();
            usersref.child(usersId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if ( dataSnapshot.hasChild("image")) {
                        String userimage = dataSnapshot.child("image").getValue().toString();
                        String profilestatus = dataSnapshot.child("status").getValue().toString();
                        String profilename = dataSnapshot.child("name").getValue().toString();
                        Picasso.get().load(userimage).placeholder(R.drawable.profile_image).into(holder.profileimagess);
                        holder.userNames.setText(profilename);
                        holder.userStatuss.setText(profilestatus);

                    }
                    else {

                        String profilestatus = dataSnapshot.child("status").getValue().toString();
                        String profilename = dataSnapshot.child("name").getValue().toString();
                        holder.userNames.setText(profilename);
                        holder.userStatuss.setText(profilestatus);

                    };




                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            }

            @NonNull
            @Override
            public contactsviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                contactsviewholder viewholder = new contactsviewholder(view);
                return viewholder;

            }
        };
        mycontactslist.setAdapter(adapters);
        adapters.startListening();
    }





    public static class contactsviewholder extends RecyclerView.ViewHolder{
        TextView userNames,userStatuss ;
        CircleImageView profileimagess ;
        public contactsviewholder(@NonNull View itemView) {
            super(itemView);
            userNames = itemView.findViewById(R.id.userprofilename);
            userStatuss = itemView.findViewById(R.id.userstatus);
            profileimagess = itemView.findViewById(R.id.usersprofileimage);
        }
    }

    }
