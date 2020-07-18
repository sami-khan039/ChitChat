package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
private Toolbar mToolbar;
private TabLayout tabs;
private ViewPager vp;
private TabsAccessAdapter FAdapter;
private FirebaseUser CurrentUser= FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar =(Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ChitChat");
        tabs=(TabLayout) findViewById(R.id.tab_layout);
        vp=(ViewPager) findViewById(R.id.viewpager);
        FAdapter= new TabsAccessAdapter(getSupportFragmentManager());

       vp.setAdapter(FAdapter);
       tabs.setupWithViewPager(vp);






    }


    @Override
    protected void onStart() {
        super.onStart();
        if(CurrentUser==null) {
            SendUserToLoginActivity();
        }
        else{
            verifyUserExistance();
        }
    }

    private void verifyUserExistance() {
        DatabaseReference DB = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUser.getUid());
        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Name").exists()) {

                }
                else{
                    SendUserToSettingActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void SendUserToLoginActivity() {
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.menu,menu);
         return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);
         if(item.getItemId()==R.id.FindUsersActivityLink){
             SendUserToFindFriendsActivity();
         }
         if(item.getItemId()==R.id.SettingsActivityLink){
          SendUserToSettingsActivity();
         }
         if(item.getItemId()==R.id.LogoutLink){
             FirebaseAuth.getInstance().signOut();
             SendUserToLoginActivity();
         }
         if(item.getItemId()==R.id.CreateGroupActivityLink){
             CreateGroupRequest();
         }
         return true;
    }

    private void SendUserToFindFriendsActivity() {
        Intent intent=new Intent(MainActivity.this,FindFirendsActivity.class);
        startActivity(intent);
    }

    private void CreateGroupRequest() {
        final EditText name=new EditText(MainActivity.this);
        AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        dialog.setTitle("Enter the Group Name");
        dialog.setView(name);
        dialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String Gname=name.getText().toString();
                if(name.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "you Must Enter Group Name Necessarily !", Toast.LENGTH_SHORT).show();
                }
                else{
                    CreateGroup(Gname);
                }
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void CreateGroup(final String gname) {
        DatabaseReference DB=FirebaseDatabase.getInstance().getReference().child("Groups").child(gname);
        DB.setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, gname+" Group Created Succesfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    String Error=task.getException().toString();
                    Toast.makeText(MainActivity.this, "Error :"+Error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SendUserToSettingActivity() {
        Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void SendUserToSettingsActivity() {
        Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(intent);
    }
}