package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatActivity extends AppCompatActivity {
    private String receivedname ,receivedid , receivedimage , messagesenderid;
    private TextView retlastssen , retname ;
    private CircleImageView retimage ;
    private Toolbar chattolbar ;
    private ImageButton sendchatbtn ;
    private EditText messageinput ;
    private FirebaseAuth mAuth;
    DatabaseReference rootrf ;
    private final List<message> messageslist =new ArrayList<>();
    private LinearLayoutManager LinearLayoutManager ;
    private messageAdapter messageAdapter ;
    private RecyclerView usermessagelist ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initializer() ;
        rootrf = FirebaseDatabase.getInstance().getReference();
        receivedname= getIntent().getExtras().get("visited_user_name").toString();
        receivedid = getIntent().getExtras().get("visited_user_id").toString();
        //receivedimage = getIntent().getExtras().get("visited_image").toString();

        //Picasso.get().load(receivedimage).placeholder(R.drawable.profile_image).into(retimage);

        retlastssen.setText("seen");
        retname.setText(receivedname);

        mAuth=FirebaseAuth.getInstance();
        messagesenderid = mAuth.getCurrentUser().getUid();


        sendchatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmessage();

            }
        });

    }

    private void initializer() {

        chattolbar =(Toolbar)findViewById(R.id.chatacticitylay);
        setSupportActionBar(chattolbar);
        ActionBar actionbar= getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutinflate =(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbarview =layoutinflate.inflate(R.layout.custom_chat_bar, null);
        actionbar.setCustomView(actionbarview);
        //retimage =(CircleImageView)findViewById(R.id.custom_image);
        retname=(TextView)findViewById(R.id.customname);
        retlastssen=(TextView)findViewById(R.id.customlastseen);
        sendchatbtn = (ImageButton) findViewById(R.id.sendmsgchat);
        messageinput =(EditText)findViewById(R.id.inputmessage);
        messageAdapter = new messageAdapter(messageslist);
        usermessagelist =(RecyclerView)findViewById(R.id.messagelistofusers);
        LinearLayoutManager =new LinearLayoutManager(this);
        usermessagelist.setLayoutManager(LinearLayoutManager);
        usermessagelist.setAdapter(messageAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        rootrf.child("message").child(messagesenderid).child(receivedid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                message messages= dataSnapshot.getValue(message.class);
                messageslist.add(messages);
                messageAdapter.notifyDataSetChanged();
                usermessagelist.smoothScrollToPosition(usermessagelist.getAdapter().getItemCount());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendmessage() {
        String messagetext= messageinput.getText().toString();
        if(TextUtils.isEmpty(messagetext))
        {
            Toast.makeText(this, "please write message", Toast.LENGTH_SHORT).show();
        }
        else {
            String senderidref="message/"+ messagesenderid + "/" + receivedid;
            String recieveridref="message/"+ receivedid + "/" + messagesenderid;
            DatabaseReference usermessagekeyref = rootrf.child("message").child(messagesenderid).child(receivedid).push();
            String messagepushkey = usermessagekeyref.getKey();
            Map messagetextbody = new HashMap();
            messagetextbody.put("messages",messagetext);
            messagetextbody.put("type","text");
            messagetextbody.put("from",messagesenderid);

            Map messagetextbodydetails = new HashMap();
            messagetextbodydetails.put(senderidref+"/"+messagepushkey,messagetextbody);
            messagetextbodydetails.put(recieveridref+"/"+messagepushkey,messagetextbody);
            rootrf.updateChildren(messagetextbodydetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(chatActivity.this, "message sent", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(chatActivity.this, "Error", Toast.LENGTH_SHORT).show();

                    }
                    messageinput.setText("");


                    }
            });





        }


        }

    }
