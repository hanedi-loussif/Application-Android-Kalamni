package com.example.whatsapp;

import android.content.Intent;
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


public class chatsFragment extends Fragment {
    private View chatfragmentlayout ;
    private RecyclerView chatlisterecyclefrag ;
    DatabaseReference chatcontactref ,userref;
    private String currentuserid ;
    private FirebaseAuth mAuth;
    private String requestuserimage ;


    public chatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chatfragmentlayout =inflater.inflate(R.layout.fragment_chats, container, false);
        mAuth=FirebaseAuth.getInstance();
        currentuserid =mAuth.getCurrentUser().getUid();


        userref= FirebaseDatabase.getInstance().getReference().child("User");

        chatlisterecyclefrag= (RecyclerView)chatfragmentlayout.findViewById(R.id.chat_list_ac);
        chatlisterecyclefrag.setLayoutManager(new LinearLayoutManager(getContext()));

        chatcontactref = FirebaseDatabase.getInstance().getReference().child("contact").child(currentuserid);



        return chatfragmentlayout ;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseRecyclerOptions<contacts> options = new FirebaseRecyclerOptions.Builder<contacts>()
                .setQuery(chatcontactref ,contacts.class)
                .build();
        FirebaseRecyclerAdapter<contacts,chatviewholder> adapter = new FirebaseRecyclerAdapter<contacts, chatviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final chatviewholder holder, int i, @NonNull contacts contacts) {
                final String usersId =getRef(i).getKey();
                userref.child(usersId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ( dataSnapshot.exists()) {
                            if ( dataSnapshot.hasChild("image")) {
                                requestuserimage = dataSnapshot.child("image").getValue().toString();
                                Picasso.get().load(requestuserimage).placeholder(R.drawable.profile_image).into(holder.profileimagea);

                            }
                            final String requestusername =dataSnapshot.child("name").getValue().toString();
                            final String requeststqtus= dataSnapshot.child("status").getValue().toString();

                            holder.userStatusa.setText("last seen :"+ " \n"+ "date"+"  time");

                            holder.userNamea.setText(requestusername);
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent chatintent =  new Intent(getContext(),chatActivity.class);
                                    chatintent.putExtra("visited_user_id",usersId);
                                    chatintent.putExtra("visited_user_name",requestusername);
                                    chatintent.putExtra("visited_image ",requestuserimage);
                                    startActivity(chatintent);
                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public chatviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                return new chatviewholder(view) ;
            }
        };
        chatlisterecyclefrag.setAdapter(adapter);
        adapter.startListening();
    }
    public static class chatviewholder extends RecyclerView.ViewHolder {
        TextView userNamea,userStatusa ;
        CircleImageView profileimagea ;

        public chatviewholder(@NonNull View itemView) {
            super(itemView);
            userNamea = itemView.findViewById(R.id.userprofilename);
            userStatusa = itemView.findViewById(R.id.userstatus);
            profileimagea = itemView.findViewById(R.id.usersprofileimage);
        }
    }

    }
