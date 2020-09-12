package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    private Toolbar mtoolbar ;
    private ViewPager myViewpager ;
    private TabLayout tablelayout;
    private AccessorAdapter myAccessoradapter;
    private FirebaseUser currentuser ;
    private FirebaseAuth mAuth;
    private DatabaseReference RouteRef ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth= FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser();
        mtoolbar =(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Kalamni");
        myViewpager=(ViewPager)findViewById(R.id.main_tabs_pager);
        myAccessoradapter = new AccessorAdapter(getSupportFragmentManager());
        myViewpager.setAdapter(myAccessoradapter);
        tablelayout =(TabLayout)findViewById(R.id.main_tabs);
        tablelayout.setupWithViewPager(myViewpager);
        RouteRef = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (currentuser == null){
            sendUserTologinactivity();

        }
        else {
            verifyUserExistance();
        }

        }

    private void verifyUserExistance() {
        String currentuserid=mAuth.getCurrentUser().getUid();
        RouteRef.child("User").child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").exists()){
                    Toast.makeText(MainActivity.this, "walcome", Toast.LENGTH_SHORT).show();
                }
                else{
                    sendUserToSettingsctivity();
                }


                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendUserTologinactivity() {
        Intent loginintent=new Intent(MainActivity.this,LoginActivity.class);
        loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginintent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
       getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.main_logout){
            mAuth.signOut();
            sendUserTologinactivity();

        }
        if(item.getItemId() == R.id.main_friends){
            sendUserToFriendsctivity();

        }
        if(item.getItemId() == R.id.createGroup){

            RequestNewgroup();
        }
        if(item.getItemId() == R.id.main_settings)
        {
            sendUserToSettingsctivity();

        }
            return true;
    }
    private void RequestNewgroup(){
        AlertDialog.Builder builder =new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("enter group name");
        final EditText groupnamefield =new EditText(MainActivity.this);
        groupnamefield.setHint("e.g coding");
        builder.setView(groupnamefield);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupnamefield.getText().toString();
                if(TextUtils.isEmpty(groupName)){
                    Toast.makeText(MainActivity.this, "please write group name ", Toast.LENGTH_SHORT).show();

                }
                else {
                    CreateNewGroup(groupName);

                }


                }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        builder.show();

    }
    private void CreateNewGroup(final String groupName){
        RouteRef.child("groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, groupName+"is created", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
    private void sendUserToSettingsctivity(){
        Intent settingsintent=new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(settingsintent);

    }
    private void sendUserToFriendsctivity(){
        Intent friendintent = new Intent(MainActivity.this,findFriendsActivity.class);
        startActivity(friendintent);

    }
}
