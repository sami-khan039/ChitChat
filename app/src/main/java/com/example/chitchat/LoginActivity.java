package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
private Button btnLoginwithEmail,btnLoginwithPhon;
private EditText LoginemailInput,LoginpasswordInput;
private TextView ForgotPasswordLink,CreateNewAccountLink;
private ProgressDialog loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
        CreateNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { SendUserToRegistrationActivity(); }});
        btnLoginwithEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              LoginUser();
            }
        });
        btnLoginwithPhon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToPhonAuthActivity();
            }
        });
    }

    private void SendUserToPhonAuthActivity() {
        Intent intent=new Intent(LoginActivity.this,PhonAuthActivity.class);
        startActivity(intent);
    }

    private void LoginUser() {
        String email=LoginemailInput.getText().toString();
        String password=LoginpasswordInput.getText().toString();
        if(email.isEmpty()){
            Toast.makeText(LoginActivity.this, "Please enter the email", Toast.LENGTH_SHORT).show();
        }
        if(password.isEmpty()){
            Toast.makeText(LoginActivity.this, "Please enter the password", Toast.LENGTH_SHORT).show();
        }
        else{
            loading.setTitle("Signing IN");
            loading.setMessage("please wait while we are logging you in ");
            loading.setCanceledOnTouchOutside(false);
            loading.show();
         FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {
                 if(task.isSuccessful()){
                     String DT= FirebaseInstanceId.getInstance().getToken();
                     DatabaseReference tr= FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Device_Token");
                     tr.setValue(DT).addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if(task.isSuccessful()){

                                 SendUserToMainActivity();
                                 Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                 loading.dismiss();
                             }

                         }
                     });

                 }
                 else{
                     String ErrorMessage=task.getException().toString();
                     Toast.makeText(LoginActivity.this, "Error :"+ErrorMessage, Toast.LENGTH_SHORT).show();
                     loading.dismiss();
                 }
             }
         });

        } }
    private void initialize() {
        loading=new ProgressDialog(LoginActivity.this);
        btnLoginwithEmail=(Button) findViewById(R.id.btnLoginEmail);
        btnLoginwithPhon=(Button) findViewById(R.id.btnloginphon);
        LoginemailInput=(EditText) findViewById(R.id.email_input);
        LoginpasswordInput=(EditText) findViewById(R.id.pasword_input);
        ForgotPasswordLink=(TextView) findViewById(R.id.Forget_password_link);
        CreateNewAccountLink=(TextView)findViewById(R.id.CreatenewAcount_link);
    }
    private void SendUserToMainActivity() {
        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void SendUserToRegistrationActivity() {
        Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
}