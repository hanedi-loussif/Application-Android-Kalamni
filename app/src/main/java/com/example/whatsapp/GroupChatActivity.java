package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {
    private Toolbar mtoolbar ;
    private ImageButton sendMessage ;
    private EditText UserMessage ;
    private ScrollView mscrollView ;
    private TextView displayMessage ;
    private String currentGroupname , currentuserID, currentuserName ,currentdate ,currentTime ;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, groupnaameRef,GroupMessageKeyRef ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        currentGroupname = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupChatActivity.this,currentGroupname, Toast.LENGTH_SHORT).show();

        mAuth= FirebaseAuth.getInstance();
        currentuserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("User");
        groupnaameRef=FirebaseDatabase.getInstance().getReference().child("groups").child(currentGroupname);


        InitializeFields();
        getuserInfo();
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savemsgTodatabase();
                UserMessage.setText("");
                mscrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });
    }

    private void savemsgTodatabase() {
        String message= UserMessage.getText().toString();
        String messagekey= groupnaameRef.push().getKey();
        if(TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar calfordate =Calendar.getInstance();
            SimpleDateFormat currentdateFormat = new SimpleDateFormat("MM dd, yyyy");
            currentdate = currentdateFormat.format(calfordate.getTime());

            Calendar calforTime =Calendar.getInstance();
            SimpleDateFormat currenttimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currenttimeFormat.format(calforTime.getTime());

            HashMap<String, Object> groupeMessage = new HashMap<>();
            groupnaameRef.updateChildren(groupeMessage);
            GroupMessageKeyRef =groupnaameRef.child(messagekey);
            HashMap<String, Object> messageinfomap =new HashMap<>();
            messageinfomap.put("name",currentuserName);
            messageinfomap.put("message",message);
            messageinfomap.put("date",currentdate);
            messageinfomap.put("time",currentTime);
            GroupMessageKeyRef.updateChildren(messageinfomap);
        }

        }


    @Override
    protected void onStart() {
        super.onStart();
        groupnaameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Diplaymessages(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Diplaymessages(dataSnapshot);
                }

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

    private void Diplaymessages(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext()){
            String datechat= (String)((DataSnapshot)iterator.next()).getValue();
            String messagechat= (String)((DataSnapshot)iterator.next()).getValue();
            String Namechat= (String)((DataSnapshot)iterator.next()).getValue();
            String timechat= (String)((DataSnapshot)iterator.next()).getValue();
            displayMessage.append(Namechat + ":\n" +messagechat+"\n"+timechat+"      "+datechat+"\n\n");
            mscrollView.fullScroll(ScrollView.FOCUS_DOWN);

        }

        }


    private void getuserInfo() {
        userRef.child(currentuserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentuserName = dataSnapshot.child("name").getValue().toString();
                }


                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void InitializeFields(){
        mtoolbar =(Toolbar)findViewById(R.id.group_chat_bar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(currentGroupname);
        sendMessage =(ImageButton)findViewById(R.id.send_message);
        UserMessage =(EditText)findViewById(R.id.input_group_message);
        displayMessage=(TextView) findViewById(R.id.groupchat_text_display);
        mscrollView =(ScrollView)findViewById(R.id.scroll_view);




    }
}
