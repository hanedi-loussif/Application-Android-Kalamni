package com.example.whatsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.messageviewholder> {
    private List<message> usermessageslisst ;
    private FirebaseAuth mAuth ;
    private DatabaseReference userRef ;
    public  messageAdapter (List<message> usermessageslisst){
        this.usermessageslisst =usermessageslisst;

    }
    public class messageviewholder extends RecyclerView.ViewHolder {
        public TextView sendermessagetext , receivermessagetext;
        public CircleImageView receiverimageprofile;

        public messageviewholder(@NonNull View itemView) {
            super(itemView);
            sendermessagetext= (TextView)itemView.findViewById(R.id.sender_message_text);
            receivermessagetext =(TextView)itemView.findViewById(R.id.receiver_message_text);
            receiverimageprofile = (CircleImageView)itemView.findViewById(R.id.profilecustomimage);


        }
    }

    @NonNull
    @Override
    public messageviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layout,parent,false);
        mAuth = FirebaseAuth.getInstance();
        return new messageviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final messageviewholder messageviewholder, int position) {
        String messagesenderid = mAuth.getCurrentUser().getUid();
        message message = usermessageslisst.get(position);
        String fromuserid = message.getFrom();
        String frommessagetype= message.getType();
        userRef= FirebaseDatabase.getInstance().getReference().child("User").child(fromuserid);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ( dataSnapshot.hasChild("image")) {
                    String receiverimage = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(receiverimage).placeholder(R.drawable.profile_image).into(messageviewholder.receiverimageprofile);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {



            }
        });
        if (frommessagetype.equals("text")) {
            messageviewholder.receivermessagetext.setVisibility(View.INVISIBLE);
            messageviewholder.receiverimageprofile.setVisibility(View.INVISIBLE);
            messageviewholder.sendermessagetext.setVisibility(View.INVISIBLE);
            if (fromuserid.equals(messagesenderid)){
                messageviewholder.sendermessagetext.setVisibility(View.VISIBLE);
                messageviewholder.sendermessagetext.setBackgroundResource(R.drawable.sender_messages_layout);
                messageviewholder.sendermessagetext.setText(message.getMessages());
            }
            else {

                messageviewholder.receiverimageprofile.setVisibility(View.VISIBLE);
                messageviewholder.receivermessagetext.setVisibility(View.VISIBLE);
                messageviewholder.receivermessagetext.setBackgroundResource(R.drawable.receiver_massege_layout);
                messageviewholder.receivermessagetext.setText(message.getMessages());


            }




            }


        }

    @Override
    public int getItemCount() {
        return usermessageslisst.size();
    }




    }
