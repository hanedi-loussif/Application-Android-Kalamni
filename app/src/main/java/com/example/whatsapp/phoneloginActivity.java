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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class phoneloginActivity extends AppCompatActivity {
    private Button sendverificationcode , verifybutton ;
    private EditText inputphonenumber, inputverificationcode ;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks ;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken ;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonelogin);
        mAuth= FirebaseAuth.getInstance();
        loadingbar = new ProgressDialog(this);
        sendverificationcode = (Button)findViewById(R.id.verification_code_button);
        verifybutton = (Button)findViewById(R.id.verify_button);
        inputphonenumber = (EditText)findViewById(R.id.phone_number_login);
        inputverificationcode =(EditText)findViewById(R.id.verification_login);
        sendverificationcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String phoneNumber= inputphonenumber.getText().toString();
                if(TextUtils.isEmpty(phoneNumber))
                {
                    Toast.makeText(phoneloginActivity.this, "phone number is required ", Toast.LENGTH_SHORT).show();

                }
                else {
                    loadingbar.setTitle("phone verification");
                    loadingbar.setMessage("please wait ,we are authenticating your phone");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            phoneloginActivity.this,               // Activity (for callback binding)
                            callbacks);
                }

                }
        });
        callbacks =new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
                sendverificationcode.setVisibility(View.VISIBLE);
                inputphonenumber.setVisibility(View.VISIBLE);
                verifybutton.setVisibility(View.INVISIBLE);
                inputverificationcode.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                loadingbar.dismiss();
                Toast.makeText(phoneloginActivity.this, "invalid phone number, enter correct number", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent( String verificationId,
                                    PhoneAuthProvider.ForceResendingToken token) {



                // Save verification ID and resending token so we can use them later
                loadingbar.dismiss();
                mVerificationId = verificationId;
                mResendToken = token;
                Toast.makeText(phoneloginActivity.this, "code has been sent", Toast.LENGTH_SHORT).show();
                sendverificationcode.setVisibility(View.INVISIBLE);
                inputphonenumber.setVisibility(View.INVISIBLE);
                verifybutton.setVisibility(View.VISIBLE);
                inputverificationcode.setVisibility(View.VISIBLE);

                // ...
            }
        };
        verifybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendverificationcode.setVisibility(View.INVISIBLE);
                inputphonenumber.setVisibility(View.INVISIBLE);
                String verficationcode = inputverificationcode.getText().toString();
                if(TextUtils.isEmpty(verficationcode)) {
                    Toast.makeText(phoneloginActivity.this, "write code first ", Toast.LENGTH_SHORT).show();

                }
                else {
                    loadingbar.setTitle(" verification code");
                    loadingbar.setMessage("please wait ,we are verifying your code");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verficationcode);
                    signInWithPhoneAuthCredential(credential);

                }


                }
        });
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingbar.dismiss();
                            Toast.makeText(phoneloginActivity.this, "you are logged in ", Toast.LENGTH_SHORT).show();
                            sendUsertothemainactivity();
                        } else {
                            String errormessage=task.getException().toString();
                            Toast.makeText(phoneloginActivity.this, "Error: "+errormessage, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void sendUsertothemainactivity() {
        Intent mainintent = new Intent(phoneloginActivity.this,MainActivity.class);
        startActivity(mainintent);
        finish();
    }

}
