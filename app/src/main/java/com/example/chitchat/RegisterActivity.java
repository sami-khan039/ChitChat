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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private Button btnRegisterwithEmail;
    private EditText RegisteremailInput,RegisterpasswordInput;
    private TextView AlreadyHaveAnAccountLink;
    private ProgressDialog ProgressBar;
    private String Device_Token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        AlreadyHaveAnAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { SendUserToLoginActivity(); }});
        btnRegisterwithEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount(); }});
    }

    private void CreateNewAccount() {
        String email=RegisteremailInput.getText().toString();
        String password=RegisterpasswordInput.getText().toString();
        if(email.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Please enter the email", Toast.LENGTH_SHORT).show();
        }
        if(password.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Please enter the password", Toast.LENGTH_SHORT).show();
        }
        else{
            ProgressBar.setTitle("Creating Account");
            ProgressBar.setMessage("please wait while we are Creating a new Account for you ");
            ProgressBar.setCanceledOnTouchOutside(false);
            ProgressBar.show();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Device_Token=FirebaseInstanceId.getInstance().getToken();
                     DatabaseReference tr=FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Device_Token");
                     tr.setValue(Device_Token).addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if(task.isSuccessful()){

                                 SendUserToMainActivity();
                                 Toast.makeText(RegisterActivity.this, "Account is created Succesfully", Toast.LENGTH_SHORT).show();
                                 ProgressBar.dismiss();
                             }

                         }
                     });

                }
                else{
                    String ErrorMessage=task.getException().toString();
                    Toast.makeText(RegisterActivity.this, "Error :"+ErrorMessage, Toast.LENGTH_SHORT).show();
                    ProgressBar.dismiss();
                }
            }
        });
        }
    }

    private void SendUserToMainActivity() {
        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void SendUserToLoginActivity() {
        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
    }
    private void init() {
        ProgressBar=new ProgressDialog(RegisterActivity.this);
        btnRegisterwithEmail=(Button) findViewById(R.id.btnRegisterEmail);
        RegisteremailInput=(EditText) findViewById(R.id.email_input_Register);
        RegisterpasswordInput=(EditText) findViewById(R.id.pasword_input_Register);
        AlreadyHaveAnAccountLink=(TextView) findViewById(R.id.login_activity_link);

    }
}