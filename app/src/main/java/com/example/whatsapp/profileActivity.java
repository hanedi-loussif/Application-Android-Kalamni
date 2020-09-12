package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

public class profileActivity extends AppCompatActivity {
    private String reciveruserid ,currentuserid,currentstate;
    private CircleImageView userprolifeimage ;
    private TextView userprofilename , userprofilestatus ;
    private Button sendmessagerequest ,declinemessagerequestButton;
    private DatabaseReference userRef,chatrequestref, contactref ;
    private FirebaseAuth mAuth ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth=FirebaseAuth.getInstance();
        userRef= FirebaseDatabase.getInstance().getReference().child("User");
        chatrequestref= FirebaseDatabase.getInstance().getReference().child("chat request");
        contactref= FirebaseDatabase.getInstance().getReference().child("contact");
        reciveruserid =getIntent().getExtras().get("visited_user_id").toString();
        currentuserid=mAuth.getCurrentUser().getUid();



        userprolifeimage =(CircleImageView)findViewById(R.id.visit_profileimage);
        userprofilename=(TextView)findViewById(R.id.viset_username);
        userprofilestatus=(TextView)findViewById(R.id.viset_userstatus);
        sendmessagerequest =(Button)findViewById(R.id.send_message_request);
        declinemessagerequestButton =(Button)findViewById(R.id.get_message_request);
        currentstate = "new";


       retriveuseinfo();

    }

    private void retriveuseinfo() {
        userRef.child(reciveruserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("image")) {
                    String userimage = dataSnapshot.child("image").getValue().toString();
                    String userstatus = dataSnapshot.child("status").getValue().toString();
                    String username = dataSnapshot.child("name").getValue().toString();
                    Picasso.get().load(userimage).placeholder(R.drawable.profile_image).into(userprolifeimage);
                    userprofilename.setText(username);
                    userprofilestatus.setText(userstatus);
                    managechatrequest();
                }
                else {

                    String userstatus = dataSnapshot.child("status").getValue().toString();
                    String username = dataSnapshot.child("name").getValue().toString();

                    userprofilename.setText(username);
                    userprofilestatus.setText(userstatus);
                    managechatrequest();

                }


                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void managechatrequest() {
        chatrequestref.child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(reciveruserid)){
                    String request_type=dataSnapshot.child(reciveruserid).child("request_type").getValue().toString();
                    if(request_type.equals("sent")){
                        currentstate="request_sent";
                        sendmessagerequest.setText("cancel chat request");
                    }
                    else if(request_type.equals("recieved")) {
                        currentstate ="request_recieved";
                        sendmessagerequest.setText("accept chat request");
                        declinemessagerequestButton.setVisibility(View.VISIBLE);
                        declinemessagerequestButton.setEnabled(true);
                        declinemessagerequestButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelchatrequest();
                            }
                        });
                    }

                }else {
                    contactref.child(currentuserid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(reciveruserid)) {
                                currentstate="friends" ;
                                sendmessagerequest.setText("remove user");
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (!currentuserid.equals(reciveruserid)){
            sendmessagerequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendmessagerequest.setEnabled(false);
                    if (currentstate.equals("new")){
                        sendchatrequest();
                    }
                    if (currentstate.equals("request_sent")){
                        cancelchatrequest();
                    }
                    if (currentstate.equals("request_recieved")){
                        Acceptchatrequest();
                    }
                    if (currentstate.equals("friends")){
                        removespecifiquecpntact();
                    }

                }
            });

        }else{
            sendmessagerequest.setVisibility(View.INVISIBLE);
        }


        }

    private void removespecifiquecpntact() {
        contactref.child(currentuserid).child(reciveruserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    contactref.child(reciveruserid).child(currentuserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                sendmessagerequest.setEnabled(true);
                                currentstate="new";
                                sendmessagerequest.setText("send message");
                                declinemessagerequestButton.setVisibility(View.INVISIBLE);
                                declinemessagerequestButton.setEnabled(false);

                            }

                        }
                    });

                }

            }
        });
    }

    private void Acceptchatrequest() {
        contactref.child(currentuserid).child(reciveruserid).child("contact").setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            contactref.child(reciveruserid).child(currentuserid).child("contact").setValue("saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                chatrequestref.child(currentuserid).child(reciveruserid)
                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            chatrequestref.child(reciveruserid).child(currentuserid)
                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    sendmessagerequest.setEnabled(true);
                                                                    currentstate="friends";
                                                                    sendmessagerequest.setText("Remove this contact");
                                                                    declinemessagerequestButton.setVisibility(View.INVISIBLE);
                                                                    declinemessagerequestButton.setEnabled(false);

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

    private void cancelchatrequest() {
        chatrequestref.child(currentuserid).child(reciveruserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    chatrequestref.child(reciveruserid).child(currentuserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                sendmessagerequest.setEnabled(true);
                                currentstate="new";
                                sendmessagerequest.setText("send message");
                                declinemessagerequestButton.setVisibility(View.INVISIBLE);
                                declinemessagerequestButton.setEnabled(false);

                            }

                        }
                    });

                }

            }
        });
    }

    private void sendchatrequest() {
        chatrequestref.child(currentuserid).child(reciveruserid).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            chatrequestref.child(reciveruserid).child(currentuserid).child("request_type")
                                    .setValue("recieved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        sendmessagerequest.setEnabled(true);
                                        currentstate="request_sent";
                                        sendmessagerequest.setText("cancel chat request");
                                    }

                                }
                            });


                        }


                        }
                });
    }


}
