package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private Button createAccountButton;
    private EditText userEmail, userPassword ;
    private TextView alreadyHaveaccount ;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar ;
    private DatabaseReference RouteRef ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth =FirebaseAuth.getInstance();
        RouteRef = FirebaseDatabase.getInstance().getReference();
        InitializeFields();
        alreadyHaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserTologinActivity();

            }
        });
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Createnewacount();

            }
        });


    }
    private void Createnewacount() {
        String Email= userEmail.getText().toString();
        String password = userPassword.getText().toString();
        if(TextUtils.isEmpty(Email)){
            Toast.makeText(this, " Please enter email", Toast.LENGTH_SHORT).show();
            }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, " Please enter password", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("creating new account");
            loadingBar.setMessage("please wait while we are creating new account");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(Email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String currentuserID = mAuth.getCurrentUser().getUid();
                                RouteRef.child("User").child(currentuserID).setValue("");
                                sendUserToMainActivity();
                                Toast.makeText(RegisterActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else{
                                String message= task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                        }
                    });

            }

        }



    private void InitializeFields()
    {
        createAccountButton=(Button)findViewById(R.id.register_button);
        userEmail=(EditText)findViewById(R.id.register_email);
        userPassword=(EditText)findViewById(R.id.register_password);
        alreadyHaveaccount=(TextView)findViewById(R.id.haveanaccount);
        loadingBar= new ProgressDialog(this);

    }
    private void sendUserTologinActivity() {
        Intent Logintent=new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(Logintent);
    }
    private void sendUserToMainActivity() {
        Intent Mainintent=new Intent(RegisterActivity.this,MainActivity.class);
        Mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(Mainintent);
        finish();
    }


}
