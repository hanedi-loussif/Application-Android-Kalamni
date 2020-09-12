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
import android.widget.RelativeLayout;
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


public class requestFragment extends Fragment {
    private View requestfriendfragments ;
    private RecyclerView myrequestlist;
    private DatabaseReference requestRef ,userref,contactsRef;
    private String currentuserid ;
    private FirebaseAuth mAuth;


    public requestFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestfriendfragments = inflater.inflate(R.layout.fragment_request, container, false);


        mAuth=FirebaseAuth.getInstance();
       contactsRef= FirebaseDatabase.getInstance().getReference().child("contact");

        currentuserid =mAuth.getCurrentUser().getUid();
        userref= FirebaseDatabase.getInstance().getReference().child("User");

        requestRef= FirebaseDatabase.getInstance().getReference().child("chat request");
        myrequestlist= (RecyclerView)requestfriendfragments.findViewById(R.id.chat_request_list);
        myrequestlist.setLayoutManager(new LinearLayoutManager(getContext()));
        return requestfriendfragments ;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<contacts>()
                .setQuery( requestRef.child(currentuserid), contacts.class)
                .build();
        FirebaseRecyclerAdapter<contacts,requestviewholder> adapter = new FirebaseRecyclerAdapter<contacts, requestviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final requestviewholder holder, int position, @NonNull contacts contacts) {
                holder.itemView.findViewById(R.id.requestaccept).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.requestcancel).setVisibility(View.VISIBLE);
                final String listuserid =getRef(position).getKey();
                DatabaseReference gettyperef = getRef(position).child("request_type").getRef();
                gettyperef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ( dataSnapshot.exists()) {
                            String type = dataSnapshot.getValue().toString();
                            if ( type.equals("recieved")) {
                                userref.child(listuserid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if ( dataSnapshot.hasChild("image")) {
                                            final String requestuserimage =dataSnapshot.child("image").getValue().toString();
                                            Picasso.get().load(requestuserimage).placeholder(R.drawable.profile_image).into(holder.profileimagea);


                                        }
                                        final String requestusername =dataSnapshot.child("name").getValue().toString();
                                        final String requeststqtus= dataSnapshot.child("status").getValue().toString();
                                        holder.userStatusa.setText(requeststqtus);

                                        holder.userNamea.setText(requestusername);

                                        holder.itemView.setOnClickListener(new View.OnClickListener()
                                        {

                                            @Override
                                            public void onClick(View v)
                                            {
                                                CharSequence options []=new CharSequence[] {
                                                        "Accept",
                                                        "Cancel"
                                                };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle(requestusername+"chat Request");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int i) {
                                                        if(i == 0){
                                                            contactsRef.child(currentuserid).child(listuserid).child("contact")
                                                                    .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        contactsRef.child(listuserid).child(currentuserid).child("contact")
                                                                                .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    requestRef.child(currentuserid).child(listuserid).removeValue()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if (task.isSuccessful()) {
                                                                                                        requestRef.child(listuserid).child(currentuserid).removeValue()
                                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                        if (task.isSuccessful()) {
                                                                                                                            Toast.makeText(getContext(), "new contact saved", Toast.LENGTH_SHORT).show();

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

                                                        }
                                                        if(i == 1){
                                                            requestRef.child(currentuserid).child(listuserid).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                requestRef.child(listuserid).child(currentuserid).removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    Toast.makeText(getContext(), "contact deleted", Toast.LENGTH_SHORT).show();

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
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public requestviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                requestviewholder holder = new requestviewholder(view);
                return holder;

            }
        };
        myrequestlist.setAdapter(adapter);
        adapter.startListening();
    }
    public static class requestviewholder extends RecyclerView.ViewHolder{
        TextView userNamea,userStatusa ;
        CircleImageView profileimagea ;
        Button acceptbutton , cancelbutton;
        public requestviewholder(@NonNull View itemView) {
            super(itemView);
            userNamea = itemView.findViewById(R.id.userprofilename);
            userStatusa = itemView.findViewById(R.id.userstatus);
            profileimagea = itemView.findViewById(R.id.usersprofileimage);
            acceptbutton =itemView.findViewById(R.id.requestaccept);
            cancelbutton =itemView.findViewById(R.id.requestcancel);
        }
    }

    }
