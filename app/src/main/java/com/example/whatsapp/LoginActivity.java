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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private FirebaseUser currentuser ;
    private Button  loginButton , phoneloginButton ;
    private EditText userEmail , userPassword ;
    private TextView needNewaccount , forgetPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference adminref ;
    private ProgressDialog loadingBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        adminref = FirebaseDatabase.getInstance().getReference().child("admin");

        InitializeFields();
        needNewaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegisterctivity();

            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AllowUserToLogin();
            }
        });
        phoneloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneloginIntent = new Intent(LoginActivity.this,phoneloginActivity.class);
                startActivity(phoneloginIntent);
            }
        });

    }

    private void AllowUserToLogin(){
        final String Email= userEmail.getText().toString();
        final String password = userPassword.getText().toString();
        if(TextUtils.isEmpty(Email)) {
            Toast.makeText(this, " Please enter email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, " Please enter password", Toast.LENGTH_SHORT).show();
        }
        else if (Email.equals("admin@gmail.com")){
            sendUserToadmiarctivity();
            Toast.makeText(LoginActivity.this, " logged in successful", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();

        }


        else  {
            loadingBar.setTitle("sign in");
            loadingBar.setMessage("please wait....");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(Email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {

                                sendUserToMainActivity();
                                Toast.makeText(LoginActivity.this, " logged in successful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();


                            }
                            else{
                                String message= task.getException().toString();
                                Toast.makeText(LoginActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                            }
                    });
        }



    }


        private void InitializeFields() {
        loginButton=(Button)findViewById(R.id.log_button);
        phoneloginButton=(Button)findViewById(R.id.phonelogin);
        userEmail=(EditText)findViewById(R.id.login_email);
        userPassword=(EditText)findViewById(R.id.login_password);
        needNewaccount=(TextView)findViewById(R.id.haveaccount);
        forgetPassword=(TextView)findViewById(R.id.forgetpassword);
        loadingBar= new ProgressDialog(this);

    }




        private void sendUserToMainActivity()
    {
        Intent Mainintent=new Intent(LoginActivity.this,MainActivity.class);
        Mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(Mainintent);
        finish();
    }
    private void sendUserToRegisterctivity() {
        Intent registerintent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerintent);
    }
    private void sendUserToadmiarctivity() {
        Intent adminintent=new Intent(LoginActivity.this,adminActivity.class);
        startActivity(adminintent);
    }
}
