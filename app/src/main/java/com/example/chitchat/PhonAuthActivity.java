package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.TimeUnit;

public class PhonAuthActivity extends AppCompatActivity {
private EditText PhonInput,CodeInput;
private Button btnSendVC,btnVerify;
private PhoneAuthProvider.OnVerificationStateChangedCallbacks CBobject;
private String VerifID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phon_auth);

        init();

        btnSendVC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phon=PhonInput.getText().toString();
                if(phon.isEmpty()){
                    Toast.makeText(PhonAuthActivity.this, "Enter the Phone Number Please", Toast.LENGTH_SHORT).show();
                }
                else{
                    PhonInput.setVisibility(View.INVISIBLE);
                    btnSendVC.setVisibility(View.INVISIBLE);
                    CodeInput.setVisibility(View.VISIBLE);
                    btnVerify.setVisibility(View.VISIBLE);

                     verifyPhonNumber();
                }
            }
        });

        CBobject=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                SignInWithCredentials(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(PhonAuthActivity.this,"Error"+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                VerifID=s;

            }
        };
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInWithCode();
            }
        });
    }

    private void verifyPhonNumber() {
      PhoneAuthProvider.getInstance().verifyPhoneNumber(PhonInput.getText().toString(),60, TimeUnit.SECONDS,PhonAuthActivity.this,CBobject);

    }

    private void SignInWithCode() {
        PhoneAuthCredential credentials=PhoneAuthProvider.getCredential(VerifID,CodeInput.getText().toString());
        SignInWithCredentials(credentials);
    }

    private void SignInWithCredentials(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(PhonAuthActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
           String dtv= FirebaseInstanceId.getInstance().getToken();
                    DatabaseReference tr= FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Device_Token");
                    tr.setValue(dtv).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                SendUserToMainActivity();
                            }

                        }
                    });


                }
            }
        });
    }

    private void SendUserToMainActivity() {
        Intent intent=new Intent(PhonAuthActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void init() {
        btnSendVC=(Button) findViewById(R.id.btnSendVerificationCode);
        btnVerify=(Button) findViewById(R.id.btnVerifyAccount);
        PhonInput=(EditText) findViewById(R.id.PhonNumberInput);
        CodeInput=(EditText) findViewById(R.id.VerificationCodeInput);
    }
}